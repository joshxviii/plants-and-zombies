package joshxviii.plantz.gui

import com.mojang.blaze3d.platform.cursor.CursorTypes
import joshxviii.plantz.inventory.MailboxMenu
import joshxviii.plantz.pazResource
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory

class MailboxScreen(
    val menu: MailboxMenu,
    val inventory: Inventory,
    title: Component
) : AbstractContainerScreen<MailboxMenu>(menu, inventory, title, 176, 180) {
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
        val BACKGROUND: Identifier = pazResource("textures/gui/mailbox/background.png")
        val SEND_BUTTON: Identifier = pazResource("textures/gui/mailbox/send_button.png")
        val SEND_BUTTON_HOVER: Identifier = pazResource("textures/gui/mailbox/send_button_hover.png")
        val SEND_BUTTON_PRESS: Identifier = pazResource("textures/gui/mailbox/send_button_press.png")
        val SCROLLER: Identifier = pazResource("textures/gui/mailbox/scroller.png")
        val SCROLLER_DISABLED: Identifier = pazResource("textures/gui/mailbox/scroller_disabled.png")
    }

    init {
        //TODO get all active in level mailboxes from MailboxMenu
    }

    fun initSearchBar(x: Int, y: Int): EditBox {
        val txt = EditBox(font, x, y, 94, 12, Component.translatable("container.plantz.address_search"));
        txt.setCanLoseFocus(false)
        txt.setTextColor(-1)
        txt.setTextColorUneditable(-1)
        txt.setInvertHighlightedTextColor(false)
        txt.setBordered(false)
        txt.setMaxLength(50)
        txt.setResponder(this::onSearchUpdated)
        //txt.setValue("")
        txt.setEditable(true)
        addRenderableWidget(txt)
        return txt
    }

    fun initSendButton(x: Int, y: Int): Button {
        val btn = PazButton(x, y, 20, 14,
            { onSendPressed() },
            SEND_BUTTON, SEND_BUTTON_HOVER, SEND_BUTTON_PRESS,
            { menu.mailSlot.hasItem() && menu.selectedMailboxIndex != null },
            { menu.mailSlot.hasItem() && menu.selectedMailboxIndex != null }
        )
        addRenderableWidget(btn)
        return btn
    }

    override fun init() {
        super.init()
        val xo = (width - imageWidth) / 2
        val yo = (height - imageHeight) / 2
        addressSearch = initSearchBar(xo+53, yo+17)
        sendButton = initSendButton(xo+18, yo+55)
        menu.registerUpdateListener { containerChanged() }
        menu.refreshMailboxList()
        rebuildAddressButtons()
    }

    private fun rebuildAddressButtons() {
        // Clear existing buttons
        addressButtons.forEach { removeWidget(it) }
        addressButtons.clear()

        val xo = leftPos
        val yo = topPos

        val visibleCount = menu.filteredMailboxes.size.coerceAtMost(4)
        for (i in 0 until visibleCount) {
            val mailboxIndex = startIndex + i
            val mailbox  = menu.getMailbox(mailboxIndex)
            if (mailbox != null) {
                val button = AddressButton(
                    address = mailbox.name,
                    color = mailbox.color,
                    buttonX = xo + 52,
                    buttonY = yo + 28 + i * 14,
                    clickAction = {
                        if (menu.selectedMailboxIndex == mailboxIndex) menu.selectedMailboxIndex = null else menu.selectedMailboxIndex = mailboxIndex
                        rebuildAddressButtons()
                    },
                    enabledRequirement = { menu.selectedMailboxIndex != mailboxIndex },
                    clickRequirement = { true }
                )
                addressButtons.add(button)
                addRenderableWidget(button)
            }
        }
    }

    override fun renderBg(
        graphics: GuiGraphics,
        a: Float,
        xm: Int,
        ym: Int
    ) {
        val xo = leftPos
        val yo = topPos
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, xo, yo, 0f, 0f, imageWidth, imageHeight, 256, 256)

        val sy = (41.0f * scrollOffs).toInt()
        val sprite = if (isScrollBarActive()) SCROLLER else SCROLLER_DISABLED
        val scrollerX = xo + 152
        val scrollerY = yo + 28 + sy
        graphics.blit(RenderPipelines.GUI_TEXTURED, sprite, scrollerX, scrollerY, 0f, 0f, 12, 15, 12, 15)
        if (xm >= scrollerX && xm < scrollerX + 12 && ym >= scrollerY && ym < scrollerY + 15) {
            graphics.requestCursor(if (scrolling) CursorTypes.RESIZE_NS else CursorTypes.POINTING_HAND)
        }

    }

    override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
        val xo = leftPos + 152
        val yo = topPos + 28
        if (event.x() >= xo && event.x() < xo + 12 && event.y() >= yo && event.y() < yo + 54) scrolling = true

        return super.mouseClicked(event, doubleClick)
    }

    override fun mouseDragged(event: MouseButtonEvent, dx: Double, dy: Double): Boolean {
        if (scrolling && isScrollBarActive()) {
            val yscr = topPos + 14
            val yscr2 = yscr + 54
            scrollOffs = (event.y().toFloat() - yscr - 7.5f) / (yscr2 - yscr - 15.0f)
            scrollOffs = Mth.clamp(scrollOffs, 0.0f, 1.0f)
            startIndex = (scrollOffs * getOffscreenRows() + 0.5).toInt() * 4
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
                startIndex = (scrollOffs * offscreenRows + 0.5).toInt() * 4
            }
            return true
        }
    }

    fun onSendPressed() {
        if (menu.sendMail()) {
            //TODO
            //minecraft?.player?.closeContainer()
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
        val oldEdit: String = addressSearch.value
        this.init(width, height)
        addressSearch.setValue(oldEdit)
    }

    private fun isScrollBarActive(): Boolean = menu.filteredMailboxes.size > 4

    private fun getOffscreenRows(): Int = (menu.filteredMailboxes.size - 4).coerceAtLeast(0)

    private fun containerChanged() {
        rebuildAddressButtons()
    }

}