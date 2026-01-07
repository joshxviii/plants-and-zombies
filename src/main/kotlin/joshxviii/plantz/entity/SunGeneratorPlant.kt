package joshxviii.plantz.entity

import joshxviii.plantz.PazLootTables
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent

abstract class SunGeneratorPlant(type: EntityType<out Plant>, level: Level) : Plant(type, level) {

    open fun getSunCooldown() = 1500
    private var sunTime = getSunCooldown()

    open fun canGenerateAtNight() : Boolean = false

    protected fun tryGenerateSun() {
        if (!this.level().isClientSide &&
            this.isAlive &&
            ( canGenerateAtNight() || sunIsVisible() )
        ) {
            if (--sunTime <= 0) {
                generateSun()
                sunTime = getSunCooldown()  // reset
            }
        }
    }

    private fun generateSun() {
        val serverLevel = this.level() as? ServerLevel ?: return

        val dropped = this.dropFromGiftLootTable(
            serverLevel,
            PazLootTables.SUN_DROP
        ) { level, stack ->
            this.spawnAtLocation(level, stack)
        }

        if (dropped) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0f, 0.5f)
            this.gameEvent(GameEvent.ENTITY_PLACE)
        }
    }

    private fun sunIsVisible() : Boolean = (
        this.level().isBrightOutside &&
        this.level().canSeeSky(BlockPos.containing(this.x, this.eyeY, this.z))
    )

    override fun aiStep() {
        super.aiStep()
        tryGenerateSun()
    }
}