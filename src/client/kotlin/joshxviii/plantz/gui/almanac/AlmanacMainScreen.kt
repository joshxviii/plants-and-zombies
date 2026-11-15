package joshxviii.plantz.gui.almanac

import joshxviii.plantz.PazConfig
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
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
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
        private const val BACKGROUND_WIDTH = 201
        private const val BACKGROUND_HEIGHT = 145
        private const val MENU_WIDTH = BACKGROUND_WIDTH
        private const val MENU_HEIGHT = AlmanacMenu.INVENTORY_Y - 7 + 89
        private const val INVENTORY_WIDTH = 176
        private const val INVENTORY_HEIGHT = 89
        private const val PLANT_SLOT_SIZE = 20
        private const val PAGE_BUTTON_WIDTH = 18
        private const val PAGE_BUTTON_HEIGHT = 11
        private const val DETAILS_BUTTON_WIDTH = 12
        private const val DETAILS_BUTTON_HEIGHT = 10

        private val BACKGROUND = pazResource("textures/gui/almanac/background.png")
        private val PAPER_BACKGROUND = pazResource("textures/gui/almanac/paper_background.png")
        private val PAPER_DETAILS_BACKGROUND = pazResource("textures/gui/almanac/paper_details_background.png")
        private val INVENTORY_BACKGROUND = pazResource("textures/gui/letter/inventory.png")
        private val PLANT_SLOT = pazResource("textures/gui/almanac/plant_slot.png")
        private val PLANT_SLOT_SELECTED = pazResource("textures/gui/almanac/plant_slot_selected.png")
        private val PAGE_BACK = pazResource("textures/gui/almanac/page_back.png")
        private val PAGE_FORWARD = pazResource("textures/gui/almanac/page_forward.png")
        private val DETAILS_BUTTON = pazResource("textures/gui/almanac/details_button.png")
        private val DETAILS_BUTTON_HOVER = pazResource("textures/gui/almanac/details_button_hover.png")

    }

    private val tabButtons = ArrayList<PazButton>()
    private lateinit var backButton: PazButton
    private lateinit var forwardButton: PazButton
    private lateinit var detailsButton: PazButton

    override fun init() {
        super.init()
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
                { navigate(menu.tab, menu.page - 1) }, PAGE_BACK,
                sound = SoundEvents.BOOK_PAGE_TURN,
            )
        )
        forwardButton = addRenderableWidget(
            PazButton(
                leftPos + BACKGROUND_WIDTH - 4 - PAGE_BUTTON_WIDTH, topPos + BACKGROUND_HEIGHT - 10,
                PAGE_BUTTON_WIDTH, PAGE_BUTTON_HEIGHT,
                { navigate(menu.tab, menu.page + 1) }, PAGE_FORWARD,
                sound = SoundEvents.BOOK_PAGE_TURN,
            )
        )
        detailsButton = addRenderableWidget(
            PazButton(
                leftPos + BACKGROUND_WIDTH - DETAILS_BUTTON_WIDTH - 9, topPos + 23,
                DETAILS_BUTTON_WIDTH, DETAILS_BUTTON_HEIGHT,
                { menu.detailedView = !menu.detailedView; PlantProfile.scroll = 0f }, DETAILS_BUTTON, DETAILS_BUTTON_HOVER
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
        detailsButton.visible = seedTab
    }

    private var lastSelected = -1
    override fun containerTick() {
        super.containerTick()
        if (menu.selected != lastSelected) { lastSelected = menu.selected; PlantProfile.scroll = 0f }
        if (menu.responseTimeout > 0) --menu.responseTimeout
    }

    override fun extractLabels(graphics: GuiGraphicsExtractor, xm: Int, ym: Int) {}

    private var skipRelease = false
    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        draggingScroll = false
        if (skipRelease) {
            skipRelease = false
            return true
        }
        return super.mouseReleased(event)
    }

    private var draggingScroll = false
    override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {
        if (menu.detailedView && menu.tab == AlmanacTab.SEED_PACKETS.ordinal && x >= leftPos + BACKGROUND_WIDTH / 2) {
            PlantProfile.scrollBy((-scrollY * 9).toFloat())
            return true
        }
        return super.mouseScrolled(x, y, scrollX, scrollY)
    }

    override fun mouseDragged(event: MouseButtonEvent, dx: Double, dy: Double): Boolean {
        if (draggingScroll) {
            PlantProfile.scrollToMouse(event.y)
            return true
        }
        return super.mouseDragged(event, dx, dy)
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        if (menu.detailedView && PlantProfile.overScrollTrack(event.x, event.y)) {
            draggingScroll = true
            PlantProfile.scrollToMouse(event.y)
            return true
        }
        val slot = hoveredSlot
        if (slot != null && slot.index < menu.plantCount && slot.isActive
            && menu.carried.isEmpty && !event.hasShiftDown()
        ) {
            skipRelease = true
            sendButton((if (doubleClick && slot.hasItem()) AlmanacMenu.TAKE_BASE else AlmanacMenu.SELECT_BASE) + slot.index)
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
        blit(graphics, if (menu.detailedView && menu.tab == AlmanacTab.SEED_PACKETS.ordinal) PAPER_DETAILS_BACKGROUND else PAPER_BACKGROUND, leftPos, topPos, BACKGROUND_WIDTH, BACKGROUND_HEIGHT)
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
        menu.selectedType()?.let {
            PlantProfile.extractProfile(graphics, it, menu.selectedItem(), leftPos, topPos, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, menu.detailedView)
        }
    }

    private fun blit(graphics: GuiGraphicsExtractor, texture: Identifier, x: Int, y: Int, width: Int, height: Int) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0f, 0f, width, height, width, height)
    }
}
