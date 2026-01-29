package joshxviii.plantz.block

import net.minecraft.util.StringRepresentable

enum class MailboxState(val stateName: String) : StringRepresentable {
    INACTIVE("inactive"),
    HAS_MAIL("has_mail"),
    EJECTING("ejecting");

    override fun getSerializedName(): String {
        return this.stateName
    }
}