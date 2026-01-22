package joshxviii.plantz

import joshxviii.plantz.item.SeedPacketItem
import joshxviii.plantz.raid.ZombieRaid
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BannerPattern

object PazCreativeTab {
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
                // seed packets
                output.accept(SeedPacketItem.stackFor(PazEntities.SUNFLOWER))
                output.accept(SeedPacketItem.stackFor(PazEntities.PEA_SHOOTER))
                output.accept(SeedPacketItem.stackFor(PazEntities.WALL_NUT))
                output.accept(SeedPacketItem.stackFor(PazEntities.CHOMPER))
                output.accept(SeedPacketItem.stackFor(PazEntities.CHERRY_BOMB))
                output.accept(SeedPacketItem.stackFor(PazEntities.POTATO_MINE))
                output.accept(SeedPacketItem.stackFor(PazEntities.ICE_PEA_SHOOTER))
                output.accept(SeedPacketItem.stackFor(PazEntities.REPEATER))
                output.accept(SeedPacketItem.stackFor(PazEntities.FIRE_PEA_SHOOTER))
                output.accept(SeedPacketItem.stackFor(PazEntities.CACTUS))
                output.accept(SeedPacketItem.stackFor(PazEntities.MELON_PULT))
                output.accept(SeedPacketItem.stackFor(PazEntities.PUFF_SHROOM))
                output.accept(SeedPacketItem.stackFor(PazEntities.FUME_SHROOM))
                output.accept(SeedPacketItem.stackFor(PazEntities.SUN_SHROOM))

                // zombie spawn eggs
                output.accept(PazItems.BROWN_COAT_SPAWN_EGG)
                output.accept(PazItems.ZOMBIE_YETI_SPAWN_EGG)

                //gnome
                output.accept(PazItems.GNOME_SPAWN_EGG)

                //other
                output.accept(PazItems.SUN)
                output.accept(PazBlocks.PLANT_POT)
                output.accept(PazItems.PLANT_POT_MINECART)
                output.accept(PazBlocks.CONE)
                output.accept(PazBlocks.BRAINZ_FLAG)
                //output.accept(ZombieRaid.getBrainzBannerInstance(parameters.holders().lookupOrThrow(Registries.BANNER_PATTERN)))
            }
            .build()
    )

    fun initialize() {}
}
