package joshxviii.plantz.entity.plant

import joshxviii.plantz.*
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.ai.goal.MeleeAttackActionGoal
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.SpawnGroupData
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

class BonkChoy(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.BONK_CHOY, level) {

    companion object {
        val USE_UPPERCUT: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(BonkChoy::class.java, EntityDataSerializers.BOOLEAN)
    }

    // normal attack
    val punchGoal = MeleeAttackActionGoal(
        usingEntity = this,
        actionDelay = 7,
        cooldownTime = 20,
    )

    // uppercut
    val uppercutGoal = MeleeAttackActionGoal(
        usingEntity = this,
        actionDelay = 18,
        cooldownTime = 60,
        damageMultiplier = 2.0f,
        actionSuccessEffect = {
            //TODO custom sounds
            playSound(SoundEvents.WIND_CHARGE_BURST.value(), 2.0f, 0.8f)
        },
        afterHitEntityEffect = {
            val lookDirection = calculateViewVector(-45f, yHeadRot)
            it.applyImpulse(lookDirection.x, lookDirection.y, lookDirection.z, 1.25f, 0.5f)
        }
    )

    var useUppercut: Boolean
        get() = this.entityData.get(USE_UPPERCUT)
        set(value) = this.entityData.set(USE_UPPERCUT, value)

    init {
        reassessAttack()
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(USE_UPPERCUT, false)
    }

    override fun registerGoals() {
        super.registerGoals()
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && target !is Creeper
                    && (target is Zombie
                    || (target is Enemy && isTame)
                    || (target is Player && !isTame))
        })
    }

    fun reassessAttack() {
        goalSelector.removeGoal(punchGoal)
        goalSelector.removeGoal(uppercutGoal)
        if (useUppercut)
            goalSelector.addGoal(1, uppercutGoal)
        else
            goalSelector.addGoal(1, punchGoal)
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        reassessAttack()
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData)
    }

    override fun cooldownFinished() {
        useUppercut = random.nextFloat() < 0.45f
        reassessAttack()
    }
}