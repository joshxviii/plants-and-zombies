package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazTags.EntityTypes.CANNOT_CHOMP
import joshxviii.plantz.ai.goal.MeleeAttackActionGoal
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.applyImpulse
import joshxviii.plantz.clampYRot
import joshxviii.plantz.entity.zombie.Gargantuar.Companion.SMASH_DAMAGE_CALCULATOR
import joshxviii.plantz.hasSameRootOwner
import joshxviii.plantz.knockback
import joshxviii.plantz.pazResource
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.util.random.WeightedList
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class Squash(type: EntityType<out Plant>, level: Level) : Plant(type, level) {

    companion object {
        private val SQUASH_MODIFIER = AttributeModifier(
            pazResource("squash"), 100.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        )
        val SQUASH_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(Squash::class.java, EntityDataSerializers.INT)
    }

    var squashTime: Int
        get() = this.entityData.get(SQUASH_TIME_ID)
        set(value) = this.entityData.set(SQUASH_TIME_ID, value)

    override fun tick() {
        super.tick()
        if (squashTime > 0) {
            squashTime++
            if (squashTime>10 && onGround()) {
                squashTime=0
                squash()
            }
        }
    }

    private fun squash() {
        playSound(SoundEvents.SLIME_BLOCK_BREAK, 1.5f, 0.8f)
        (level() as? ServerLevel)?.let {
            it.sendParticles(
                BlockParticleOption(ParticleTypes.BLOCK, it.getBlockState(blockPosition().below())),
                x, y, z,
                50, 1.2, 0.4, 1.2, 0.2
            )
        }
        val range = 4.5
        val candidates = level().getEntities(this, boundingBox.inflate(range)) { entity ->
            entity is LivingEntity && entity.isAlive && !entity.hasSameRootOwner(this) && owner != entity
        }
        for (entity in candidates) {
            val damage : Float = this.attributes.getValue(Attributes.ATTACK_DAMAGE).toFloat()
            val distance = entity.distanceToSqr(this)
            (level() as? ServerLevel)?.let { level ->
                val drop = (1.0 - (distance / (range*range))).toFloat()
                val dmg = damage * drop
                if(entity.hurtServer(level, damageSources().source(PazDamageTypes.PLANT, this, this), dmg)) {
                    entity.applyImpulse(entity.position().subtract(this.position()).normalize().add(0.0, 0.5, 0.0), pow = drop * 0.75f)
                }
            }
        }
        snapTo(position(), clampYRot(yRot), xRot)
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(2, SquashGoal(this))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                && (target is Zombie
                || (target is Enemy && isTame)
                || (target is Player && !isTame))
        })
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(SQUASH_TIME_ID, 0)
    }

    override fun attackGoals() {}
    override fun canLookAround(): Boolean = false
    override fun canSurviveOn(block: BlockState): Boolean = true
    override fun clampToGrid(): Boolean = squashTime == 0 && super.clampToGrid()

    class SquashGoal(
        val squash: Squash,
    ) : MeleeAttackActionGoal(
        usingEntity = squash,
        cooldownTime = 75,
        actionDelay = 0,
        damageType = PazDamageTypes.PLANT,
        actionStartEffect = {
            squash.target?.let { target ->
                val targetPos = target.position().subtract(squash.position())
                val vel = 1.9f
                ProjectileAttackGoal.calculateMotionArcs(targetPos, squash.gravity, vel.toDouble())?.let {
                    squash.lookAt(target, 360f, 0f)
                    val horizDist = targetPos.horizontalDistance()

                    val horizUnitX = targetPos.x / horizDist
                    val horizUnitZ = targetPos.z / horizDist
                    val horizComp = Mth.cos(it.first) * 5

                    val shootX = (horizUnitX * horizComp)
                    val shootY = Mth.sin(it.first).toDouble()
                    val shootZ = (horizUnitZ * horizComp)
                    squash.squashTime = 1
                    squash.playSound(SoundEvents.SULFUR_CUBE_JUMP)// TODO Custom sounds
                    squash.applyImpulse(shootX, shootY*0.5, shootZ, vel)
                }
            }
        },
        actionPredicate = { squash.squashTime <= 0 }
    ) {
        override fun doAction() : Boolean = true
    }
}