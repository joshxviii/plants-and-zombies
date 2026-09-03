package joshxviii.plantz.renderer

import joshxviii.plantz.PazEntities.MAGIC_NAMES
import joshxviii.plantz.pazResource
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager


// MODEL RENDERING
fun List<String>.permutationsDescending(): List<String> = buildList {
    add(this@permutationsDescending.joinToString("_"))
    for (i in size - 1 downTo 1) {
        add(this@permutationsDescending.subList(0, i).joinToString(""))
    }
}

fun resolveTextureLocation(base: String, rm: ResourceManager, suffixes: List<String> = listOf()): Identifier? {
    for (suffix in suffixes.permutationsDescending()) {
        if (suffix.isEmpty()) break
        val candidate = pazResource("${base}_${suffix}.png")
        if (rm.getResource(candidate).isPresent) return candidate
    }
    return null
}

fun LivingEntityRenderState.isMagicName(name: String): String {
    val type = this.entityType
    MAGIC_NAMES.forEach { (entityType, magicName) ->
        if (entityType == type && magicName == name.lowercase()) return magicName
    }
    return ""
}

fun BlockEntityRenderState.getTextureLocation(path: String): Identifier {
    return pazResource("${path}.png")
}

fun EntityRenderState.getProjectileTextureLocation(basePath: String, emissive: Boolean = false): Identifier? {
    val entityName = entityType.toShortString().lowercase()
    val path = "${basePath}/${entityName}${if (emissive) "_emissive" else ""}.png"
    val texture = pazResource(path)
    val isValid = Minecraft.getInstance().resourceManager.getResource(texture).isPresent
    return if (isValid) texture else null
}

fun EntityRenderState.getTextureLocation(basePath: String, suffixes: MutableList<String> = mutableListOf()): Identifier {
    val entityName = entityType.toShortString().lowercase()
    val base = "${basePath}/${entityName}/${entityName}"
    val rm = Minecraft.getInstance().resourceManager

    val textureLocation = resolveTextureLocation(base, rm, suffixes)
    return textureLocation?: pazResource("${base}.png")
}

fun EntityRenderState.getEmissiveTextureLocation(basePath: String, suffixes: MutableList<String> = mutableListOf()): Identifier? {
    val entityName = entityType.toShortString().lowercase()
    val base = "${basePath}/${entityName}/${entityName}"
    val rm = Minecraft.getInstance().resourceManager

    return resolveTextureLocation(base, rm, suffixes.apply { add("emissive") })
}