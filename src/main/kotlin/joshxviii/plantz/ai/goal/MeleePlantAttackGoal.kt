package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.Plant
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.player.Player

class MeleePlantAttackGoal(
    plantEntity: Plant,
    cooldownTime: Int = 20,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    val attackReach: Double = 1.0,
) : PlantActionGoal(plantEntity, cooldownTime, actionDelay, actionStartEffect, actionEndEffect) {

    override fun canUse(): Boolean = (
        plantEntity.tickCount>cooldownTime
        && plantEntity.target?.isAlive == true
    )

    override fun canDoAction(): Boolean {
        val target = plantEntity.target?: return false
        plantEntity.lookControl.setLookAt(target, 30f, 30f)

        return isInReach(target);
    }

    override fun doAction() {
        val target = plantEntity.target?: return
        if (!isInReach(target)) return

        // TODO make tag list of entities that can not be insta eaten
        if(target !is Player) target.discard()
    }

    private fun isInReach(target: LivingEntity): Boolean {
        return plantEntity.distanceTo(target) <= attackReach && plantEntity.sensing.hasLineOfSight(target)
    }
}