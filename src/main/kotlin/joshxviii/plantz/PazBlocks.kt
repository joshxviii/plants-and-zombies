package joshxviii.plantz

import joshxviii.plantz.block.BrainzFlagBlock
import joshxviii.plantz.block.ConeBlock
import joshxviii.plantz.block.MailboxBlock
import joshxviii.plantz.block.PlantPotBlock
import joshxviii.plantz.block.entity.MailboxBlockEntity
import joshxviii.plantz.item.component.BlocksProjectileDamage
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.equipment.Equippable
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction

object PazBlocks {
    @JvmField
    val PLANT_POT: Block = registerBlock(
        "plant_pot",
        BlockBehaviour.Properties.of()
            .sound(SoundType.STONE)
            .strength(0.2F)
            .noOcclusion()
            .pushReaction(PushReaction.NORMAL),
        ::PlantPotBlock
    )

    @JvmField val MAILBOX: Block = registerBlock("mailbox", mailboxProperties(), ::MailboxBlock)
    @JvmField val LIGHT_GRAY_MAILBOX: Block = registerBlock("light_gray_mailbox", mailboxProperties(MapColor.COLOR_LIGHT_GRAY), {MailboxBlock(it, DyeColor.LIGHT_GRAY)})
    @JvmField val GRAY_MAILBOX: Block = registerBlock("gray_mailbox", mailboxProperties(MapColor.COLOR_GRAY), {MailboxBlock(it, DyeColor.GRAY)})
    @JvmField val BLACK_MAILBOX: Block = registerBlock("black_mailbox", mailboxProperties(MapColor.COLOR_BLACK), {MailboxBlock(it, DyeColor.BLACK)})
    @JvmField val BROWN_MAILBOX: Block = registerBlock("brown_mailbox", mailboxProperties(MapColor.COLOR_BROWN), {MailboxBlock(it, DyeColor.BROWN)})
    @JvmField val RED_MAILBOX: Block = registerBlock("red_mailbox", mailboxProperties(MapColor.COLOR_RED), {MailboxBlock(it, DyeColor.RED)})
    @JvmField val ORANGE_MAILBOX: Block = registerBlock("orange_mailbox", mailboxProperties(MapColor.COLOR_ORANGE), {MailboxBlock(it, DyeColor.ORANGE)})
    @JvmField val YELLOW_MAILBOX: Block = registerBlock("yellow_mailbox", mailboxProperties(MapColor.COLOR_YELLOW), {MailboxBlock(it, DyeColor.YELLOW)})
    @JvmField val LIME_MAILBOX: Block = registerBlock("lime_mailbox", mailboxProperties(MapColor.COLOR_LIGHT_GREEN), {MailboxBlock(it, DyeColor.LIME)})
    @JvmField val GREEN_MAILBOX: Block = registerBlock("green_mailbox", mailboxProperties(MapColor.COLOR_GREEN), {MailboxBlock(it, DyeColor.GREEN)})
    @JvmField val CYAN_MAILBOX: Block = registerBlock("cyan_mailbox", mailboxProperties(MapColor.COLOR_CYAN), {MailboxBlock(it, DyeColor.CYAN)})
    @JvmField val LIGHT_BLUE_MAILBOX: Block = registerBlock("light_blue_mailbox", mailboxProperties(MapColor.COLOR_LIGHT_BLUE), {MailboxBlock(it, DyeColor.LIGHT_BLUE)})
    @JvmField val BLUE_MAILBOX: Block = registerBlock("blue_mailbox", mailboxProperties(MapColor.COLOR_BLUE), {MailboxBlock(it, DyeColor.BLUE)})
    @JvmField val PURPLE_MAILBOX: Block = registerBlock("purple_mailbox", mailboxProperties(MapColor.COLOR_PURPLE), {MailboxBlock(it, DyeColor.PURPLE)})
    @JvmField val MAGENTA_MAILBOX: Block = registerBlock("magenta_mailbox", mailboxProperties(MapColor.COLOR_MAGENTA), {MailboxBlock(it, DyeColor.MAGENTA)})
    @JvmField val PINK_MAILBOX: Block = registerBlock("pink_mailbox", mailboxProperties(MapColor.COLOR_PINK), {MailboxBlock(it, DyeColor.PINK)})
    val mailboxByColor = mapOf(
        DyeColor.WHITE to      MAILBOX,
        DyeColor.LIGHT_GRAY to LIGHT_GRAY_MAILBOX,
        DyeColor.GRAY to       GRAY_MAILBOX,
        DyeColor.BLACK to      BLACK_MAILBOX,
        DyeColor.BROWN to      BROWN_MAILBOX,
        DyeColor.RED to        RED_MAILBOX,
        DyeColor.ORANGE to     ORANGE_MAILBOX,
        DyeColor.YELLOW to     YELLOW_MAILBOX,
        DyeColor.LIME to       LIME_MAILBOX,
        DyeColor.GREEN to      GREEN_MAILBOX,
        DyeColor.CYAN to       CYAN_MAILBOX,
        DyeColor.LIGHT_BLUE to LIGHT_BLUE_MAILBOX,
        DyeColor.BLUE to       BLUE_MAILBOX,
        DyeColor.PURPLE to     PURPLE_MAILBOX,
        DyeColor.MAGENTA to    MAGENTA_MAILBOX,
        DyeColor.PINK to       PINK_MAILBOX,
    )
    val MAILBOX_ENTITY: BlockEntityType<MailboxBlockEntity> = registerBlockEntity(
        "mailbox",
        ::MailboxBlockEntity,
        *mailboxByColor.values.toTypedArray()
    )

