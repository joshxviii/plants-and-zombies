package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.entity.Plant
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType

class BeamAttackPlantGoal(
    plantEntity: Plant,
    cooldownTime: Int = 20,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    val attackReach : Double = 5.0,
    val damageType: ResourceKey<DamageType> = PazDamageTypes.PLANT
) : PlantActionGoal(plantEntity, cooldownTime, actionDelay, actionStartEffect, actionEndEffect) {
    override fun canDoAction(): Boolean = (
        plantEntity.tickCount>cooldownTime
        && plantEntity.target?.isAlive == true
    )

    override fun doAction(): Boolean {
        TODO("Not yet implemented")
    }

    override fun canUse(): Boolean {
        TODO("Not yet implemented")
    }

}