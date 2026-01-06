package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazLootTables
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent

class Sunflower(level: Level) : Plant(PazEntities.SUNFLOWER, level) {

    var sunTime = this.random.nextInt(1000)+1000

    override fun aiStep() {
        super.aiStep()

        if (this.level() is ServerLevel && this.isAlive && --this.sunTime <= 0) {
            if (this.dropFromGiftLootTable(
                    level() as ServerLevel,
                    PazLootTables.SUN_DROP
                ) { level: ServerLevel?, itemStack: ItemStack? ->
                    this.spawnAtLocation(level!!, itemStack!!)
                }
            ) {
                this.playSound(SoundEvents.CHICKEN_EGG, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f)
                this.gameEvent(GameEvent.ENTITY_PLACE)
            }

            this.sunTime = this.random.nextInt(1000) + 1000
        }
    }

}