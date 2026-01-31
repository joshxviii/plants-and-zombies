package joshxviii.plantz

import joshxviii.plantz.inventory.MailboxMenu
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.MenuType.MenuSupplier

object PazMenus {

    @JvmField val MAILBOX_MENU = register("mailbox", ::MailboxMenu)

    private fun <T : AbstractContainerMenu> register(name: String, constructor: MenuSupplier<T>): MenuType<T> {
        val id = pazResource(name)
        return Registry.register(
            BuiltInRegistries.MENU, id, MenuType(constructor, FeatureFlags.VANILLA_SET)
        )
    }

    fun initialize() {}
}