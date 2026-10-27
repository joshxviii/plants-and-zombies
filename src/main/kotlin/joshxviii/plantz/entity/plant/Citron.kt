package joshxviii.plantz.entity.plant

import joshxviii.plantz.BeamParticleOptions
import joshxviii.plantz.NukeSmokeParticleOptions
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.ai.goal.BeamAttackGoal
import joshxviii.plantz.ai.goal.NavigateToTargetGoal
import joshxviii.plantz.pazResource
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import org.joml.Quaternionf

class Citron(type: EntityType<out Plant>, level: Level) : Plant(type, level), RollingPlant {
    companion object {
        val BALL_TRANSFORMATION: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(Citron::class.java, EntityDataSerializers.BOOLEAN)
        val TRANSFORMED_ID: Identifier = pazResource("transformed")

        private val SPEED_MODIFIER = AttributeModifier(TRANSFORMED_ID, 0.19, AttributeModifier.Operation.ADD_VALUE)
        private val STEP_HEIGHT_MODIFIER = AttributeModifier(TRANSFORMED_ID, 1.0, AttributeModifier.Operation.ADD_VALUE)

        const val BEAM_COLOR = 0xEE9933
    }

    override var rollRotation: Quaternionf = Quaternionf()

    var isTransformed: Boolean
        get() = this.entityData.get(BALL_TRANSFORMATION)
        set(value) {
            this.entityData.set(BALL_TRANSFORMATION, value)
            reassessNavigation()
        }

    val transformAnimation : AnimationState = AnimationState()

    val walkGoal = NavigateToTargetGoal(this)
    val ballGoal = NavigateToTargetGoal(
        this,
        keepAwayDistance = 3.0,
        alwaysFaceTarget = false
    )

    override fun isRolling(): Boolean = isTransformed
    override fun canWalk(): Boolean = true

    override fun registerGoals() {
        super.registerGoals()

        goalSelector.addGoal(4, BeamAttackGoal(
            this,
            beamRange = 32.0,
            beamWidth = 1.5,
            actionDelay = 13,
            doNotExtendPastTarget = false,
            damageType = PazDamageTypes.ENERGY,
            damageMultiplier = 1.0f,
            particleFactory = { startPos, endPos ->
                val laserStart = calculateUpVector(this.xRot + 95, this.yHeadRot + 25).scale(0.9).add(startPos)
                (level() as ServerLevel).sendParticles(
                    BeamParticleOptions(endPos.offsetRandom(random, .25f),
                        color = BEAM_COLOR, width = 0.2f, lifeTime = 12),
                    true, true,
                    laserStart.x, laserStart.y, laserStart.z,
                    1, 0.0, 0.0, 0.0, 0.0
                )
                (level() as ServerLevel).sendParticles(
                    NukeSmokeParticleOptions(color = BEAM_COLOR, scale = 0.1f),
                    endPos.x, endPos.y, endPos.z,
                    3, 0.0, 0.0, 0.0, 0.0
                )
                (level() as ServerLevel).sendParticles(
                    PazServerParticles.POP,
                    endPos.x, endPos.y, endPos.z,
                    6, 0.1, 0.1, 0.1, 0.0
                )
            },
            afterHitEntityEffect = {}
        ))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && (target is Zombie
                    || (target is Enemy && isTame)
                    || (target is Player && !isTame))
        })
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(BALL_TRANSFORMATION, false)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.putBoolean("is_transformed", isTransformed)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        isTransformed = input.getBooleanOr("is_transformed", false)
        reassessNavigation()
    }

    fun reassessNavigation() {
        transformAnimation.start(tickCount)
        goalSelector.removeGoal(walkGoal)
        goalSelector.removeGoal(ballGoal)
        if (isTransformed) {
            getAttribute(Attributes.MOVEMENT_SPEED)!!.let { if (!it.hasModifier(TRANSFORMED_ID)) it.addTransientModifier(
                SPEED_MODIFIER
            ) }
            getAttribute(Attributes.STEP_HEIGHT)!!.let { if (!it.hasModifier(TRANSFORMED_ID)) it.addTransientModifier(
                STEP_HEIGHT_MODIFIER
            ) }

            goalSelector.addGoal(3, ballGoal)
        }
        else {
            getAttribute(Attributes.MOVEMENT_SPEED)!!.let { if (it.hasModifier(TRANSFORMED_ID)) it.removeModifier(
                SPEED_MODIFIER
            ) }
            getAttribute(Attributes.STEP_HEIGHT)!!.let { if (it.hasModifier(TRANSFORMED_ID)) it.removeModifier(
                STEP_HEIGHT_MODIFIER
            ) }

            goalSelector.addGoal(3, walkGoal)
        }
    }

    override fun onSyncedDataUpdated(accessor: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(accessor)
        if (accessor == BALL_TRANSFORMATION) reassessNavigation()
    }

}