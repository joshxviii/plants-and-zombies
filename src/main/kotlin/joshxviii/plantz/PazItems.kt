package joshxviii.plantz

import joshxviii.plantz.PazEntities.ALL_STAR
import joshxviii.plantz.PazEntities.BACKUP_DANCER
import joshxviii.plantz.PazEntities.BROWN_COAT
import joshxviii.plantz.PazEntities.DIGGER_ZOMBIE
import joshxviii.plantz.PazEntities.DISCO_ZOMBIE
import joshxviii.plantz.PazEntities.ENGINEER_ZOMBIE
import joshxviii.plantz.PazEntities.GARGANTUAR
import joshxviii.plantz.PazEntities.GNOME
import joshxviii.plantz.PazEntities.IMP
import joshxviii.plantz.PazEntities.NEWSPAPER_ZOMBIE
import joshxviii.plantz.PazEntities.PIRATE_CAPTAIN
import joshxviii.plantz.PazEntities.ROBO_ZOMBIE
import joshxviii.plantz.PazEntities.SOLDIER_ZOMBIE
import joshxviii.plantz.PazEntities.SUPER_BRAINZ
import joshxviii.plantz.PazEntities.ZOMBIE_YETI
import joshxviii.plantz.item.*
import joshxviii.plantz.item.component.BrainzAlloyCost
import joshxviii.plantz.item.component.BlocksProjectileDamage
import joshxviii.plantz.item.component.StoredSun
import joshxviii.plantz.item.component.StoredWater
import joshxviii.plantz.item.component.SunCost
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents
import net.fabricmc.fabric.api.registry.FuelValueEvents
import net.fabricmc.fabric.impl.item.ItemComponentTooltipProviderRegistryImpl
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponents
import net.minecraft.core.dispenser.BlockSource
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior
import net.minecraft.core.dispenser.MinecartDispenseItemBehavior
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.*
import net.minecraft.world.item.Items.GLASS_BOTTLE
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.component.UseCooldown
import net.minecraft.world.item.equipment.ArmorMaterials
import net.minecraft.world.item.equipment.ArmorType
import net.minecraft.world.item.equipment.EquipmentAssets
import net.minecraft.world.item.equipment.Equippable
import net.minecraft.world.level.block.ComposterBlock
import net.minecraft.world.level.block.DispenserBlock
import java.util.function.Function

