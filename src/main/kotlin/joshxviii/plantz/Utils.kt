package joshxviii.plantz

import joshxviii.plantz.PazMain.MODID
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.item.component.AttackRange

fun pazResource(path: String): Identifier = Identifier.fromNamespaceAndPath(MODID, path)