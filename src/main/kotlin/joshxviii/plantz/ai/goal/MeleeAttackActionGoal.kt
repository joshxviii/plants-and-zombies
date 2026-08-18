package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.attackRange
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.OwnableEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.Attributes
import java.util.EnumSet
import java.util.function.Predicate

open class MeleeAttackActionGoal(
    usingEntity: PathfinderMob,
    cooldownTime: Int = 20,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionSuccessEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    actionPredicate: Predicate<PathfinderMob> = Predicate { true },
    delayedEffectDelay: Int = 0,
    delayedEffect: () -> Unit = {},
    val usePredicate: Predicate<PathfinderMob> = actionPredicate,
    val rangeMultiplier: Float = 1.0f,
    val damageMultiplier: Float = 1.0f,
    val damageType: ResourceKey<DamageType> = PazDamageTypes.PLANT,
    val beforeHitEntityEffect: (target: LivingEntity) -> Unit = {},
    val afterHitEntityEffect: (target: LivingEntity) -> Unit = {},
) : ActionGoal(usingEntity, cooldownTime, actionDelay, actionStartEffect, actionSuccessEffect, actionEndEffect, actionPredicate, delayedEffectDelay, delayedEffect) {

    init {
        flags = EnumSet.of(Flag.LOOK)
    }

    override fun canUse(): Boolean = (
        actionPredicate.test(usingEntity)
            && usingEntity.tickCount>cooldownTime
            && usingEntity.target?.isAlive == true
            && !(usingEntity is Plant && (usingEntity.isAsleep || usingEntity.isGrowingSeeds))
    )

    override fun canDoAction(): Boolean {
        val target = usingEntity.target?: return false
        usingEntity.lookControl.setLookAt(target, 30f, 30f)

        return isReachable(target) && usePredicate.test(usingEntity);
    }

    override fun doAction() : Boolean {
        val target = usingEntity.target?: return false
        usingEntity.lookControl.setLookAt(target, 30.0f, 30.0f)
        if (!isReachable(target)) return false

        val damage : Float = usingEntity.attributes.getValue(Attributes.ATTACK_DAMAGE).toFloat() * damageMultiplier
        val knockback : Double = usingEntity.attributes.getValue(Attributes.ATTACK_KNOCKBACK)
        val source = usingEntity.damageSources().source(damageType, usingEntity,
            if (PazConfig.PLAYER_CREDIT_FOR_PLANT_KILLS && usingEntity is OwnableEntity) usingEntity.rootOwner else usingEntity)

        beforeHitEntityEffect(target)
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
        val range = usingEntity.attackRange()
        val distance = usingEntity.distanceToSqr(target)
        return distance <= (range * range) * rangeMultiplier
    }
}