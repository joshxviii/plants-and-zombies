package joshxviii.plantz

import joshxviii.plantz.PazMain.MODID
import net.minecraft.resources.Identifier

fun pazResource(path: String): Identifier = Identifier.fromNamespaceAndPath(MODID, path)