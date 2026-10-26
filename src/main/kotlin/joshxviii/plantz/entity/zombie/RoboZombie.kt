package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazSounds
import joshxviii.plantz.ai.goal.MeleeAttackActionGoal
import joshxviii.plantz.ai.goal.NavigateToTargetGoal
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.Missile
import joshxviii.plantz.pazResource
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class RoboZombie(type: EntityType<out RoboZombie>, level: Level) : PazZombie(type, level) {

    companion object {
        const val MISSILE_COOLDOWN_TIME = 320

        val TANK_TRANSFORMATION: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(RoboZombie::class.java, EntityDataSerializers.BOOLEAN)
        val MISSILE_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(RoboZombie::class.java, EntityDataSerializers.INT)
        val MISSILE_COOLDOWN_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(RoboZombie::class.java, EntityDataSerializers.INT)
        val BASH_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(RoboZombie::class.java, EntityDataSerializers.INT)
        val TRANSFORMED_ID: Identifier = pazResource("transformed")

        private val SPEED_MODIFIER = AttributeModifier(TRANSFORMED_ID, 0.15, AttributeModifier.Operation.ADD_VALUE)
        private val STEP_HEIGHT_MODIFIER = AttributeModifier(TRANSFORMED_ID, 0.0, AttributeModifier.Operation.ADD_VALUE)
    }

    val idleAnimation : AnimationState = AnimationState()
    val shootAnimation : AnimationState = AnimationState()
    val bashAnimation : AnimationState = AnimationState()
    val transformAnimation : AnimationState = AnimationState()

    val walkGoal = NavigateToTargetGoal(this)
    val tankGoal = NavigateToTargetGoal(
        this,
        keepAwayDistance = 3.0,
        alwaysFaceTarget = true
    )

    override fun createNavigation(level: Level): PathNavigation {
        return super.createNavigation(level)
    }

    var treadYaw: Float = 0.0f
    var treadPitch: Float = 0.0f

    var missileCooldown: Int
        get() = this.entityData.get(MISSILE_COOLDOWN_ID)
        set(value) = this.entityData.set(MISSILE_COOLDOWN_ID, value)

    var isTransformed: Boolean
        get() = this.entityData.get(TANK_TRANSFORMATION)
        set(value) {
            this.entityData.set(TANK_TRANSFORMATION, value)
            reassessNavigation()
        }

    var missileTime: Int
        get() = this.entityData.get(MISSILE_TIME_ID)
        set(value) = this.entityData.set(MISSILE_TIME_ID, value)

    var bashTime: Int
        get() = this.entityData.get(BASH_TIME_ID)
        set(value) = this.entityData.set(BASH_TIME_ID, value)

    init {
        //starts the animation at the end so that the robo is "pre-transformed" for idle
        transformAnimation.start(-40)
        xpReward = 30
    }

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.ROBO_ZOMBIE_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.ROBO_ZOMBIE_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return PazSounds.ROBO_ZOMBIE_DEATH
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.COPPER_GOLEM_STEP // TODO: custom sounds
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(TANK_TRANSFORMATION, false)
        entityData.define(MISSILE_COOLDOWN_ID, MISSILE_COOLDOWN_TIME)
        entityData.define(MISSILE_TIME_ID, 0)
        entityData.define(BASH_TIME_ID, 0)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.putBoolean("is_transformed", isTransformed)
        output.putInt("missile_cooldown", missileCooldown)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        isTransformed = input.getBooleanOr("is_transformed", false)
        missileCooldown = input.getIntOr("missile_cooldown", MISSILE_COOLDOWN_TIME)
        reassessNavigation()
    }

    fun reassessNavigation() {
        transformAnimation.start(tickCount)
        goalSelector.removeGoal(walkGoal)
        goalSelector.removeGoal(tankGoal)
        if (isTransformed) {
            getAttribute(Attributes.MOVEMENT_SPEED)!!.let { if (!it.hasModifier(TRANSFORMED_ID)) it.addTransientModifier(SPEED_MODIFIER) }
            getAttribute(Attributes.STEP_HEIGHT)!!.let { if (!it.hasModifier(TRANSFORMED_ID)) it.addTransientModifier(STEP_HEIGHT_MODIFIER) }

            goalSelector.addGoal(3, tankGoal)
        }
        else {
            getAttribute(Attributes.MOVEMENT_SPEED)!!.let { if (it.hasModifier(TRANSFORMED_ID)) it.removeModifier(SPEED_MODIFIER) }
            getAttribute(Attributes.STEP_HEIGHT)!!.let { if (it.hasModifier(TRANSFORMED_ID)) it.removeModifier(STEP_HEIGHT_MODIFIER) }

            goalSelector.addGoal(3, walkGoal)
        }
    }

    override fun onSyncedDataUpdated(accessor: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(accessor)
        if (accessor == TANK_TRANSFORMATION) reassessNavigation()
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(4, MeleeAttackActionGoal(
            usingEntity = this,
            damageType = DamageTypes.MOB_ATTACK,
            actionDelay = 10,
            usePredicate = {
                bashTime<=0 && missileTime<=0
            },
            actionStartEffect = {
                bashTime=1
            }
        ))
        goalSelector.addGoal(4, ProjectileAttackGoal(
            usingEntity = this,
            projectileFactory =  { Missile(level(), this) },
            velocity = 0.9,
            attackRadius = 30f,
            leadShots = false,
            actionDelay = 19,
            soundEvent = PazSounds.ROBO_ZOMBIE_SHOOT,
            usePredicate = {
                missileTime<=0 && missileCooldown<=0
            },
            actionStartEffect = {
                missileTime=1
            },
            actionSuccessEffect = {
                missileCooldown = MISSILE_COOLDOWN_TIME + random.nextIntBetweenInclusive(10, 50)
            }
        ))
    }

    override fun addBehaviourGoals() {
        addBehaviourGoalsNoMelee()
    }

    override fun canEquipBalloons(): Boolean = false
    override fun canEquipDuckyInWater(): Boolean = false
    override fun canPickUpLoot(): Boolean = false
    override fun canClimbWalls(): Boolean = isTransformed || super.canClimbWalls()

    override fun getMoveControl(): MoveControl = if (missileTime>0) noMoveControl else super.getMoveControl()

    override fun tick() {
        super.tick()
        if (health <= maxHealth*.5 && !isTransformed) isTransformed=true
        if (!this.isNoAi) { updateAnimationState() }
        if (missileCooldown>0) --missileCooldown
    }

    fun updateAnimationState() {
        idleAnimation.startIfStopped(0)

        if (missileTime>0) {
            shootAnimation.startIfStopped(tickCount)
            if (missileTime++>60) {
                shootAnimation.stop()
                missileTime=0
            }
        }

        if (bashTime>0 || swinging) {
            bashAnimation.startIfStopped(tickCount)
            if (bashTime++>20) {
                bashAnimation.stop()
                bashTime=0
            }
        }
    }

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        reassessNavigation()
        val data = super.finalizeSpawn(level, difficulty, spawnReason, groupData)

        missileCooldown = MISSILE_COOLDOWN_TIME

        if (spawnReason != EntitySpawnReason.CONVERSION) {
            setCanBreakDoors(true)
        }

        return data
    }
}