object PazItems {
    @JvmField
    val SUN: Item = registerItem(
        "sun", ::SunItem,
        properties = Item.Properties()
    )
    @JvmField
    val SUN_BOTTLE: Item = registerItem(
        "sun_bottle", ::SunBottleItem,
        properties = Item.Properties().craftRemainder(GLASS_BOTTLE)
    )
    @JvmField
    val SUN_BATTERY: Item = registerItem(
        "sun_battery", ::SunBatteryItem,
        properties = Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE)
            .component(PazComponents.STORED_SUN, StoredSun())
    )
    @JvmField
    val WATERING_CAN: Item = registerItem(
        "watering_can", ::WateringCanItem,
        properties = Item.Properties()
            .stacksTo(1)
            .component(PazComponents.STORED_WATER, StoredWater())
    )
    @JvmField
    val BRAINZIUM: Item = registerItem(
        "brainzium",
        properties = Item.Properties()
            .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    )
    @JvmField
    val BRAINZ_ALLOY: Item = registerItem(
        "brainz_alloy",
        properties = Item.Properties()
    )
    @JvmField
    val NEWSPAPER: Item = registerItem(
        "newspaper", ::NewspaperItem,
        properties = Item.Properties()
            .component(PazComponents.BLOCKS_PROJECTILE_DAMAGE, BlocksProjectileDamage(
                slot = EquipmentSlotGroup.HAND,
                breakChance = 0.1f,
                mustBeUsing = true
            )
            ).component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)
    )
    @JvmField
    val TACO: Item = registerItem(
        "taco",
        properties = Item.Properties().food(FoodProperties.Builder().nutrition(9).saturationModifier(1.1f).build())
    )
    const val DUCKY_TUBE_DAMAGE_INTERVAL = 45
    val DUCKY_EQUIP_ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID, pazResource("ducky_tube"))
    @JvmField
    val DUCKY_TUBE: Item = registerItem(
        "ducky_tube", ::DuckyTubeItem,
        properties = Item.Properties()
            .durability(225)
            .attributes(
                ItemAttributeModifiers.builder()
                    .add(
                        Attributes.WATER_MOVEMENT_EFFICIENCY,
                        AttributeModifier(pazResource("ducky_tube"), 1.5, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.LEGS
                    ).build())
            .component(DataComponents.EQUIPPABLE,
                Equippable.builder(EquipmentSlot.LEGS)
                .setAsset(DUCKY_EQUIP_ASSET)
                .build())
    )
    @JvmField val WHITE_BALLOON: Item = registerItem("white_balloon", {BalloonItem(it, DyeColor.WHITE)}, properties = Item.Properties())
    @JvmField val LIGHT_GRAY_BALLOON: Item = registerItem("light_gray_balloon", {BalloonItem(it, DyeColor.LIGHT_GRAY)}, properties = Item.Properties())
    @JvmField val GRAY_BALLOON: Item = registerItem("gray_balloon", {BalloonItem(it, DyeColor.GRAY)}, properties = Item.Properties())
    @JvmField val BLACK_BALLOON: Item = registerItem("black_balloon", {BalloonItem(it, DyeColor.BLACK)}, properties = Item.Properties())
    @JvmField val BROWN_BALLOON: Item = registerItem("brown_balloon", {BalloonItem(it, DyeColor.BROWN)}, properties = Item.Properties())
    @JvmField val RED_BALLOON: Item = registerItem("red_balloon", {BalloonItem(it, DyeColor.RED)}, properties = Item.Properties())
    @JvmField val ORANGE_BALLOON: Item = registerItem("orange_balloon", {BalloonItem(it, DyeColor.ORANGE)}, properties = Item.Properties())
    @JvmField val YELLOW_BALLOON: Item = registerItem("yellow_balloon", {BalloonItem(it, DyeColor.YELLOW)}, properties = Item.Properties())
    @JvmField val LIME_BALLOON: Item = registerItem("lime_balloon", {BalloonItem(it, DyeColor.LIME)}, properties = Item.Properties())
    @JvmField val GREEN_BALLOON: Item = registerItem("green_balloon", {BalloonItem(it, DyeColor.GREEN)}, properties = Item.Properties())
    @JvmField val CYAN_BALLOON: Item = registerItem("cyan_balloon", {BalloonItem(it, DyeColor.CYAN)}, properties = Item.Properties())
    @JvmField val LIGHT_BLUE_BALLOON: Item = registerItem("light_blue_balloon", {BalloonItem(it, DyeColor.LIGHT_BLUE)}, properties = Item.Properties())
    @JvmField val BLUE_BALLOON: Item = registerItem("blue_balloon", {BalloonItem(it, DyeColor.BLUE)}, properties = Item.Properties())
    @JvmField val PURPLE_BALLOON: Item = registerItem("purple_balloon", {BalloonItem(it, DyeColor.PURPLE)}, properties = Item.Properties())
    @JvmField val MAGENTA_BALLOON: Item = registerItem("magenta_balloon", {BalloonItem(it, DyeColor.MAGENTA)}, properties = Item.Properties())
    @JvmField val PINK_BALLOON: Item = registerItem("pink_balloon", {BalloonItem(it, DyeColor.PINK)}, properties = Item.Properties())
    val balloonByColor = mapOf(
        DyeColor.WHITE to      WHITE_BALLOON,
        DyeColor.LIGHT_GRAY to LIGHT_GRAY_BALLOON,
        DyeColor.GRAY to       GRAY_BALLOON,
        DyeColor.BLACK to      BLACK_BALLOON,
        DyeColor.BROWN to      BROWN_BALLOON,
        DyeColor.RED to        RED_BALLOON,
        DyeColor.ORANGE to     ORANGE_BALLOON,
        DyeColor.YELLOW to     YELLOW_BALLOON,
        DyeColor.LIME to       LIME_BALLOON,
        DyeColor.GREEN to      GREEN_BALLOON,
        DyeColor.CYAN to       CYAN_BALLOON,
        DyeColor.LIGHT_BLUE to LIGHT_BLUE_BALLOON,
        DyeColor.BLUE to       BLUE_BALLOON,
        DyeColor.PURPLE to     PURPLE_BALLOON,
        DyeColor.MAGENTA to    MAGENTA_BALLOON,
        DyeColor.PINK to       PINK_BALLOON
    )

    @JvmField
    val DYE_BLASTER: Item = registerItem(
        "dye_blaster", ::DyeBlasterItem,
        properties = Item.Properties()
            .durability(435)
            .repairable(BRAINZ_ALLOY)
            .stacksTo(1)
    )
    @JvmField
    val FOOTBALL_HELMET: Item = registerItem(
        "football_helmet", ::FootballHelmetItem,
        properties = Item.Properties()
            .humanoidArmor(ArmorMaterials.CHAINMAIL, ArmorType.HELMET)
            .component(PazComponents.BLOCKS_PROJECTILE_DAMAGE, BlocksProjectileDamage(
                breakChance = 0.025f)
            )
            .component(
                DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
                    .setEquipSound(SoundEvents.ARMOR_EQUIP_IRON)
                    .build()
            )
            .component(
                DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                    .add(
                        Attributes.ARMOR,
                        AttributeModifier(pazResource("football_armor"), 4.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD
                    ).add(
                        Attributes.KNOCKBACK_RESISTANCE,
                        AttributeModifier(pazResource("football_knockback_resistance"), 0.1, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD
                    ).build()
            )
    )
    @JvmField
    val BLUEPRINT: Item = registerItem(
        "blueprint", ::BlueprintItem,
        properties = Item.Properties()
            .stacksTo(1)
            .durability(20)
            .component(PazComponents.BRAINZ_ALLOY_COST, BrainzAlloyCost())
            .component(DataComponents.BREAK_SOUND, SoundEvents.WOLF_ARMOR_BREAK)
    )
    @JvmField
    val LETTER: Item = registerItem(
        "letter",
        properties = Item.Properties()
    )
    @JvmField
    val SEED_PACKET: Item = registerItem(
        "seed_packet", ::SeedPacketItem,
        properties = Item.Properties()
            .component(PazComponents.SUN_COST, SunCost())
            .component(DataComponents.USE_COOLDOWN, UseCooldown(0f))
    )
    @JvmField
    val PLANT_POT_MINECART: Item = registerItem(
        "plant_pot_minecart", { p: Item.Properties -> MinecartItem(PazEntities.PLANT_POT_MINECART ,p) },
        properties = Item.Properties().stacksTo(1)
    )
    @JvmField
    val PLANT_POT_HELMET: Item = registerItem(
        "plant_pot_helmet", ::PlantPotHelmetItem,
        properties = Item.Properties()
            .durability(185)
            .rarity(Rarity.UNCOMMON)
            .repairable(Items.BRICK)
            .component(
                DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
                    .setEquipSound(SoundEvents.ARMOR_EQUIP_GENERIC)
                    .build())
    )
    @JvmField
    val MUSIC_DISC_GRASSY_GROOVE: Item = registerItem(
        "music_disc_grassy_groove",
        properties = Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).jukeboxPlayable(PazJukeboxSongs.GRASSY_GROOVE)
    )

    @JvmField val BROWN_COAT_SPAWN_EGG: Item = registerSpawnEgg(BROWN_COAT)
    @JvmField val NEWSPAPER_ZOMBIE_SPAWN_EGG: Item = registerSpawnEgg(NEWSPAPER_ZOMBIE)
    @JvmField val DIGGER_ZOMBIE_SPAWN_EGG: Item = registerSpawnEgg(DIGGER_ZOMBIE)
    @JvmField val ENGINEER_ZOMBIE_SPAWN_EGG: Item = registerSpawnEgg(ENGINEER_ZOMBIE)
    @JvmField val ZOMBIE_YETI_SPAWN_EGG: Item = registerSpawnEgg(ZOMBIE_YETI)
    @JvmField val DISCO_ZOMBIE_SPAWN_EGG: Item = registerSpawnEgg(DISCO_ZOMBIE)
    @JvmField val BACKUP_DANCER_SPAWN_EGG: Item = registerSpawnEgg(BACKUP_DANCER)
    @JvmField val ALL_STAR_SPAWN_EGG: Item = registerSpawnEgg(ALL_STAR)
    @JvmField val SOLDIER_ZOMBIE_SPAWN_EGG: Item = registerSpawnEgg(SOLDIER_ZOMBIE)
    @JvmField val ROBO_ZOMBIE_SPAWN_EGG: Item = registerSpawnEgg(ROBO_ZOMBIE)
    @JvmField val PIRATE_CAPTAIN_SPAWN_EGG: Item = registerSpawnEgg(PIRATE_CAPTAIN)
    @JvmField val SUPER_BRAINZ_SPAWN_EGG: Item = registerSpawnEgg(SUPER_BRAINZ)
    @JvmField val IMP_SPAWN_EGG: Item = registerSpawnEgg(IMP)
    @JvmField val GARGANTUAR_SPAWN_EGG: Item = registerSpawnEgg(GARGANTUAR)

    @JvmField val GNOME_SPAWN_EGG: Item = registerSpawnEgg(GNOME)

    private fun registerItem(
        name: String,
        itemFactory: Function<Item.Properties, Item> = { p: Item.Properties -> Item(p) },
        properties: Item.Properties = Item.Properties()
    ) : Item {

        val key = ResourceKey.create(Registries.ITEM, pazResource(name) )
        val item = itemFactory.apply(properties.setId(key))
        Registry.register(BuiltInRegistries.ITEM, key, item)

        return item
    }

    private fun registerSpawnEgg(type: EntityType<*>): Item {
        val entityId = EntityType.getKey(type).path
        return registerItem(
           "${entityId}_spawn_egg", ::SpawnEggItem,
            Item.Properties().spawnEgg(type)
        )
    }

    fun initialize() {

        FuelValueEvents.BUILD.register { builder, context ->
            builder.add(SUN, (context.baseSmeltTime()*0.5).toInt())
            builder.add(NEWSPAPER, context.baseSmeltTime())
        }

        ComposterBlock.COMPOSTABLES.put(SEED_PACKET.asItem(), 1.0f)

        // Modify components
        ItemComponentTooltipProviderRegistryImpl.addLast(PazComponents.STORED_WATER)
        ItemComponentTooltipProviderRegistryImpl.addLast(PazComponents.STORED_SUN)
        ItemComponentTooltipProviderRegistryImpl.addLast(PazComponents.SUN_COST)
        ItemComponentTooltipProviderRegistryImpl.addLast(PazComponents.BRAINZ_ALLOY_COST)
        ItemComponentTooltipProviderRegistryImpl.addLast(PazComponents.BLOCKS_PROJECTILE_DAMAGE)

        DefaultItemComponentEvents.MODIFY.register {
            it.modify(Items.BUCKET) { builder ->
                builder.set(PazComponents.BLOCKS_PROJECTILE_DAMAGE, BlocksProjectileDamage(breakChance = .05f))
                builder.set(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
                    .setEquipSound(SoundEvents.ARMOR_EQUIP_IRON)
                    .build()
                )

                val armorModifier = ItemAttributeModifiers.builder()
                    .add(
                        Attributes.ARMOR,
                        AttributeModifier(pazResource("bucket_armor"), 1.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD
                    ).add(
                        Attributes.KNOCKBACK_RESISTANCE,
                        AttributeModifier(pazResource("bucket_knockback_resistance"), 0.05, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD
                    ).build()

                builder.set(DataComponents.ATTRIBUTE_MODIFIERS, armorModifier)
            }
        }

        // Dispenser behavior
        DispenserBlock.registerBehavior(
            SEED_PACKET, object : DefaultDispenseItemBehavior() {
            public override fun execute(source: BlockSource, dispensed: ItemStack): ItemStack {
                return super.execute(source, dispensed)
            }
        })

        DispenserBlock.registerProjectileBehavior(SUN_BOTTLE)

        DispenserBlock.registerBehavior(
            PLANT_POT_MINECART, object : MinecartDispenseItemBehavior(PazEntities.PLANT_POT_MINECART) {}
        )
    }
}