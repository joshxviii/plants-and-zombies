package joshxviii.plantz.gui

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.pazResource
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.DyeItem

/**
 * Button to populate list with
 */
class AddressButton(
    val address: Component = Component.empty(),
    val color: DyeColor = DyeColor.WHITE,
    buttonX: Int,
    buttonY: Int,
    clickAction: OnPress,
    enabledRequirement: ((button: PazButton) -> Boolean) = { true },
    clickRequirement: ((button: PazButton) -> Boolean) = enabledRequirement,
) : PazButton(buttonX, buttonY, 97, 14, clickAction, ADDRESS, ADDRESS_HIGHLIGHTED, ADDRESS_SELECTED, enabledRequirement, clickRequirement, address) {

    companion object {
        val ADDRESS: Identifier = pazResource("textures/gui/mailbox/address.png")
        val ADDRESS_HIGHLIGHTED: Identifier = pazResource("textures/gui/mailbox/address_highlighted.png")
        val ADDRESS_SELECTED: Identifier = pazResource("textures/gui/mailbox/address_selected.png")
    }

    override fun renderContents(graphics: GuiGraphics, mx: Int, my: Int, a: Float) {
        super.renderContents(graphics, mx, my, a)
        val itemStack = PazBlocks.mailboxByColor[color]?.asItem()?.defaultInstance?: return
        graphics.renderFakeItem(itemStack, buttonX+buttonWidth-16, buttonY-1)
    }

}