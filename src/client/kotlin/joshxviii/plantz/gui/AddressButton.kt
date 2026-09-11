package joshxviii.plantz.gui

import joshxviii.plantz.MailboxData
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.block.entity.MailboxBlockEntity
import joshxviii.plantz.inventory.MailboxMenu
import joshxviii.plantz.pazResource
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB

/**
 * Address button used to populate mailbox list gui
 */
class AddressButton(
    val menuData: MailboxData,
    val mailboxData: MailboxData,
    buttonX: Int,
    buttonY: Int,
    clickAction: OnPress,
    enabledRequirement: ((button: PazButton) -> Boolean) = { true },
    clickRequirement: ((button: PazButton) -> Boolean) = enabledRequirement,
) : PazButton(buttonX, buttonY, 97, 14, clickAction, ADDRESS, ADDRESS_HIGHLIGHTED, ADDRESS_SELECTED, enabledRequirement, clickRequirement, mailboxData.name, ARGB.addRgb(menuData.color, 0x333333)) {
    val posText: MutableComponent =
        Component.translatable("container.plantz.mailbox_coords", mailboxData.blockPos.x, mailboxData.blockPos.y, mailboxData.blockPos.z).withColor(0xFFFFFFF)

    init {
        setTooltip(Tooltip.create(posText))
    }

    companion object {
        val ADDRESS: Identifier = pazResource("textures/gui/mailbox/address.png")
        val ADDRESS_HIGHLIGHTED: Identifier = pazResource("textures/gui/mailbox/address_highlighted.png")
        val ADDRESS_SELECTED: Identifier = pazResource("textures/gui/mailbox/address_selected.png")
    }

    override fun extractContents(graphics: GuiGraphicsExtractor, mx: Int, my: Int, a: Float) {
        super.extractContents(graphics, mx, my, a)
        val font = Minecraft.getInstance().font
        val pressed = isPressed()
        graphics.outlineText(font, Component.literal("●"), buttonX+buttonWidth-8 + if(pressed) 1 else 0, buttonY-4+(buttonHeight/2), color = mailboxData.color, outlineColor = ARGB.multiply(mailboxData.color, 0x444444))
    }

}