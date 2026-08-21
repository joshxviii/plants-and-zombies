package joshxviii.plantz.entity.blueprint_machines

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.ai.ZombieState
import joshxviii.plantz.ai.goal.FlyingPathfindingGoal
import joshxviii.plantz.ai.goal.MeleeAttackActionGoal
import joshxviii.plantz.entity.zombie.ZombieRobot
import net.minecraft.advancements.criterion.DamageSourcePredicate.Builder.damageType
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.control.BodyRotationControl
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class ZombieDrone(type: EntityType<out ZombieDrone>, level: Level) : ZombieRobot(type, level) {

    init {
        state = ZombieState.FLYING
    }

    override fun tick() {
        super.tick()

        if (actionTime>0) {
            actionAnimation.startIfStopped(tickCount)
            if (actionTime++>20) {
                actionAnimation.stop()
                actionTime=0
            }
        }
    }

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(1, FlyingPathfindingGoal(
            this,
            hoverGroundHeight = 2.5
        ))
        this.goalSelector.addGoal(2, MeleeAttackActionGoal(
            usingEntity = this,
            actionDelay = 10,
            damageType = DamageTypes.MOB_ATTACK,
            usePredicate = { actionTime<=0 },
            actionStartEffect = { actionTime=1 },
            beforeHitEntityEffect = {

            }
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
}