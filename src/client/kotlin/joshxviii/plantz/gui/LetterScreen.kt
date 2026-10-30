package joshxviii.plantz.gui

import joshxviii.plantz.inventory.LetterMenu
import joshxviii.plantz.item.component.LetterBackground
import joshxviii.plantz.item.component.LetterFont
import joshxviii.plantz.networking.UpdateLetterPayload
import joshxviii.plantz.pazResource
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.TextAlignment
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.MultiLineEditBox
import net.minecraft.client.gui.screens.ConfirmScreen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.BookViewScreen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FontDescription
import net.minecraft.network.chat.Style
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack

class LetterScreen(
    val menu: LetterMenu,
    inventory: Inventory,
    title: Component = Component.translatable("gui.plantz.letter")
) : AbstractContainerScreen<LetterMenu>(menu, inventory, title, MENU_WIDTH, MENU_HEIGHT) {
    companion object {
        private const val TEXT_WIDTH = 176
        private const val TEXT_HEIGHT = 133
        private const val LETTER_WIDTH = 185
        private const val LETTER_HEIGHT = 133
        private const val BACKGROUND_TEXTURE_SIZE = 256
        private const val CONTROL_WIDTH = 90
        private const val SLOT_SIZE = 18
        private const val INVENTORY_WIDTH = 176
        private const val INVENTORY_HEIGHT = 89
        private const val INVENTORY_TOP = LETTER_HEIGHT + 1
        private const val ACTION_TOP = INVENTORY_TOP + INVENTORY_HEIGHT
        private const val MENU_WIDTH = 279
        private const val MENU_HEIGHT = 249
        private const val TEXT_COLOR = 0xFF000000.toInt()

        private val INVENTORY_BACKGROUND = pazResource("textures/gui/letter/inventory.png")
        private val ATTACHMENT_SLOT = pazResource("textures/gui/letter/attachment_slot.png")
    }

    private val itemStack = menu.letterStack
    private val originalData = menu.letterData
    private val editable = menu.editable
    private val bookAccess = BookViewScreen.BookAccess.fromItem(itemStack) ?: BookViewScreen.EMPTY_ACCESS
    private val legacyPages = (0 until bookAccess.pageCount).map(bookAccess::getPage)
    private val pageComponents = if (originalData.pages.isNotEmpty() || originalData.sign) {
        originalData.pages.map(Component::literal)
    } else {
        legacyPages
    }
    private val pages = pageComponents.mapTo(mutableListOf()) { it.string }
    private var selectedBackground = originalData.background
    private var selectedFont = originalData.font
    private var pageNumber = CommonComponents.EMPTY
    private var saved = false
    private lateinit var pageEditor: MultiLineEditBox

    override fun init() {
        super.init()
        if (editable) {
            if (pages.isEmpty()) pages.add("")
            pageEditor = MultiLineEditBox.builder()
                .setShowDecorations(false)
                .setTextColor(TEXT_COLOR)
                .setCursorColor(TEXT_COLOR)
                .setShowBackground(false)
                .setTextShadow(false)
                .setX(backgroundLeft() + 4)
                .setY(topPos + 4)
                .build(font, TEXT_WIDTH, TEXT_HEIGHT - 32, CommonComponents.EMPTY)
            pageEditor.setCharacterLimit(1024)
            pageEditor.setLineLimit(126 / 9)
            pageEditor.setValueListener { value -> pages[0] = value }
            addRenderableWidget(pageEditor)
            pageEditor.setValue(pages[0], true)

            addRenderableWidget(
                Button.builder(fontButtonLabel()) {
                    selectedFont = LetterFont.entries[(selectedFont.ordinal + 1) % LetterFont.entries.size]
                    it.setMessage(fontButtonLabel())
                }.pos(leftPos + LETTER_WIDTH + 4, topPos + 27).width(CONTROL_WIDTH).build()
            )
            addRenderableWidget(
                Button.builder(backgroundButtonLabel()) {
                    selectedBackground =
                        LetterBackground.entries[(selectedBackground.ordinal + 1) % LetterBackground.entries.size]
                    it.setMessage(backgroundButtonLabel())
                }.pos(leftPos + LETTER_WIDTH + 4, topPos + 51).width(CONTROL_WIDTH).build()
            )
            addRenderableWidget(
                Button.builder(Component.translatable("gui.plantz.letter.sign")) {
                    Minecraft.getInstance().setScreen(
                        ConfirmScreen(
                            { confirmed ->
                                if (confirmed) {
                                    saveChanges(true)
                                    onClose()
                                } else {
                                    Minecraft.getInstance().setScreen(this)
                                }
                            },
                            Component.translatable("gui.plantz.letter.confirm_title"),
                            Component.translatable("gui.plantz.letter.confirm_message"),
                            Component.translatable("gui.plantz.letter.confirm_sign"),
                            CommonComponents.GUI_CANCEL
                        )
                    )
                }.pos(leftPos + (MENU_WIDTH - 188) / 2, topPos + ACTION_TOP).width(92).build()
            )
            addRenderableWidget(
                Button.builder(CommonComponents.GUI_DONE) {
                    saveChanges(false)
                    onClose()
                }.pos(leftPos + MENU_WIDTH / 2 + 2, topPos + ACTION_TOP).width(92).build()
            )
        } else {
            addRenderableWidget(
                Button.builder(CommonComponents.GUI_DONE) { onClose() }
                    .pos(leftPos + (MENU_WIDTH - 200) / 2, topPos + ACTION_TOP).width(200).build()
            )
        }
    }

    override fun extractLabels(graphics: GuiGraphicsExtractor, xm: Int, ym: Int) {}

    override fun setInitialFocus() {
        if (editable) setInitialFocus(pageEditor) else super.setInitialFocus()
    }

    override fun getNarrationMessage(): Component =
        CommonComponents.joinForNarration(super.getNarrationMessage(), pageNumber)

    override fun keyPressed(event: KeyEvent): Boolean {
        return if (event.isEscape && this.shouldCloseOnEsc()) {
            this.onClose()
            true
        } else {
            if (editable && this.minecraft.options.keyInventory.matches(event)) false
            else (super.keyPressed(event) || event.key() == 258)
        }
    }

    override fun onClose() {
        if (editable) saveChanges(false)
        super.onClose()
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)
        val collector = graphics.textRenderer()
        collector.accept(TextAlignment.RIGHT, leftPos + LETTER_WIDTH - 5, topPos, pageNumber)

        if (!editable) {
            val page = (pageComponents.firstOrNull() ?: CommonComponents.EMPTY).copy()
            page.withStyle(Style.EMPTY.withFont(FontDescription.Resource(originalData.font.getFont())))
            page.withColor(TEXT_COLOR)
            page.withoutShadow()
            val lines = font.split(page, TEXT_WIDTH)
            for (index in 0 until minOf(126 / 9, lines.size)) {
                collector.accept(backgroundLeft() + 8, topPos + 8 + index * 9, lines[index])
            }
        }
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractBackground(graphics, mouseX, mouseY, a)
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            (if (editable) selectedBackground else originalData.background).getBackground(),
            backgroundLeft(),
            backgroundTop(),
            0f,
            0f,
            LETTER_WIDTH,
            LETTER_HEIGHT,
            BACKGROUND_TEXTURE_SIZE,
            BACKGROUND_TEXTURE_SIZE
        )
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            ATTACHMENT_SLOT,
            leftPos + menu.attachmentSlot.x,
            topPos + menu.attachmentSlot.y,
            0f,
            0f,
            SLOT_SIZE,
            SLOT_SIZE,
            SLOT_SIZE,
            SLOT_SIZE
        )
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            INVENTORY_BACKGROUND,
            leftPos + (MENU_WIDTH - INVENTORY_WIDTH) / 2,
            topPos + INVENTORY_TOP,
            0f,
            0f,
            INVENTORY_WIDTH,
            INVENTORY_HEIGHT,
            INVENTORY_WIDTH,
            INVENTORY_HEIGHT
        )
    }

    private fun saveChanges(sign: Boolean) {
        if (saved || itemStack.isEmpty) return
        val player = Minecraft.getInstance().player ?: return
        while (pages.isNotEmpty() && pages.last().isEmpty()) pages.removeAt(pages.lastIndex)

        val data = originalData.copy(
            background = selectedBackground,
            font = selectedFont,
            item = ItemStack.EMPTY,
            pages = pages.toList(),
            sign = sign || originalData.sign,
            author = if (sign) player.name.string else originalData.author
        )
        ClientPlayNetworking.send(UpdateLetterPayload(menu.handIndex, data))
        saved = true
    }

    private fun fontButtonLabel() = Component.translatable(
        "gui.plantz.letter.font", Component.translatable(selectedFont.translationKey)
    )

    private fun backgroundButtonLabel() = Component.translatable(
        "gui.plantz.letter.background", Component.translatable(selectedBackground.translationKey)
    )

    fun backgroundLeft() = if (editable) leftPos else (width - 187) / 2
    fun backgroundTop() = topPos
}
