package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.plants.Plant
import joshxviii.plantz.entity.Sun
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents

class GenerateSunGoal(
    plantEntity: Plant,
    cooldownTime: Int = 700,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    val sunAmount: Int = 5,
    val generateAtNight: Boolean = false,
): PlantActionGoal(plantEntity, cooldownTime, actionDelay, actionStartEffect, actionEndEffect) {
    override fun canUse(): Boolean = (
        plantEntity.tickCount>cooldownTime &&
        plantEntity.isAlive
    )

    override fun canDoAction(): Boolean = generateAtNight || sunIsVisible()

    override fun doAction() : Boolean {
        val serverLevel = plantEntity.level() as? ServerLevel ?: return false
        Sun.award(serverLevel, plantEntity.position(), if (plantEntity.isBaby) sunAmount/2 else sunAmount )
        plantEntity.playSound(SoundEvents.CHICKEN_EGG, 1.0f, 0.5f)
        return true
    }

    private fun sunIsVisible() : Boolean = (
        plantEntity.level().isBrightOutside &&
        plantEntity.level().canSeeSky(BlockPos.containing(plantEntity.x, plantEntity.eyeY, plantEntity.z))
    )
}