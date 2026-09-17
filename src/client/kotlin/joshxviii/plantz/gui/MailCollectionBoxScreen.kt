package joshxviii.plantz.gui

import com.mojang.blaze3d.platform.cursor.CursorTypes
import joshxviii.plantz.inventory.MailCollectionBoxMenu
import joshxviii.plantz.networking.UpdateCollectionBoxPayload
import joshxviii.plantz.pazResource
import joshxviii.plantz.renderer.outlineText
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory

class MailCollectionBoxScreen(
    val menu: MailCollectionBoxMenu,
    val inventory: Inventory,
    title: Component,
) : AbstractContainerScreen<MailCollectionBoxMenu>(menu, inventory, title, 176, 180) {
    private lateinit var addressSearch: EditBox
    private val miniButtons = mutableListOf<MiniAddressButton>()
    private var scrollOffs = 0f
    private var scrolling = false
    private var startIndex = 0
        set(value) {
            if (field != value) {
                field = value
                rebuildAddressButtons()
            }
        }

    companion object {
        val BACKGROUND: Identifier = pazResource("textures/gui/mail_collection_box/background.png")
        val MAILBOX_SELECTED: Identifier = pazResource("textures/gui/mail_collection_box/title_selected.png")
        val MAILBOX_UNSELECTED: Identifier = pazResource("textures/gui/mail_collection_box/title_unselected.png")
        val SCROLLER: Identifier = pazResource("textures/gui/mailbox/scroller.png")
        val SCROLLER_DISABLED: Identifier = pazResource("textures/gui/mailbox/scroller_disabled.png")

        const val MAX_VISIBLE_ROWS = 8
    }

    fun initSearchBar(x: Int, y: Int): EditBox {
        val txt = EditBox(font, x, y, 94, 12, Component.translatable("container.plantz.address_search"));
        txt.setCanLoseFocus(true)
        txt.setTextColor(-1)
        txt.setTextColorUneditable(-1)
        txt.setTextShadow(false)
        txt.setInvertHighlightedTextColor(false)
        txt.setBordered(false)
        txt.setMaxLength(40)
        txt.setResponder(this::onSearchUpdated)
        txt.setEditable(true)
        addRenderableWidget(txt)
        return txt
    }

    override fun init() {
        super.init()
        val xo = (width - imageWidth) / 2
        val yo = (height - imageHeight) / 2
        addressSearch = initSearchBar(xo+28, yo+59)
        menu.slotUpdateListener = { containerChanged() }
        menu.mailboxListUpdateListener = { containerChanged() }
        scrollToIndex()
        rebuildAddressButtons()
    }

    private fun rebuildAddressButtons() {
        val suggestName = if (addressSearch.value.isEmpty()) menu.filteredMailboxes.find { it.blockPos == menu.selectedMailboxPos }?.name?.string else null
        addressSearch.setSuggestion(suggestName)
        menu.updateFilteredMailboxes()
        miniButtons.forEach { removeWidget(it) }
        miniButtons.clear()

        val xo = leftPos
        val yo = topPos

        val visibleCount = menu.filteredMailboxes.size.coerceAtMost(MAX_VISIBLE_ROWS)
        for (i in 0 until visibleCount) {
            val index = startIndex + i
            menu.getMailbox(index)?.let { mailbox ->
                val miniButton = MiniAddressButton(
                    mailboxData = mailbox,
                    buttonX = xo+140,
                    buttonY = yo+19+i*8,
                    clickAction = {
                        if (menu.selectedMailboxPos == mailbox.blockPos) menu.selectedMailboxPos = null else menu.selectedMailboxPos = mailbox.blockPos
                        onSelectedMailbox()
                        rebuildAddressButtons()
                    },
                    enabledRequirement = { menu.selectedMailboxPos != mailbox.blockPos },
                    clickRequirement = { true }
                )
                miniButtons.add(miniButton)
                addRenderableWidget(miniButton)
            }
        }
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)
        val xo = leftPos
        val yo = topPos
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, xm: Int, ym: Int, a: Float) {
        val xo = leftPos
        val yo = topPos
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, xo, yo, 0f, 0f, imageWidth, imageHeight, 256, 256, -1)

        val sy = (52.0f * scrollOffs).toInt()
        val sprite = if (isScrollBarActive()) SCROLLER else SCROLLER_DISABLED
        val scrollerX = xo+152
        val scrollerY = yo+17 + sy
        graphics.blit(RenderPipelines.GUI_TEXTURED, sprite, scrollerX, scrollerY, 0f, 0f, 12, 15, 12, 15)
        if (xm >= scrollerX && xm < scrollerX + 12 && ym >= scrollerY && ym < scrollerY + 15) {
            graphics.requestCursor(if (scrolling) CursorTypes.RESIZE_NS else CursorTypes.POINTING_HAND)
        }

        menu.availableMailboxes.find { it.blockPos == menu.selectedMailboxPos }?.let { mailbox ->
            val color = ARGB.addRgb(mailbox.color, 0x333333)
            val darker = ARGB.multiply(color, 0x999999)
            val posText = mailbox.blockPos.let { Component.translatable("container.plantz.mailbox_coords", it.x, it.y, it.z) }.withColor(darker)
            val text = Component.translatable("chat.square_brackets", posText).withColor(darker)
            val line = font.split(text, 101).firstOrNull()
            graphics.blit(RenderPipelines.GUI_TEXTURED, MAILBOX_SELECTED, xo+24, yo+70, 0f, 0f, 101, 14, 101, 14, color)
            if (line!=null) graphics.text(font, line, xo+75 - font.width(line)/2, yo+73, -1, false)
        }

        if (miniButtons.isEmpty()) {
            val text = Component.translatable("container.plantz.no_address").withColor(0x777777)
            val line = font.split(text, 101).firstOrNull()
            if (line!=null) graphics.text(font, line, xo+75 - font.width(line)/2, yo+73, -1, false)
        }
    }

    override fun containerTick() {
        super.containerTick()
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        val xo = leftPos + 152
        val yo = topPos + 18
        if (event.x() >= xo && event.x() < xo + 12 && event.y() >= yo && event.y() < yo + 64) scrolling = true
        return super.mouseClicked(event, doubleClick)
    }

    override fun mouseDragged(event: MouseButtonEvent, dx: Double, dy: Double): Boolean {
        if (scrolling && isScrollBarActive()) {
            val yscr = topPos + 19
            val yscr2 = yscr + 64
            scrollOffs = (event.y().toFloat() - yscr - 7.5f) / (yscr2 - yscr - 15.0f)
            scrollOffs = Mth.clamp(scrollOffs, 0.0f, 1.0f)
            startIndex = (scrollOffs * getOffscreenRows() + 0.5).toInt()
            return true
        } else return super.mouseDragged(event, dx, dy)
    }

    override fun mouseReleased(event: MouseButtonEvent): Boolean {
        scrolling = false
        return super.mouseReleased(event)
    }
    
    override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {
        if (super.mouseScrolled(x, y, scrollX, scrollY)) return true
        else {
            if (isScrollBarActive()) {
                val offscreenRows: Int = getOffscreenRows()
                val scrolledDelta = scrollY.toFloat() / offscreenRows
                scrollOffs = Mth.clamp(scrollOffs - scrolledDelta, 0.0f, 1.0f)
                startIndex = (scrollOffs * offscreenRows + 0.5).toInt()
            }
            return true
        }
    }

    fun onSearchUpdated(searchString: String) {
        menu.searchFilter = searchString
        startIndex = 0
        scrollOffs = 0f
        rebuildAddressButtons()
    }

    fun onSelectedMailbox() {
        addressSearch.value = ""
        ClientPlayNetworking.send(UpdateCollectionBoxPayload(menu.data.blockPos, menu.selectedMailboxPos))
        scrollToIndex()
    }

    fun scrollToIndex() {
        val index = menu.filteredMailboxes.indexOfFirst { it.blockPos == menu.selectedMailboxPos }
        if (index == -1) return
        val size = menu.filteredMailboxes.size
        if (size == 0) return

        val maxStart = (size - MAX_VISIBLE_ROWS).coerceAtLeast(0)
        startIndex = index.coerceIn(0, maxStart)

        val offscreen = getOffscreenRows()
        scrollOffs = if (offscreen > 0) startIndex.toFloat() / offscreen else 0f
    }

    override fun keyPressed(event: KeyEvent): Boolean {
        if (event.isEscape) {
            this.minecraft.player!!.closeContainer()
            return true
        } else return if (!addressSearch.keyPressed(event) && !addressSearch.canConsumeInput()) super.keyPressed(event) else true
    }

    override fun resize(width: Int, height: Int) {
        val oldEditAddress: String = addressSearch.value
        this.init(width, height)
        addressSearch.setValue(oldEditAddress)
    }

    private fun isScrollBarActive(): Boolean = menu.filteredMailboxes.size > MAX_VISIBLE_ROWS

    private fun getOffscreenRows(): Int = (menu.filteredMailboxes.size - 1).coerceAtLeast(0)

    private fun containerChanged() {
        scrollToIndex()
        rebuildAddressButtons()
    }

}