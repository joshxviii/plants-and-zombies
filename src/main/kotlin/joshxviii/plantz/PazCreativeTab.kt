package joshxviii.plantz

import joshxviii.plantz.item.BlueprintItem
import joshxviii.plantz.item.SeedPacketItem
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

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
                output.accept(SeedPacketItem.stackFor(PazEntities.EXPLODE_O_NUT))
                output.accept(SeedPacketItem.stackFor(PazEntities.CHOMPER))
                output.accept(SeedPacketItem.stackFor(PazEntities.CHERRY_BOMB))
                output.accept(SeedPacketItem.stackFor(PazEntities.POTATO_MINE))
                output.accept(SeedPacketItem.stackFor(PazEntities.REPEATER))
                output.accept(SeedPacketItem.stackFor(PazEntities.ICE_PEA_SHOOTER))
                output.accept(SeedPacketItem.stackFor(PazEntities.FIRE_PEA_SHOOTER))
                output.accept(SeedPacketItem.stackFor(PazEntities.ELECTRIC_PEA_SHOOTER))
                output.accept(SeedPacketItem.stackFor(PazEntities.CACTUS))
                output.accept(SeedPacketItem.stackFor(PazEntities.LIGHTNING_REED))
                output.accept(SeedPacketItem.stackFor(PazEntities.CABBAGE_PULT))
                output.accept(SeedPacketItem.stackFor(PazEntities.KERNEL_PULT))
                output.accept(SeedPacketItem.stackFor(PazEntities.MELON_PULT))
                output.accept(SeedPacketItem.stackFor(PazEntities.BONK_CHOY))
                output.accept(SeedPacketItem.stackFor(PazEntities.TANGLE_KELP))
                output.accept(SeedPacketItem.stackFor(PazEntities.SUN_SHROOM))
                output.accept(SeedPacketItem.stackFor(PazEntities.PUFF_SHROOM))
                output.accept(SeedPacketItem.stackFor(PazEntities.FUME_SHROOM))
                output.accept(SeedPacketItem.stackFor(PazEntities.SCAREDY_SHROOM))
                output.accept(SeedPacketItem.stackFor(PazEntities.HYPNOSHROOM))
                output.accept(SeedPacketItem.stackFor(PazEntities.DOOM_SHROOM))
                output.accept(SeedPacketItem.stackFor(PazEntities.SEA_SHROOM))
                output.accept(SeedPacketItem.stackFor(PazEntities.COFFEE_BEAN))
                output.accept(SeedPacketItem.stackFor(PazEntities.GRAVE_BUSTER))

                // zombie spawn eggs
                output.accept(PazItems.BROWN_COAT_SPAWN_EGG)
                output.accept(PazItems.NEWSPAPER_ZOMBIE_SPAWN_EGG)
                output.accept(PazItems.DIGGER_ZOMBIE_SPAWN_EGG)
                output.accept(PazItems.ENGINEER_ZOMBIE_SPAWN_EGG)
                output.accept(PazItems.BACKUP_DANCER_SPAWN_EGG)
                output.accept(PazItems.DISCO_ZOMBIE_SPAWN_EGG)
                output.accept(PazItems.ALL_STAR_SPAWN_EGG)
                output.accept(PazItems.ZOMBIE_YETI_SPAWN_EGG)
                output.accept(PazItems.IMP_SPAWN_EGG)
                output.accept(PazItems.SOLDIER_ZOMBIE_SPAWN_EGG)
                output.accept(PazItems.ROBO_ZOMBIE_SPAWN_EGG)
                output.accept(PazItems.PIRATE_CAPTAIN_SPAWN_EGG)
                output.accept(PazItems.SUPER_BRAINZ_SPAWN_EGG)
                output.accept(PazItems.GARGANTUAR_SPAWN_EGG)

                // machine blueprints
                //output.accept(BlueprintItem.stackFor(PazEntities.ZOMBIE_TURRET))
                //output.accept(BlueprintItem.stackFor(PazEntities.ELECTRO_TURRET))
                //output.accept(BlueprintItem.stackFor(PazEntities.ZOMBIE_DRONE))
                //output.accept(BlueprintItem.stackFor(PazEntities.LAWN_MOWER))

                // gnome
                if (parameters.hasPermissions()) output.accept(PazItems.GNOME_SPAWN_EGG)

                // items + blocks
                output.accept(PazItems.SUN_BATTERY)
                output.accept(PazItems.SUN)
                output.accept(PazItems.SUN_BOTTLE)
                output.accept(PazItems.WATERING_CAN)
                output.accept(PazBlocks.PLANT_POT)
                output.accept(PazBlocks.ZEN_PLANT_POT)
                output.accept(PazItems.PLANT_POT_MINECART)
                output.accept(PazItems.PLANT_POT_HELMET)
                output.accept(PazItems.DUCKY_TUBE)
                output.accept(PazBlocks.CONE)
                output.accept(PazItems.NEWSPAPER)
                output.accept(PazItems.FOOTBALL_HELMET)
                output.accept(PazItems.DYE_BLASTER)
                output.accept(PazBlocks.BRAINZ_FLAG)
                output.accept(PazBlocks.PLANTZ_FLAG)
                output.accept(PazItems.TACO)
                if (parameters.hasPermissions()) output.accept(PazItems.BRAINZIUM)
                output.accept(PazItems.BRAINZ_ALLOY)
                output.accept(PazBlocks.BRAINZ_ALLOY_BLOCK)
                output.accept(PazBlocks.BRAINZ_ALLOY_STAIRS)
                output.accept(PazBlocks.BRAINZ_ALLOY_SLAB)
                output.accept(PazBlocks.TREADED_BRAINZ_ALLOY_BLOCK)
                output.accept(PazBlocks.REINFORCED_BRAINZ_ALLOY_BLOCK)
                output.accept(PazBlocks.BRAINZ_ALLOY_FENCE)
                output.accept(PazBlocks.GRAVESTONE)
                output.accept(PazBlocks.BLUE_GARDEN_GNOME)
                output.accept(PazBlocks.GREEN_GARDEN_GNOME)
                output.accept(PazBlocks.RED_GARDEN_GNOME)
                output.accept(PazBlocks.YELLOW_GARDEN_GNOME)
                if (parameters.hasPermissions()) output.accept(PazBlocks.TIME_MACHINE)

                // music
                output.accept(PazItems.MUSIC_DISC_GRASSY_GROOVE)

                // balloons
                PazItems.balloonByColor.forEach { output.accept(it.value) }

                // mailboxes
                PazBlocks.mailboxByColor.forEach { output.accept(it.value) }
            }
            .build()
    )

    fun initialize() {}
}
