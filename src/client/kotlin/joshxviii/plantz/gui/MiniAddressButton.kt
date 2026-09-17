package joshxviii.plantz.gui

import joshxviii.plantz.MailboxData
import joshxviii.plantz.pazResource
import joshxviii.plantz.renderer.outlineText
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.ChatComponent
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB

/**
 * Mini address button used to populate list in collection box gui
 */
class MiniAddressButton(
    val menuData: MailboxData? = null,
    val mailboxData: MailboxData,
    buttonX: Int,
    buttonY: Int,
    clickAction: OnPress,
    enabledRequirement: ((button: PazButton) -> Boolean) = { true },
    clickRequirement: ((button: PazButton) -> Boolean) = enabledRequirement,
) : PazButton(buttonX, buttonY, 8, 8, clickAction, ADDRESS, ADDRESS_HIGHLIGHTED, ADDRESS_SELECTED, enabledRequirement, clickRequirement, Component.empty(), menuData?.let { ARGB.addRgb(it.color, 0x333333) }?: -1) {
    val posText: MutableComponent =
        mailboxData.name.copy()
            .append(Component.literal(" "))
            .append(Component.translatable("container.plantz.mailbox_coords", mailboxData.blockPos.x, mailboxData.blockPos.y, mailboxData.blockPos.z).withColor(0xFFFFFFF))

    init {
        setTooltip(Tooltip.create(posText))
    }

    companion object {
        val ADDRESS: Identifier = pazResource("textures/gui/mail_collection_box/unselected.png")
        val ADDRESS_HIGHLIGHTED: Identifier = pazResource("textures/gui/mail_collection_box/highlighted.png")
        val ADDRESS_SELECTED: Identifier = pazResource("textures/gui/mail_collection_box/selected.png")
    }

    override fun extractContents(graphics: GuiGraphicsExtractor, mx: Int, my: Int, a: Float) {
        super.extractContents(graphics, mx, my, a)
        val font = Minecraft.getInstance().font
        val pressed = isPressed()
        graphics.outlineText(font, Component.literal("•"), buttonX+buttonWidth-5, buttonY-4+(buttonHeight/2), color = mailboxData.color, outlineColor = ARGB.multiply(mailboxData.color, 0x444444))
    }

}