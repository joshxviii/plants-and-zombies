package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes

open class MeleeAttackPlantGoal(
    plantEntity: Plant,
    cooldownTime: Int = 20,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    val attackReach : Double = 5.0,
    val damageType: ResourceKey<DamageType> = PazDamageTypes.PLANT
) : PlantActionGoal(plantEntity, cooldownTime, actionDelay, actionStartEffect, actionEndEffect) {

    override fun canUse(): Boolean = (
        plantEntity.tickCount>cooldownTime
            && plantEntity.target?.isAlive == true
            && !plantEntity.isAsleep
    )

    override fun canDoAction(): Boolean {
        val target = plantEntity.target?: return false
        plantEntity.lookControl.setLookAt(target, 30f, 30f)

        return isInReach(target);
    }

    override fun doAction() : Boolean {
        val target = plantEntity.target?: return false
        if (!isInReach(target)) return false

        val damage : Float = plantEntity.attributes.getValue(Attributes.ATTACK_DAMAGE).toFloat()
        val knockback : Double = plantEntity.attributes.getValue(Attributes.ATTACK_KNOCKBACK)
        val source = plantEntity.damageSources().source(damageType, plantEntity)

        if (target.hurtServer(plantEntity.level() as ServerLevel, source, damage)) {
            target.knockback(
                knockback,
                plantEntity.x - target.x,
                plantEntity.z - target.z
            )
            return true
        }

        return false
    }

    private fun isInReach(target: LivingEntity): Boolean {
        return plantEntity.boundingBox.inflate(attackReach).intersects(target.boundingBox) && plantEntity.sensing.hasLineOfSight(target)
    }
}