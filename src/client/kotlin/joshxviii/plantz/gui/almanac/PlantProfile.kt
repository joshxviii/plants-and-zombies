package joshxviii.plantz.gui.almanac

import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.gui.SunInfoHud
import joshxviii.plantz.gui.almanac.AlmanacMainScreen.Companion.idleAnimationState
import joshxviii.plantz.pazResource
import joshxviii.plantz.renderer.entity.plant.PlantRenderState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.locale.Language
import net.minecraft.network.chat.Component
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
    private const val DESCRIPTION_TOP = 102
    private const val TEXT_COLOR = 0xFF3F2A14.toInt()

    private val ICON_SUN = pazResource("")
    private val ICON_HEALTH = pazResource("")

    private val previewEntities = HashMap<EntityType<*>, Entity?>()
    val mc = Minecraft.getInstance()
    val font = mc.font

    fun extractProfile(graphics: GuiGraphicsExtractor, type: EntityType<*>, itemStack: ItemStack?, x: Int, y: Int, width: Int, height: Int) {
        val level = mc.level ?: return
        val entity = previewEntities.getOrPut(type) { type.create(level, EntitySpawnReason.LOAD)?.also { it.id = -1 } } ?: return

        extractPlantModel(graphics, entity, itemStack, x, y + 30, width, height)
        extractInformation(graphics, entity, itemStack, x, y)
    }

    private fun extractInformation(graphics: GuiGraphicsExtractor, entity: Entity, itemStack: ItemStack?, x: Int, y: Int) {
        val type = entity.type
        val centerX = x + INFO_CENTER_X
        val name = type.description
        graphics.text(font, name.visualOrderText, centerX - font.width(name) / 2, y + NAME_Y, TEXT_COLOR, false)

        val health = (entity as? LivingEntity)?.maxHealth ?: 0f

        SunInfoHud.displaySeedCost(graphics, itemStack, 100, centerX, y + DESCRIPTION_TOP)

        val key = "almanac.plantz.description.${BuiltInRegistries.ENTITY_TYPE.getKey(type).path}"
        if (!Language.getInstance().has(key)) return
        font.split(Component.translatable(key), INFO_WIDTH).forEachIndexed { line, text ->
            graphics.text(font, text, centerX - INFO_WIDTH / 2, y + DESCRIPTION_TOP + line * 9, TEXT_COLOR, false)
        }
    }

    private fun extractPlantModel(graphics: GuiGraphicsExtractor, entity: Entity, itemStack: ItemStack?, x: Int, y: Int, width: Int, height: Int) {
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
        }
        val size = max(max(state.boundingBoxHeight, state.boundingBoxWidth), 0.1f)
        val scale = (MODEL_BOTTOM - MODEL_TOP) * 0.7f / size
        val flip = Quaternionf().rotateX(Math.PI.toFloat() * 0.15f).rotateY(Math.PI.toFloat() * -0.75f).rotateZ(Math.PI.toFloat())

        graphics.guiRenderState.addPicturesInPictureState(
            GuiEntityRenderState(state, Vector3f(), flip, null, 93, -65, mc.gui.screen()?.width?:width, mc.gui.screen()?.height?:height, scale, ScreenRectangle(x, y, width-20, height))
        )
    }

    fun sunInfo(x: Int, y: Int, graphics: GuiGraphicsExtractor) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, ICON_SUN, x, y, 0f, 0f, 14, 14, 14, 14)
    }

    fun healthInfo(x: Int, y: Int, graphics: GuiGraphicsExtractor) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, ICON_HEALTH, x, y, 0f, 0f, 14, 14, 14, 14)
    }
}