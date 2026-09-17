package joshxviii.plantz.block

import net.minecraft.util.StringRepresentable

enum class MailboxState(val stateName: String) : StringRepresentable {
    INACTIVE("inactive"),
    HAS_MAIL("has_mail"),
    EJECTING("ejecting");

    override fun getSerializedName(): String = this.stateName
}

enum class CollectionBoxState(val stateName: String) : StringRepresentable {
    OPEN("open"),
    CLOSED("closed");

    override fun getSerializedName(): String = this.stateName
}