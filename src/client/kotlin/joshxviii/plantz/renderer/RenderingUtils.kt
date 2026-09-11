package joshxviii.plantz.renderer

import joshxviii.plantz.PazEntities.MAGIC_NAMES
import joshxviii.plantz.pazResource
import joshxviii.plantz.renderer.entity.ProjectileRenderState
import joshxviii.plantz.renderer.entity.ProjectileRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.ARGB
import kotlin.collections.component1
import kotlin.collections.component2

fun GuiGraphicsExtractor.outlineText(font: Font, text: Component, x: Int = 0, y: Int = 0, color: Int = 0xFFFFFF, outlineColor: Int = ARGB.multiply(color, 0x333333)) {
    text(font, text, x+1, y, ARGB.opaque(outlineColor), false)
    text(font, text, x-1, y, ARGB.opaque(outlineColor), false)
    text(font, text, x, y+1, ARGB.opaque(outlineColor), false)
    text(font, text, x, y-1, ARGB.opaque(outlineColor), false)
    text(font, text, x, y, ARGB.opaque(color), false)
}

// MODEL RENDERING
fun ModelPart.getChildOrNull(name: String): ModelPart? = if (this.hasChild(name)) this.getChild(name) else null

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

fun EntityRenderState.getProjectileTextureLocation(emissive: Boolean = false): Identifier? {
    val entityName = entityType.toShortString().lowercase()
    val path = "${ProjectileRenderState.TEXTURE_PATH}/${entityName}${if (emissive) "_emissive" else ""}.png"
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