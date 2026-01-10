package joshxviii.plantz

import joshxviii.plantz.PazMain.MODID
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.AnimationState

fun pazResource(path: String): Identifier = Identifier.fromNamespaceAndPath(MODID, path)