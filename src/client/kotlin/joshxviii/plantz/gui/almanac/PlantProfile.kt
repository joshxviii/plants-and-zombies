package joshxviii.plantz.gui.almanac

import joshxviii.plantz.PazConfig
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.item.SeedPacketItem
import joshxviii.plantz.pazResource
import joshxviii.plantz.renderer.entity.plant.PlantRenderState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.max

object PlantProfile {
    private const val INFO_CENTER_X = 150
    private const val INFO_WIDTH = 80
    private const val NAME_Y = 14
    private const val MODEL_TOP = 28
    private const val MODEL_BOTTOM = 88
    private const val DESCRIPTION_TOP = 105
    private const val TEXT_COLOR = 0xFF3F2A14.toInt()

    private const val ICON_SIZE = 10
    private val ICON_SUN = pazResource("textures/gui/almanac/info_icon_sun.png")
    private val ICON_HEALTH = pazResource("textures/gui/almanac/info_icon_health.png")

    private const val MODEL_PLATFORM_WIDTH = 83
    private const val MODEL_PLATFORM_HEIGHT = 21
    private val MODEL_PLATFORM = pazResource("textures/gui/almanac/model_platform.png")

    private const val UNKNOWN_WIDTH = 34
    private const val UNKNOWN_HEIGHT = 62
    private val UNKNOWN = pazResource("textures/gui/almanac/unknown.png")

    private const val NAMEPLATE_WIDTH = 88
    private const val NAMEPLATE_HEIGHT = 10
    private val NAMEPLATE = pazResource("textures/gui/almanac/info_box_name.png")

    val mc = Minecraft.getInstance()
    val font = mc.font

    private val idleAnimationState = AnimationState()

    private const val DETAIL_LEFT = 104
    private const val DETAIL_TOP = 68
    private const val DETAIL_RIGHT_MARGIN = 14
    private const val DETAIL_BOTTOM_MARGIN = 14
    private const val SCROLL_THUMB_WIDTH = 6
    private const val SCROLL_THUMB_HEIGHT = 12
    private val SCROLL_THUMB = pazResource("textures/gui/almanac/scroll_thumb.png")

    var scroll = 0f
    var contentHeight = 0
    var viewHeight = 1
    var scrollTrackX = 0
    var scrollTrackY = 0
    val maxScroll: Float get() = max(0, contentHeight - viewHeight).toFloat()

    private fun isKnown(itemStack: ItemStack?) = itemStack != null && !itemStack.isEmpty

    init {
        idleAnimationState.startIfStopped(0)
    }

    fun extractProfile(graphics: GuiGraphicsExtractor, type: EntityType<*>, itemStack: ItemStack?, x: Int, y: Int, width: Int, height: Int, detailedView: Boolean) {
        val level = mc.level ?: return
        val entity = SeedPacketItem.previewEntities.getOrPut(type) { type.create(level, EntitySpawnReason.LOAD)?.also { it.id = -1 } }
        val known = isKnown(itemStack)

        if (detailedView) {
            val left = x + DETAIL_LEFT
            renderEntity(graphics, entity, itemStack, left, y + 12, left + 44, y + 64, known)
            if (!known) blitUnknown(graphics, left + 22, y + 40, 0.45f)
            renderDetails(graphics, entity, known, x+4, y, width, height)
        }
        else {
            renderPlantModel(graphics, entity, itemStack, x+(width/2)+5, y+20, width-30, height-30)
            renderPlantInformation(graphics, entity, itemStack, x, y, width, height)
        }
    }

    private fun blitUnknown(graphics: GuiGraphicsExtractor, centerX: Int, centerY: Int, scale: Float) {
        return
        val w = (UNKNOWN_WIDTH * scale).toInt()
        val h = (UNKNOWN_HEIGHT * scale).toInt()
        graphics.blit(RenderPipelines.GUI_TEXTURED, UNKNOWN, centerX - w / 2, centerY - h, 0f, 0f, w, h, w, h)
    }

    private fun renderDetails(graphics: GuiGraphicsExtractor, entity: Entity?, known: Boolean, x: Int, y: Int, width: Int, height: Int) {
        val type = entity?.type
        val left = x + DETAIL_LEFT
        val right = x + width - DETAIL_RIGHT_MARGIN

        val top = y + DETAIL_TOP
        val bottom = y + height - DETAIL_BOTTOM_MARGIN
        val lines = if (type == null) emptyList() else if (!known) font.split(Component.literal("???"), right - left) else {
            val key = "almanac.plantz.details.${BuiltInRegistries.ENTITY_TYPE.getKey(type).path}"
            if (Language.getInstance().has(key)) font.split(Component.translatable(key), right - left - SCROLL_THUMB_WIDTH - 2) else emptyList()
        }
        viewHeight = bottom - top
        contentHeight = lines.size * 9
        scroll = scroll.coerceIn(0f, maxScroll)
        scrollTrackX = right - SCROLL_THUMB_WIDTH
        scrollTrackY = top

        graphics.enableScissor(left, top, right, bottom)
        lines.forEachIndexed { i, line ->
            graphics.text(font, line, left, top + i * 9 - scroll.toInt(), TEXT_COLOR, false)
        }
        graphics.disableScissor()

        if (maxScroll > 0) {
            val travel = viewHeight - SCROLL_THUMB_HEIGHT
            val thumbY = top + (travel * (scroll / maxScroll)).toInt()
            graphics.blit(RenderPipelines.GUI_TEXTURED, SCROLL_THUMB, scrollTrackX, thumbY, 0f, 0f, SCROLL_THUMB_WIDTH, SCROLL_THUMB_HEIGHT, SCROLL_THUMB_WIDTH, SCROLL_THUMB_HEIGHT)
        }
    }

