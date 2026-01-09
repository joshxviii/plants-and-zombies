package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazLootTables
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.entity.Plant
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.gameevent.GameEvent

class GenerateSunGoal(
    val plantEntity: Plant,
    val generateAtNight: Boolean = false,
    val cooldownTime: Int = 20,
): Goal() {
    override fun canUse(): Boolean {
        TODO("Not yet implemented")
    }

    private fun Plant.tryGenerateSun() {
        if (!this.level().isClientSide
            && this.isAlive
            && ( generateAtNight || sunIsVisible() )
        ) if (cooldown <= 0) generateSun()
    }

    private fun Plant.sunIsVisible() : Boolean = (
        this.level().isBrightOutside &&
        this.level().canSeeSky(BlockPos.containing(this.x, this.eyeY, this.z))
    )

    private fun Plant.generateSun() {
        val serverLevel = this.level() as? ServerLevel ?: return
        val dropped = dropFromGiftLootTable(serverLevel, PazLootTables.SUN_DROP, this::spawnAtLocation)

        if (dropped) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0f, 0.5f)
            this.gameEvent(GameEvent.ENTITY_PLACE)
        }
    }

}