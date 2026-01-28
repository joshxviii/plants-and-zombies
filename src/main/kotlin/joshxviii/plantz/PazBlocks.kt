package joshxviii.plantz

import joshxviii.plantz.block.BrainzFlagBlock
import joshxviii.plantz.block.ConeBlock
import joshxviii.plantz.block.MailboxBlock
import joshxviii.plantz.block.PlantPotBlock
import joshxviii.plantz.block.entity.MailboxBlockEntity
import joshxviii.plantz.item.component.BlocksHeadDamage
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityType
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.mixin.blockview.BlockEntityMixin
import net.fabricmc.fabric.mixin.lookup.BlockEntityTypeAccessor
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
import net.minecraft.world.item.Item
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.equipment.Equippable
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
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

    @JvmField
    val MAILBOX: Block = registerBlock(
        "mailbox",
        BlockBehaviour.Properties.of()
            .sound(SoundType.WOOD)
            .strength(0.5F)
            .noOcclusion()
            .pushReaction(PushReaction.BLOCK),
        ::MailboxBlock
    )
    val MAILBOX_ENTITY: BlockEntityType<MailboxBlockEntity> = registerBlockEntity(
        "mailbox",
        ::MailboxBlockEntity,
        MAILBOX // TODO add colored blocks
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
            .component(PazComponents.BLOCKS_HEAD_DAMAGE, BlocksHeadDamage(breakChance = 0.2f))
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
        vararg validBlocks: Block
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

    fun initialize() {
    }
}