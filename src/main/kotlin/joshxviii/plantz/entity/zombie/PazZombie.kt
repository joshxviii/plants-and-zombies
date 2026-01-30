package joshxviii.plantz.entity.zombie

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level

abstract class PazZombie(type: EntityType<out PazZombie>, level: Level) :  Zombie(type, level) {
}