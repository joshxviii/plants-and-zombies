package joshxviii.plantz.gui.almanac

import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.gui.PazButton
import joshxviii.plantz.inventory.AlmanacMenu
import joshxviii.plantz.item.component.AlmanacTab
import joshxviii.plantz.pazResource
import joshxviii.plantz.renderer.entity.plant.PlantRenderState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState
import net.minecraft.core.component.DataComponents
import net.minecraft.locale.Language
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.max

class AlmanacMainScreen(
    val menu: AlmanacMenu,
    inventory: Inventory,
    title: Component = Component.translatable("gui.plantz.almanac")
) : AbstractContainerScreen<AlmanacMenu>(menu, inventory, title, MENU_WIDTH, MENU_HEIGHT) {
    companion object {
        val idleAnimationState = AnimationState()
        private const val BACKGROUND_WIDTH = 201
        private const val BACKGROUND_HEIGHT = 145
        private const val MENU_WIDTH = BACKGROUND_WIDTH
        private const val MENU_HEIGHT = AlmanacMenu.INVENTORY_Y - 7 + 89
        private const val INVENTORY_WIDTH = 176
        private const val INVENTORY_HEIGHT = 89
        private const val PLANT_SLOT_SIZE = 20
        private const val PAGE_BUTTON_WIDTH = 18
        private const val PAGE_BUTTON_HEIGHT = 11

        private const val INFO_CENTER_X = 150
        private const val INFO_WIDTH = 80
        private const val NAME_Y = 14
        private const val MODEL_TOP = 28
        private const val MODEL_BOTTOM = 88
        private const val DESCRIPTION_TOP = 94
        private const val TEXT_COLOR = 0xFF3F2A14.toInt()

        private val BACKGROUND = pazResource("textures/gui/almanac/background.png")
        private val PAPER_BACKGROUND = pazResource("textures/gui/almanac/paper_background.png")
        private val PLANT_SLOT = pazResource("textures/gui/almanac/plant_slot.png")
        private val PLANT_SLOT_SELECTED = pazResource("textures/gui/almanac/plant_slot_selected.png")
        private val PAGE_BACK = pazResource("textures/gui/almanac/page_back.png")
        private val PAGE_FORWARD = pazResource("textures/gui/almanac/page_forward.png")
        private val INVENTORY_BACKGROUND = pazResource("textures/gui/letter/inventory.png")
    }

    private val tabButtons = ArrayList<PazButton>()
    private lateinit var backButton: PazButton
    private lateinit var forwardButton: PazButton
    private val previewEntities = HashMap<EntityType<*>, Entity?>()

    override fun init() {
        super.init()
        idleAnimationState.startIfStopped(0)
        tabButtons.clear()
        AlmanacTab.entries.forEach { tab ->
            val button = AlmanacTabButton(
                tab, leftPos, topPos,
                clickAction = { navigate(tab.ordinal, 0) },
                enabledRequirement = { tab.ordinal != menu.tab },
            )
            tabButtons += addRenderableWidget(button)
        }
        backButton = addRenderableWidget(
            PazButton(
                leftPos + 4, topPos + BACKGROUND_HEIGHT - 10, PAGE_BUTTON_WIDTH, PAGE_BUTTON_HEIGHT,
                { navigate(menu.tab, menu.page - 1) }, PAGE_BACK
            )
        )
        forwardButton = addRenderableWidget(
            PazButton(
                leftPos + BACKGROUND_WIDTH - 4 - PAGE_BUTTON_WIDTH, topPos + BACKGROUND_HEIGHT - 10,
                PAGE_BUTTON_WIDTH, PAGE_BUTTON_HEIGHT,
                { navigate(menu.tab, menu.page + 1) }, PAGE_FORWARD
            )
        )
    }

    private fun sendButton(id: Int) {
        Minecraft.getInstance().gameMode?.handleInventoryButtonClick(menu.containerId, id)
    }

    private fun navigate(tab: Int, page: Int) = sendButton(AlmanacMenu.navigationId(tab, page))

    private fun updateButtons() {
        val seedTab = menu.tab == AlmanacTab.SEED_PACKETS.ordinal
        backButton.visible = seedTab && menu.page > 0
        forwardButton.visible = seedTab && menu.page < menu.pageCount - 1
    }

    override fun containerTick() {
        super.containerTick()
        if (menu.responseTimeout > 0) --menu.responseTimeout
    }

    override fun extractLabels(graphics: GuiGraphicsExtractor, xm: Int, ym: Int) {}

    private var skipRelease = false
    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        if (skipRelease) {
            skipRelease = false
            return true
        }
        return super.mouseReleased(event)
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        val slot = hoveredSlot
        if (slot != null && slot.index < menu.plantCount && slot.isActive && slot.hasItem()
            && menu.carried.isEmpty && !event.hasShiftDown()
        ) {
            skipRelease = true
            sendButton((if (doubleClick) AlmanacMenu.TAKE_BASE else AlmanacMenu.SELECT_BASE) + slot.index)
            return true
        }
        return super.mouseClicked(event, doubleClick)
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        updateButtons()
        super.extractRenderState(graphics, mouseX, mouseY, a)
        if (menu.responseTimeout > 0) {// notifications and warnings
            val message = menu.responseMessage
            val top = (BACKGROUND_HEIGHT + 12) / 2
            graphics.fill(leftPos, top, leftPos + BACKGROUND_WIDTH, top + 16, 0xFF3A2A1A.toInt())
            graphics.text(font, message, leftPos + (BACKGROUND_WIDTH - font.width(message)) / 2, top + 4, -1, false)
        }
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractBackground(graphics, mouseX, mouseY, a)
        blit(graphics, BACKGROUND, leftPos, topPos, BACKGROUND_WIDTH, BACKGROUND_HEIGHT)
        blit(graphics, PAPER_BACKGROUND, leftPos, topPos, BACKGROUND_WIDTH, BACKGROUND_HEIGHT)
        blit(
            graphics, INVENTORY_BACKGROUND,
            leftPos + AlmanacMenu.INVENTORY_X - 8, topPos + AlmanacMenu.INVENTORY_Y - 7,
            INVENTORY_WIDTH, INVENTORY_HEIGHT
        )

        if (menu.tab != AlmanacTab.SEED_PACKETS.ordinal) return

        menu.slots.take(menu.plantCount).filter { it.isActive }.forEach { slot ->
            val x = leftPos + slot.x - 2
            val y = topPos + slot.y - 2
            blit(graphics, if (slot.index == menu.selected) PLANT_SLOT_SELECTED else PLANT_SLOT, x, y, PLANT_SLOT_SIZE, PLANT_SLOT_SIZE)
        }
        menu.selectedType()?.let { extractPlantInfo(graphics, it) }
    }

    private fun blit(graphics: GuiGraphicsExtractor, texture: Identifier, x: Int, y: Int, width: Int, height: Int) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0f, 0f, width, height, width, height)
    }

    private fun extractPlantInfo(graphics: GuiGraphicsExtractor, type: EntityType<*>) {
        val centerX = leftPos + INFO_CENTER_X
        val name = type.description
        graphics.text(font, name.visualOrderText, centerX - font.width(name) / 2, topPos + NAME_Y, TEXT_COLOR, false)

        extractPlantModel(graphics, type)

        val key = "almanac.plantz.description.${BuiltInRegistries.ENTITY_TYPE.getKey(type).path}"
        if (!Language.getInstance().has(key)) return
        font.split(Component.translatable(key), INFO_WIDTH).forEachIndexed { line, text ->
            graphics.text(font, text, centerX - INFO_WIDTH / 2, topPos + DESCRIPTION_TOP + line * 9, TEXT_COLOR, false)
        }
    }

    private fun extractPlantModel(graphics: GuiGraphicsExtractor, type: EntityType<*>) {
        val mc = Minecraft.getInstance()
        val level = mc.level ?: return
        val name =  menu.selectedItem()?.get(DataComponents.CUSTOM_NAME)?.string ?: ""
        val entity = previewEntities.getOrPut(type) { type.create(level, EntitySpawnReason.LOAD)?.also { it.id = -1 } } ?: return
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
            GuiEntityRenderState(state, Vector3f(), flip, null, 93, -65, width, height, scale, ScreenRectangle(leftPos, topPos+30, BACKGROUND_WIDTH-20, BACKGROUND_HEIGHT))
        )
    }
}
