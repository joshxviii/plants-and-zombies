package joshxviii.plantz.entity.plant

import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

abstract class Mushroom(type: EntityType<out Mushroom>, level: Level) : Plant(type, level) {
    override fun sleepsDuringDay(): Boolean = true
}