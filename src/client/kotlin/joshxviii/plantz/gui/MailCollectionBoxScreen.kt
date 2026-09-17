package joshxviii.plantz.gui

import com.mojang.blaze3d.platform.cursor.CursorTypes
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.block.entity.MailCollectionBoxEntity
import joshxviii.plantz.inventory.MailCollectionBoxMenu
import joshxviii.plantz.inventory.MailboxMenu
import joshxviii.plantz.networking.SendMailRequestPayload
import joshxviii.plantz.networking.UpdateCollectionBoxPayload
import joshxviii.plantz.pazResource
import joshxviii.plantz.renderer.outlineText
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.util.Mth
import net.minecraft.util.datafix.ExtraDataFixUtils.blockState
import net.minecraft.world.entity.player.Inventory

class MailCollectionBoxScreen(
    val menu: MailCollectionBoxMenu,
    val inventory: Inventory,
    title: Component,
) : AbstractContainerScreen<MailCollectionBoxMenu>(menu, inventory, title, 176, 180) {
    private lateinit var addressSearch: EditBox
    private lateinit var sendButton: Button
    private val addressButtons = mutableListOf<AddressButton>()
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
        val MAILBOX_SELECTED: Identifier = pazResource("textures/gui/mail_collection_box/selected.png")
        val MAILBOX_UNSELECTED: Identifier = pazResource("textures/gui/mail_collection_box/unselected.png")
        val SCROLLER: Identifier = pazResource("textures/gui/mailbox/scroller.png")
        val SCROLLER_DISABLED: Identifier = pazResource("textures/gui/mailbox/scroller_disabled.png")
    }

    fun initSearchBar(x: Int, y: Int): EditBox {
        val txt = EditBox(font, x, y, 94, 12, Component.translatable("container.plantz.address_search"));
        val l =
        txt.setCanLoseFocus(false)
        txt.setTextColor(-1)
        txt.setTextColorUneditable(-1)
        txt.setInvertHighlightedTextColor(false)
        txt.setBordered(false)
        txt.setMaxLength(50)
        txt.setResponder(this::onSearchUpdated)
        txt.setEditable(true)
        addRenderableWidget(txt)
        return txt
    }

    override fun init() {
        menu.selectedMailboxIndex?.let { startIndex = it }
        super.init()
        val xo = (width - imageWidth) / 2
        val yo = (height - imageHeight) / 2
        addressSearch = initSearchBar(xo+40, yo+59)
        menu.slotUpdateListener = { containerChanged() }
        menu.mailboxListUpdateListener = { containerChanged() }
        rebuildAddressButtons()
    }

    private fun rebuildAddressButtons() {
        menu.updateFilteredMailboxes()
        addressButtons.forEach { removeWidget(it) }
        addressButtons.clear()

        val xo = leftPos
        val yo = topPos

        val visibleCount = menu.filteredMailboxes.size.coerceAtMost(1)
        for (i in 0 until visibleCount) {
            val mailboxIndex = startIndex + i
            menu.getMailbox(mailboxIndex)?.let { mailbox ->
                val button = AddressButton(
                    mailboxData = mailbox,
                    buttonX = xo+39,
                    buttonY = yo+70 + i * 14,
                    clickAction = {
                        if (menu.selectedMailboxIndex == mailboxIndex) menu.selectedMailboxIndex = null else menu.selectedMailboxIndex = mailboxIndex
                        rebuildAddressButtons()
                        ClientPlayNetworking.send(UpdateCollectionBoxPayload(menu.data.blockPos, mailbox.blockPos))
                    },
                    enabledRequirement = { menu.selectedMailboxIndex != mailboxIndex },
                    clickRequirement = { true }
                )
                addressButtons.add(button)
                addRenderableWidget(button)
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

        val sy = (41.0f * scrollOffs).toInt()
        val sprite = if (isScrollBarActive()) SCROLLER else SCROLLER_DISABLED
        val scrollerX = xo+152
        val scrollerY = yo+28 + sy
        graphics.blit(RenderPipelines.GUI_TEXTURED, sprite, scrollerX, scrollerY, 0f, 0f, 12, 15, 12, 15)
        if (xm >= scrollerX && xm < scrollerX + 12 && ym >= scrollerY && ym < scrollerY + 15) {
            graphics.requestCursor(if (scrolling) CursorTypes.RESIZE_NS else CursorTypes.POINTING_HAND)
        }

//        graphics.blit(RenderPipelines.GUI_TEXTURED,
//            if (menu.selectedMailboxIndex != null) MAILBOX_SELECTED else MAILBOX_UNSELECTED,
//            xo+39, yo+70, 0f, 0f, 97, 14, 97, 14, -1)


        // show message when no addresses are available
        //if (addressButtons.isEmpty()) graphics.textWithWordWrap(font, Component.translatable("container.plantz.no_address"), xo+52, yo+28, 96, -1)
    }

    override fun containerTick() {
        super.containerTick()
    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        val xo = leftPos + 152
        val yo = topPos + 28
        if (event.x() >= xo && event.x() < xo + 12 && event.y() >= yo && event.y() < yo + 54) scrolling = true

        return super.mouseClicked(event, doubleClick)
    }

    override fun mouseDragged(event: MouseButtonEvent, dx: Double, dy: Double): Boolean {
        if (scrolling && isScrollBarActive()) {
            val yscr = topPos + 29
            val yscr2 = yscr + 54
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

    private fun isScrollBarActive(): Boolean = menu.filteredMailboxes.size > 4

    private fun getOffscreenRows(): Int = (menu.filteredMailboxes.size - 1).coerceAtLeast(0)

    private fun containerChanged() {
        rebuildAddressButtons()
    }

}