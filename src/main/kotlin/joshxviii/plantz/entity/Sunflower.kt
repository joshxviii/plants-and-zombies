package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazLootTables
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent

class Sunflower(
    type: EntityType<out Plant>,
    level: Level,
) : SunGeneratorPlant(PazEntities.SUNFLOWER, level) {
    override fun getSunCooldown() = 1500
    override fun attackGoals() {}
}