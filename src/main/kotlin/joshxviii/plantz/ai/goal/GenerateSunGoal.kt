package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazLootTables
import joshxviii.plantz.entity.Plant
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.level.gameevent.GameEvent

class GenerateSunGoal(
    plantEntity: Plant,
    cooldownTime: Int = 200,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    val generateAtNight: Boolean = false,
): PlantActionGoal(plantEntity, cooldownTime, actionDelay, actionStartEffect, actionEndEffect) {
    override fun canUse(): Boolean = (
        plantEntity.tickCount>cooldownTime &&
        plantEntity.isAlive
    )

    override fun stop() {

    }

    override fun canDoAction(): Boolean = generateAtNight || sunIsVisible()

    override fun doAction() = generateSun()

    private fun sunIsVisible() : Boolean = (
        plantEntity.level().isBrightOutside &&
        plantEntity.level().canSeeSky(BlockPos.containing(plantEntity.x, plantEntity.eyeY, plantEntity.z))
    )

    private fun generateSun() {
        val serverLevel = plantEntity.level() as? ServerLevel ?: return
        val dropped = plantEntity.dropFromGiftLootTable(serverLevel, PazLootTables.SUN_DROP, plantEntity::spawnAtLocation)

        if (dropped) {
            plantEntity.playSound(SoundEvents.CHICKEN_EGG, 1.0f, 0.5f)
            plantEntity.gameEvent(GameEvent.ENTITY_PLACE)
        }
    }
}