    fun scrollBy(amount: Float) {
        scroll = (scroll + amount).coerceIn(0f, maxScroll)
    }

    fun scrollToMouse(mouseY: Double) {
        if (maxScroll <= 0) return
        val travel = (viewHeight - SCROLL_THUMB_HEIGHT).coerceAtLeast(1)
        scroll = (((mouseY - scrollTrackY - SCROLL_THUMB_HEIGHT / 2) / travel).toFloat() * maxScroll).coerceIn(0f, maxScroll)
    }

    fun overScrollTrack(mx: Double, my: Double): Boolean =
        maxScroll > 0 && mx >= scrollTrackX && mx < scrollTrackX + SCROLL_THUMB_WIDTH && my >= scrollTrackY && my < scrollTrackY + viewHeight

    private fun renderPlantInformation(graphics: GuiGraphicsExtractor, entity: Entity?, itemStack: ItemStack?, x: Int, y: Int, width: Int, height: Int) {
        val type = entity?.type
        val centerX = x + (width/2) + 48
        val known = itemStack != null && !itemStack.isEmpty
        val name = if (entity == null || !known) Component.literal("???") else type?.description ?: Component.empty()
        graphics.blit(RenderPipelines.GUI_TEXTURED, NAMEPLATE, centerX - (NAMEPLATE_WIDTH/2), y + NAME_Y - 1, 0f, 0f, NAMEPLATE_WIDTH, NAMEPLATE_HEIGHT, NAMEPLATE_WIDTH, NAMEPLATE_HEIGHT)
        graphics.text(font, name.visualOrderText, centerX - (font.width(name)/2), y + NAME_Y, TEXT_COLOR, false)

        val health = if (entity == null || !known) "???" else "${(entity as? LivingEntity)?.maxHealth ?: 0f}"
        val sunCost = if (entity == null || !known) "???" else "${PazConfig.getSunCost(type)}"

        val infoY = y + DESCRIPTION_TOP
        val infoX = x + 10 + (width / 2)
        infoStat(graphics, sunCost, ICON_SUN, infoX, infoY)
        infoStat(graphics, health, ICON_HEALTH, infoX, infoY + 12)
    }

    private fun renderPlantModel(graphics: GuiGraphicsExtractor, entity: Entity?, itemStack: ItemStack?, x: Int, y: Int, width: Int, height: Int) {
        val screenWidth = (width/2)+x
        val screenHeight = (height+y)+30
        graphics.blit(RenderPipelines.GUI_TEXTURED, MODEL_PLATFORM, ((screenWidth-MODEL_PLATFORM_WIDTH+x)/2), ((screenHeight-MODEL_PLATFORM_HEIGHT+y)/2), 0f, 0f, MODEL_PLATFORM_WIDTH, MODEL_PLATFORM_HEIGHT, MODEL_PLATFORM_WIDTH, MODEL_PLATFORM_HEIGHT)
        if (entity == null) return
        val known = isKnown(itemStack)
        renderEntity(graphics, entity, itemStack, x, y, screenWidth, screenHeight, known)
        if (!known) blitUnknown(graphics, (x + screenWidth) / 2, (y + screenHeight) / 2, 1f)
    }

    private fun renderEntity(graphics: GuiGraphicsExtractor, entity: Entity?, itemStack: ItemStack?, x: Int, y: Int, x2: Int, y2: Int, known: Boolean) {
        if (entity == null) return
        val name = if (known) itemStack?.get(DataComponents.CUSTOM_NAME)?.string ?: "" else ""
        if (known) itemStack?.get(DataComponents.ENTITY_DATA)?.loadInto(entity)
        val partial = mc.deltaTracker.getGameTimeDeltaPartialTick(true)
        val state = mc.entityRenderDispatcher.extractEntity(entity, 1f)

        state.shadowPieces.clear()
        state.outlineColor = 0
        if (state is PlantRenderState) {
            state.plantState = PlantState.IDLE
            state.silhouette = !known
            state.idleAnimationState.copyFrom(idleAnimationState)
            state.ageInTicks = if (known) mc.gui.hud.guiTicks + partial else 1f
            state.customName = name
            state.bodyRot = 0f
            state.yRot = 0f
        }
        val size = max(max(state.boundingBoxHeight, state.boundingBoxWidth), 0.1f)
        val scale = (y2 - y) * 0.7f / size * 0.45f
        val flip = Quaternionf().rotateX(Math.PI.toFloat() * 0.15f).rotateY(Math.PI.toFloat() * -0.75f).rotateZ(Math.PI.toFloat())

        graphics.guiRenderState.addPicturesInPictureState(
            GuiEntityRenderState(state, Vector3f(), flip, null, x, y, x2, y2, scale, null)
        )
    }

    private fun infoStat(graphics: GuiGraphicsExtractor, text: String, icon: Identifier, x: Int, y: Int) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, icon, x, y - 1, 0f, 0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE)
        graphics.text(font, text, x + ICON_SIZE, y, TEXT_COLOR, false)
    }

}