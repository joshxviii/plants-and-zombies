package joshxviii.plantz.gui

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.block.entity.MailboxBlockEntity
import joshxviii.plantz.pazResource
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.components.WidgetTooltipHolder
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack

/**
 * Button to populate list with
 */
class AddressButton(
    val address: MailboxBlockEntity,
    buttonX: Int,
    buttonY: Int,
    clickAction: OnPress,
    enabledRequirement: ((button: PazButton) -> Boolean) = { true },
    clickRequirement: ((button: PazButton) -> Boolean) = enabledRequirement,
) : PazButton(buttonX, buttonY, 97, 14, clickAction, ADDRESS, ADDRESS_HIGHLIGHTED, ADDRESS_SELECTED, enabledRequirement, clickRequirement, address.name) {
    val posText: MutableComponent =
        Component.literal("■").withColor(address.color.textColor)
            .append(CommonComponents.space())
            .append(Component.translatable("container.plantz.mailbox_coords", address.blockPos.x, address.blockPos.y, address.blockPos.z).withColor(0xFFFFFFF))

    init {
        setTooltip(Tooltip.create(posText))
    }

    companion object {
        val ADDRESS: Identifier = pazResource("textures/gui/mailbox/address.png")
        val ADDRESS_HIGHLIGHTED: Identifier = pazResource("textures/gui/mailbox/address_highlighted.png")
        val ADDRESS_SELECTED: Identifier = pazResource("textures/gui/mailbox/address_selected.png")
    }

    override fun renderContents(graphics: GuiGraphics, mx: Int, my: Int, a: Float) {
        super.renderContents(graphics, mx, my, a)
        val font = Minecraft.getInstance().font
    }

}