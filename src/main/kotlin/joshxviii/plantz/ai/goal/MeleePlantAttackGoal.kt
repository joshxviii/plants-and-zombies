package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.Plant
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.player.Player

class MeleePlantAttackGoal(
    plantEntity: Plant,
    cooldownTime: Int = 20,
    actionDelay: Int = 0,
    val attackReach: Double = 4.0,
    val afterEffect: () -> Unit = {}
) : PlantActionGoal(plantEntity, cooldownTime, actionDelay) {

    override fun canUse(): Boolean {
        return plantEntity.target?.isAlive == true
    }

    override fun canDoAction(): Boolean {
        val target = plantEntity.target?: return false
        plantEntity.lookControl.setLookAt(target, 30f, 30f)

        return plantEntity.distanceToSqr(target) <= attackReach && plantEntity.sensing.hasLineOfSight(target);
    }

    override fun doAction() {
        val target = plantEntity.target?: return
        // TODO make tag list of entities that can not be insta eaten
        if(target !is Player) target.discard()
        afterEffect()
    }
}