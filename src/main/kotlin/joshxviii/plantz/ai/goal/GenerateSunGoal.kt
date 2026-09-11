package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.Sun
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.PathfinderMob
import java.util.function.Predicate

class GenerateSunGoal(
    usingEntity: Plant,
    cooldownTime: Int = 900,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionSuccessEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    actionPredicate: Predicate<PathfinderMob> = Predicate { true },
    delayedEffectDelay: Int = 0,
    delayedEffect: () -> Unit = {},
    val sunAmount: Int = 1,
    val generatesAtNight : Boolean = false,
): ActionGoal(usingEntity, cooldownTime, actionDelay, actionStartEffect, actionSuccessEffect, actionEndEffect, actionPredicate, delayedEffectDelay, delayedEffect, -10..20) {

    override fun canUse(): Boolean = (
        usingEntity.isAlive
            && !(usingEntity is Plant && (usingEntity.isAsleep || usingEntity.isGrowingSeeds))
    )

    override fun canDoAction(): Boolean = (generatesAtNight || (usingEntity as? Plant)?.sunIsVisible() == true)

    override fun doAction() : Boolean {
        val serverLevel = usingEntity.level() as? ServerLevel ?: return false
        Sun.award(serverLevel, usingEntity.position(), if (usingEntity.isBaby) (sunAmount/2).coerceAtLeast(1) else sunAmount )
        usingEntity.playSound(SoundEvents.CHICKEN_EGG, 1.0f, 0.5f)
        return true
    }
}