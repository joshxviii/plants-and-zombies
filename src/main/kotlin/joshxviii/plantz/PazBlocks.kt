package joshxviii.plantz

import joshxviii.plantz.block.PlantPotBlock
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.PushReaction

object PazBlocks {
    @JvmField
    val PLANT_POT: Block = registerBlock(
        "plant_pot",
        BlockBehaviour.Properties.of()
            .instabreak()
            .noOcclusion()
            .pushReaction(PushReaction.DESTROY),
        ::PlantPotBlock
    )

    private fun registerBlock(
        name: String,
        properties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(),
        blockFactory: (BlockBehaviour.Properties) -> Block = ::Block
    ): Block {
        val key = ResourceKey.create(Registries.BLOCK, pazResource(name))
        val block = blockFactory(properties.setId(key))
        Registry.register(BuiltInRegistries.BLOCK, key, block)

        // Also register the block item
        val itemKey = ResourceKey.create(Registries.ITEM, pazResource(name))
        val blockItem = BlockItem(block, Item.Properties().setId(itemKey))
        Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem)

        return block
    }

    @JvmField val PLANTABLE = registerBlockTag("plantable")

    private fun registerBlockTag(name: String) = TagKey.create(Registries.BLOCK, pazResource(name))

    fun initialize() {}
}