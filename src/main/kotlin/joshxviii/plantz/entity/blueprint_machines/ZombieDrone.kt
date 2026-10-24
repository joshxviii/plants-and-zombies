package joshxviii.plantz.entity.blueprint_machines

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.ai.ZombieState
import joshxviii.plantz.ai.goal.FlyingPathfindingGoal
import joshxviii.plantz.ai.goal.MeleeAttackActionGoal
import joshxviii.plantz.ai.goal.NavigateToTargetGoal
import joshxviii.plantz.entity.zombie.ZombieRobot
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.SpawnGroupData
import net.minecraft.world.entity.ai.control.BodyRotationControl
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState

class ZombieDrone(type: EntityType<out ZombieDrone>, level: Level) : ZombieRobot(type, level) {

    init {
    }

    override fun tick() {
        super.tick()

        if (actionTime>0 || isSwinging) {
            actionAnimation.startIfStopped(tickCount)
            if (actionTime++>20) {
                actionAnimation.stop()
                actionTime=0
            }
        }
    }

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(1, NavigateToTargetGoal(this))
        this.goalSelector.addGoal(2, MeleeAttackActionGoal(
            usingEntity = this,
            actionDelay = 10,
            damageType = DamageTypes.MOB_ATTACK,
            usePredicate = { actionTime<=0 },
            actionStartEffect = { actionTime=1 },
            beforeHitEntityEffect = {

            }
        ))
        this.goalSelector.addGoal(7, FlyingPathfindingGoal(
            this,
            hoverGroundHeight = 2.5
        ))
    }

    override fun createBodyControl(): BodyRotationControl = object : BodyRotationControl(this) {
        override fun clientTick() {
            val drone = this@ZombieDrone
            drone.yBodyRot = Mth.lerp(0.25f, drone.yBodyRot, drone.yHeadRot)
        }
    }

    override fun getDefaultGravity(): Double = 0.0
    override fun checkFallDamage(ya: Double, onGround: Boolean, onState: BlockState, pos: BlockPos) {}

    override fun noLookControl() {}
    override fun clampToGrid(): Boolean = false

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        isFlying = true
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData)
    }
}