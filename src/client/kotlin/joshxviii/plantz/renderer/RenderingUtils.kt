package joshxviii.plantz.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import joshxviii.plantz.PazEntities
import joshxviii.plantz.pazResource
import joshxviii.plantz.renderer.entity.ProjectileRenderState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FontDescription
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.ARGB
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.entity.EntityType
import org.spongepowered.asm.mixin.Unique

object RenderingUtils {
    fun plane(
        pose: PoseStack.Pose,
        buffer: VertexConsumer,
        lightCoords: Int,
        color: Int = 0xFFFFFFF,
    ) {
        vertex(pose, buffer, -0.5f, -0.5f, color, 0f, 1f, lightCoords)
        vertex(pose, buffer, 0.5f, -0.5f, color, 1f, 1f, lightCoords)
        vertex(pose, buffer, 0.5f, 0.5f, color, 1f, 0f, lightCoords)
        vertex(pose, buffer, -0.5f, 0.5f, color, 0f, 0f, lightCoords)
    }

    fun vertex(
        pose: PoseStack.Pose,
        buffer: VertexConsumer,
        x: Float,
        y: Float,
        color: Int,
        u: Float,
        v: Float,
        lightCoords: Int,
        overlay: Int = OverlayTexture.NO_OVERLAY
    ) {
        buffer.addVertex(pose, x, y, 0.0f)
            .setColor(ARGB.red(color), ARGB.green(color), ARGB.blue(color), ARGB.alpha(color))
            .setUv(u, v)
            .setOverlay(overlay)
            .setLight(lightCoords)
            .setNormal(pose, 0.0f, 1.0f, 0.0f)
    }
}

// MODEL RENDERING
fun ModelPart?.getChildOrNull(name: String): ModelPart? = if (this?.hasChild(name) == true) this.getChild(name) else null

@Unique
fun ModelPart.scaleDownRotation(scale: Float) {
    xRot *= scale
    yRot *= scale
    zRot *= scale
}

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

val MAGIC_NAMES = mapOf<EntityType<*>, List<String>>(
    PazEntities.SUNFLOWER           to listOf("vampire"),
    PazEntities.MARIGOLD            to listOf("flowey"),
    PazEntities.CHOMPER             to listOf("chester"),
    PazEntities.FIRE_PEA_SHOOTER    to listOf("blaze"),
    PazEntities.DISCO_ZOMBIE        to listOf("mj"),
    PazEntities.BROWN_COAT          to listOf("tugboat", "mustache")
)
fun LivingEntityRenderState.validMagicName(name: String): String? {
    val type = this.entityType
    val str = name.lowercase()
    MAGIC_NAMES.forEach { (entityType, names) ->
        if (entityType == type) return if (names.contains(str)) str else null
    }
    return null
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