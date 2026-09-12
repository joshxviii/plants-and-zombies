package joshxviii.plantz.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import joshxviii.plantz.PazEntities.MAGIC_NAMES
import joshxviii.plantz.pazResource
import joshxviii.plantz.renderer.entity.ProjectileRenderState
import joshxviii.plantz.renderer.entity.ProjectileRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Button.DEFAULT_NARRATION
import net.minecraft.client.gui.components.Button.OnPress
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

// GUI

object GuiUtil {
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

fun GuiGraphicsExtractor.outlineText(font: Font, text: Component, x: Int = 0, y: Int = 0, color: Int = 0xFFFFFF, outlineColor: Int = ARGB.multiply(color, 0x333333)) {
    text(font, text, x+1, y, ARGB.opaque(outlineColor), false)
    text(font, text, x-1, y, ARGB.opaque(outlineColor), false)
    text(font, text, x, y+1, ARGB.opaque(outlineColor), false)
    text(font, text, x, y-1, ARGB.opaque(outlineColor), false)
    text(font, text, x, y, ARGB.opaque(color), false)
}

object Fonts {
    val DOT_DISPLAY: Identifier = pazResource("dot_display")
    fun Component.withFont(font: Identifier): Component =
        this.copy().withStyle { it.withFont(FontDescription.Resource(font)) }
}

open class PazButton(
    var buttonX: Int,
    var buttonY: Int,
    val buttonWidth: Int,
    val buttonHeight: Int,
    val clickAction: OnPress,
    val texture: Identifier,
    val hoverTexture: Identifier = texture,
    val disabledTexture: Identifier = texture,
    val enabledRequirement: ((button: PazButton) -> Boolean) = { true },
    val clickRequirement: ((button: PazButton) -> Boolean) = enabledRequirement,
    val text: Component = Component.empty(),
    val color: Int = -1
) : Button(buttonX, buttonY, buttonWidth, buttonHeight, text, clickAction, DEFAULT_NARRATION) {

    override fun extractContents(
        graphics: GuiGraphicsExtractor,
        mx: Int,
        my: Int,
        a: Float
    ) {
        val pressed = isPressed()
        val font = Minecraft.getInstance().font

        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            if (pressed) disabledTexture else if (isButtonHovered(mx, my)) hoverTexture else texture,
            buttonX, buttonY, 0.0f, 0.0f, buttonWidth, buttonHeight, buttonWidth, buttonHeight, color
        )

        val line = font.split(text, buttonWidth-8).firstOrNull()
        if (line!=null) graphics.text(font, line, buttonX + if(pressed) 3 else 2, buttonY+3, -1, false)
    }

    fun isPressed(): Boolean {
        return !enabledRequirement.invoke(this)
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        if (clickRequirement.invoke(this)) super.mouseClicked(event, doubleClick)
        return false
    }

    fun isButtonHovered(mouseX: Int, mouseY: Int): Boolean {
        return mouseX in (buttonX..(buttonX + (buttonWidth-1)))
                && mouseY in (buttonY..(buttonY + (buttonHeight-1)))
    }

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