    @JvmField
    val CONE: Block = registerBlock(
        "cone",
        BlockBehaviour.Properties.of()
            .sound(SoundType.CANDLE)
            .instabreak()
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY),
        ::ConeBlock,
        Item.Properties()
            .component(PazComponents.BLOCKS_PROJECTILE_DAMAGE, BlocksProjectileDamage(breakChance = 0.2f))
            .component(
                DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
                    .setEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
                    .build()
            ).component(
                DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                    .add(
                        Attributes.ARMOR,
                        AttributeModifier(pazResource("cone_armor"), 0.5, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD
                    ).add(
                        Attributes.KNOCKBACK_RESISTANCE,
                        AttributeModifier(pazResource("cone_knockback_resistance"), 0.1, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD
                    ).build()
            )
    )

    @JvmField
    val BRAINZ_FLAG: Block = registerBlock(
        "brainz_flag",
        BlockBehaviour.Properties.of()
            .sound(SoundType.WOOD)
            .instabreak()
            .noCollision()
            .pushReaction(PushReaction.DESTROY),
        ::BrainzFlagBlock,
        Item.Properties()
            .rarity(Rarity.RARE)
            .equippableUnswappable(EquipmentSlot.OFFHAND)
            .component(
                DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                    .add(
                        Attributes.SPAWN_REINFORCEMENTS_CHANCE,
                        AttributeModifier(pazResource("zombie_leader_flag"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                        EquipmentSlotGroup.HAND
                    ).add(
                        Attributes.FOLLOW_RANGE,
                        AttributeModifier(pazResource("zombie_leader_flag"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                        EquipmentSlotGroup.HAND
                    ).build()
            )
    )

    private fun registerBlock(
        name: String,
        properties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(),
        blockFactory: (BlockBehaviour.Properties) -> Block = ::Block,
        itemProperties: Item.Properties = Item.Properties()
    ): Block {
        val key = ResourceKey.create(Registries.BLOCK, pazResource(name))
        val block = blockFactory(properties.setId(key))
        Registry.register(BuiltInRegistries.BLOCK, key, block)

        // Also register the block item
        val itemKey = ResourceKey.create(Registries.ITEM, pazResource(name))
        val blockItem = BlockItem(block, itemProperties.setId(itemKey))
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem)

        return block
    }


    private fun <T : BlockEntity> registerBlockEntity(
        name: String,
        factory: (BlockPos, BlockState) -> T,
        vararg validBlocks: Block,
    ): BlockEntityType<T> {
        val key = ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, pazResource(name))
        val builder = FabricBlockEntityTypeBuilder.create(
            factory,
            *validBlocks
        )
        val blockEntity = builder.build()
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, key, blockEntity)
        return blockEntity
    }

    private fun mailboxProperties(mapColor: MapColor = MapColor.SNOW): BlockBehaviour.Properties {
        return BlockBehaviour.Properties.of()
            .mapColor(mapColor)
            .sound(SoundType.LANTERN)
            .strength(1.3F)
            .noOcclusion()
            .pushReaction(PushReaction.BLOCK)
    }

    fun initialize() {}
}