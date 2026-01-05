package joshxviii.plantz

import joshxviii.plantz.item.SeedPacketItem
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object PazTabs {
    // Define the key for the custom tab
    private val PAZ_TAB_KEY: ResourceKey<CreativeModeTab> =
        ResourceKey.create(Registries.CREATIVE_MODE_TAB, pazResource("plantz_tab"))

    // Register the tab
    val PAZ_TAB: CreativeModeTab = Registry.register(
        BuiltInRegistries.CREATIVE_MODE_TAB,
        PAZ_TAB_KEY,
        CreativeModeTab.builder(CreativeModeTab.Row.BOTTOM, 0)
            .title(Component.translatable("itemGroup.plantz.plantz_tab"))
            .icon { ItemStack(PazItems.SUN) }

            .displayItems { parameters, output ->
                output.accept(PazItems.SUN)
                // Seed packets for each plant type
                output.accept(SeedPacketItem.stackFor(PazEntities.SUNFLOWER))
                output.accept(SeedPacketItem.stackFor(PazEntities.PEA_SHOOTER))
                output.accept(SeedPacketItem.stackFor(PazEntities.WALL_NUT))
                output.accept(SeedPacketItem.stackFor(PazEntities.CHOMPER))
                output.accept(SeedPacketItem.stackFor(PazEntities.CHERRY_BOMB))
                output.accept(SeedPacketItem.stackFor(PazEntities.POTATO_MINE))
                output.accept(SeedPacketItem.stackFor(PazEntities.ICE_PEA))
                output.accept(SeedPacketItem.stackFor(PazEntities.REPEATER))
            }

            .build()
    )

    fun initialize() {}
}
