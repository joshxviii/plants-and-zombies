package joshxviii.plantz

import joshxviii.plantz.block.BrainzFlagBlock
import joshxviii.plantz.block.ConeBlock
import joshxviii.plantz.block.PlantPotBlock
import joshxviii.plantz.item.component.BlocksHeadDamage
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Util
import net.minecraft.util.datafix.fixes.References
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
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.PushReaction
import java.util.Set

object PazBlocks {
    @JvmField
    val PLANT_POT: Block = registerBlock(
        "plant_pot",
        BlockBehaviour.Properties.of()
            .sound(SoundType.STONE)
            .instabreak()
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY),
        ::PlantPotBlock
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
            .component(PazComponents.BLOCKS_HEAD_DAMAGE, BlocksHeadDamage(breakChance = .1f))
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

    fun initialize() {}
}