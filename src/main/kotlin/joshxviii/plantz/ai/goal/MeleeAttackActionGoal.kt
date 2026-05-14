package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.OwnableEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.Attributes
import java.util.function.Predicate

open class MeleeAttackActionGoal(
    usingEntity: PathfinderMob,
    cooldownTime: Int = 20,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionSuccessEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    actionPredicate: Predicate<PathfinderMob> = Predicate { true },
    val damageMultiplier: Float = 1.0f,
    val damageType: ResourceKey<DamageType> = PazDamageTypes.PLANT,
    val afterHitEntityEffect: (target: LivingEntity) -> Unit = {},
) : ActionGoal(usingEntity, cooldownTime, actionDelay, actionStartEffect, actionSuccessEffect, actionEndEffect, actionPredicate) {

    override fun canUse(): Boolean = (
        actionPredicate.test(usingEntity)
            && usingEntity.tickCount>cooldownTime
            && usingEntity.target?.isAlive == true
            && !(usingEntity is Plant && (usingEntity.isAsleep || usingEntity.isGrowingSeeds))
    )

    override fun canDoAction(): Boolean {
        val target = usingEntity.target?: return false
        usingEntity.lookControl.setLookAt(target, 30f, 30f)

        return isReachable(target);
    }

    override fun doAction() : Boolean {
        val target = usingEntity.target?: return false
        if (!isReachable(target)) return false

        val damage : Float = usingEntity.attributes.getValue(Attributes.ATTACK_DAMAGE).toFloat() * damageMultiplier
        val knockback : Double = usingEntity.attributes.getValue(Attributes.ATTACK_KNOCKBACK)
        val source = usingEntity.damageSources().source(damageType, usingEntity,
            if (PazConfig.PLAYER_CREDIT_FOR_PLANT_KILLS && usingEntity is OwnableEntity) usingEntity.rootOwner else usingEntity)

        if (target.hurtServer(usingEntity.level() as ServerLevel, source, damage)) {
            target.knockback(
                knockback,
                usingEntity.x - target.x,
                usingEntity.z - target.z
            )
            afterHitEntityEffect(target)
            return true
        }

        return false
    }

    fun isReachable(target: LivingEntity): Boolean {
        val range = usingEntity.attributes.let { if (it.hasAttribute(Attributes.ENTITY_INTERACTION_RANGE)) it.getValue(Attributes.ENTITY_INTERACTION_RANGE).toFloat() else 2f }
        return usingEntity.distanceToSqr(target) <= range * range
    }
}