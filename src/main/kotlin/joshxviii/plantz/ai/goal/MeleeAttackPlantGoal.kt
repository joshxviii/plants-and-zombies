package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.Attributes

open class MeleeAttackPlantGoal(
    usingEntity: PathfinderMob,
    cooldownTime: Int = 20,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    val attackReach : Double = 5.0,
    val damageType: ResourceKey<DamageType> = PazDamageTypes.PLANT
) : PlantActionGoal(usingEntity, cooldownTime, actionDelay, actionStartEffect, actionEndEffect) {

    override fun canUse(): Boolean = (
        usingEntity.tickCount>cooldownTime
            && usingEntity.target?.isAlive == true
            && !(usingEntity is Plant && usingEntity.isAsleep)
    )

    override fun canDoAction(): Boolean {
        val target = usingEntity.target?: return false
        usingEntity.lookControl.setLookAt(target, 30f, 30f)

        return isInReach(target);
    }

    override fun doAction() : Boolean {
        val target = usingEntity.target?: return false
        if (!isInReach(target)) return false

        val damage : Float = usingEntity.attributes.getValue(Attributes.ATTACK_DAMAGE).toFloat()
        val knockback : Double = usingEntity.attributes.getValue(Attributes.ATTACK_KNOCKBACK)
        val source = usingEntity.damageSources().source(damageType, usingEntity)

        if (target.hurtServer(usingEntity.level() as ServerLevel, source, damage)) {
            target.knockback(
                knockback,
                usingEntity.x - target.x,
                usingEntity.z - target.z
            )
            return true
        }

        return false
    }

    private fun isInReach(target: LivingEntity): Boolean {
        return usingEntity.boundingBox.inflate(attackReach).intersects(target.boundingBox) && usingEntity.sensing.hasLineOfSight(target)
    }
}