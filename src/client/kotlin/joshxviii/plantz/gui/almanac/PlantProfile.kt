package joshxviii.plantz.gui.almanac

import joshxviii.plantz.PazConfig
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.gui.SunInfoHud.SUN_ICON
import joshxviii.plantz.gui.almanac.AlmanacMainScreen.Companion.idleAnimationState
import joshxviii.plantz.pazResource
import joshxviii.plantz.renderer.entity.plant.PlantRenderState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenPosition
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
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

    private const val NAMEPLATE_WIDTH = 88
    private const val NAMEPLATE_HEIGHT = 10
    private val NAMEPLATE = pazResource("textures/gui/almanac/info_box_name.png")

    private val previewEntities = HashMap<EntityType<*>, Entity?>()
    val mc = Minecraft.getInstance()
    val font = mc.font

    fun extractProfile(graphics: GuiGraphicsExtractor, type: EntityType<*>, itemStack: ItemStack?, x: Int, y: Int, width: Int, height: Int, detailedView: Boolean) {
        val level = mc.level ?: return
        val entity = previewEntities.getOrPut(type) { type.create(level, EntitySpawnReason.LOAD)?.also { it.id = -1 } }

        if (detailedView) {

        }
        else {
            renderPlantModel(graphics, entity, itemStack, x+(width/2)+5, y+20, width-30, height-30)
            renderPlantInformation(graphics, entity, itemStack, x, y, width, height)
        }
    }

    private fun renderPlantInformation(graphics: GuiGraphicsExtractor, entity: Entity?, itemStack: ItemStack?, x: Int, y: Int, width: Int, height: Int) {
        val type = entity?.type
        val centerX = x + (width/2) + 48
        val name = type?.description ?: Component.empty()
        graphics.blit(RenderPipelines.GUI_TEXTURED, NAMEPLATE, centerX - (NAMEPLATE_WIDTH/2), y + NAME_Y - 1, 0f, 0f, NAMEPLATE_WIDTH, NAMEPLATE_HEIGHT, NAMEPLATE_WIDTH, NAMEPLATE_HEIGHT)
        graphics.text(font, name.visualOrderText, centerX - (font.width(name)/2), y + NAME_Y, TEXT_COLOR, false)

        val health = if (entity == null) "???" else "${(entity as? LivingEntity)?.maxHealth ?: 0f}"
        val sunCost = if (entity == null) "???" else "${PazConfig.getSunCost(type)}"

        val infoY = y + DESCRIPTION_TOP
        val infoX = x + 10 + (width / 2)
        infoStat(graphics, sunCost, ICON_SUN, infoX, infoY)
        infoStat(graphics, health, ICON_HEALTH, infoX, infoY + 12)
        descriptionInfo(graphics, type, centerX, infoY)
    }

    private fun renderPlantModel(graphics: GuiGraphicsExtractor, entity: Entity?, itemStack: ItemStack?, x: Int, y: Int, width: Int, height: Int) {
        val screenWidth = (width/2)+x
        val screenHeight = (height+y)+20
        graphics.blit(RenderPipelines.GUI_TEXTURED, MODEL_PLATFORM, ((screenWidth-MODEL_PLATFORM_WIDTH+x)/2), ((screenHeight-MODEL_PLATFORM_HEIGHT+y)/2), 0f, 0f, MODEL_PLATFORM_WIDTH, MODEL_PLATFORM_HEIGHT, MODEL_PLATFORM_WIDTH, MODEL_PLATFORM_HEIGHT)
        if (entity == null) return
        val name = itemStack?.get(DataComponents.CUSTOM_NAME)?.string ?: ""
        val partial = mc.deltaTracker.getGameTimeDeltaPartialTick(true)
        val state = mc.entityRenderDispatcher.extractEntity(entity, 1f)

        state.shadowPieces.clear()
        state.outlineColor = 0
        if (state is PlantRenderState) {
            state.plantState = PlantState.IDLE
            state.idleAnimationState.copyFrom(idleAnimationState)
            state.ageInTicks = mc.gui.hud.guiTicks + partial
            state.customName = name
            state.bodyRot = 0f
            state.yRot = 0f
        }
        val size = max(max(state.boundingBoxHeight, state.boundingBoxWidth), 0.1f)
        val scale = (MODEL_BOTTOM - MODEL_TOP) * 0.7f / size
        val flip = Quaternionf().rotateX(Math.PI.toFloat() * 0.15f).rotateY(Math.PI.toFloat() * -0.75f).rotateZ(Math.PI.toFloat())

        //graphics.fill(x,y,screenWidth,screenHeight,-1) // debug
        graphics.guiRenderState.addPicturesInPictureState(
            GuiEntityRenderState(state, Vector3f(), flip, null, x, y, screenWidth, screenHeight, scale, null)
        )

    }

    private fun infoStat(graphics: GuiGraphicsExtractor, text: String, icon: Identifier, x: Int, y: Int) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, icon, x, y - 1, 0f, 0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE)
        graphics.text(font, text, x + ICON_SIZE, y, TEXT_COLOR, false)
    }

    private fun descriptionInfo(graphics: GuiGraphicsExtractor, type: EntityType<*>?, x: Int, y: Int) {
        if (type == null) return
        val key = "almanac.plantz.description.${BuiltInRegistries.ENTITY_TYPE.getKey(type).path}"
        if (!Language.getInstance().has(key)) return
        font.split(Component.translatable(key), INFO_WIDTH).forEachIndexed { line, text ->
            graphics.text(font, text, x - INFO_WIDTH / 2, y + DESCRIPTION_TOP + line * 9, TEXT_COLOR, false)
        }
    }
}