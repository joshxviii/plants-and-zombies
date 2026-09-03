package joshxviii.plantz.gui

import joshxviii.plantz.MailboxData
import joshxviii.plantz.block.entity.MailboxBlockEntity
import joshxviii.plantz.pazResource
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier

/**
 * Address button used to populate mailbox list gui
 */
class AddressButton(
    val mailboxData: MailboxData,
    buttonX: Int,
    buttonY: Int,
    clickAction: OnPress,
    enabledRequirement: ((button: PazButton) -> Boolean) = { true },
    clickRequirement: ((button: PazButton) -> Boolean) = enabledRequirement,
) : PazButton(buttonX, buttonY, 97, 14, clickAction, ADDRESS, ADDRESS_HIGHLIGHTED, ADDRESS_SELECTED, enabledRequirement, clickRequirement, mailboxData.name) {
    val posText: MutableComponent =
        Component.literal("■").withColor(mailboxData.color)
            .append(CommonComponents.space())
            .append(Component.translatable("container.plantz.mailbox_coords", mailboxData.blockPos.x, mailboxData.blockPos.y, mailboxData.blockPos.z).withColor(0xFFFFFFF))

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
    }

}