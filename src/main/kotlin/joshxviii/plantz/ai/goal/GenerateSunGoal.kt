package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.entity.Sun
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents

class GenerateSunGoal(
    usingEntity: Plant,
    cooldownTime: Int = 700,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    val sunAmount: Int = 5,
    val generatesAtNight : Boolean = false
): PlantActionGoal(usingEntity, cooldownTime, actionDelay, actionStartEffect, actionEndEffect) {
    override fun canUse(): Boolean = (
        usingEntity.tickCount>cooldownTime
            && usingEntity.isAlive
            && !(usingEntity is Plant && usingEntity.isAsleep)
    )

    override fun canDoAction(): Boolean = (generatesAtNight || (usingEntity as? Plant)?.sunIsVisible() == true)

    override fun doAction() : Boolean {
        val serverLevel = usingEntity.level() as? ServerLevel ?: return false
        Sun.award(serverLevel, usingEntity.position(), if (usingEntity.isBaby) sunAmount/2 else sunAmount )
        usingEntity.playSound(SoundEvents.CHICKEN_EGG, 1.0f, 0.5f)
        return true
    }
}