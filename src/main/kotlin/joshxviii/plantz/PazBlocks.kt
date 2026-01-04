package joshxviii.plantz

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FlowerPotBlock
import net.minecraft.world.level.block.state.BlockBehaviour

object PazBlocks {
    @JvmField
    val POTTED_PLANT_ENTITY: Block = registerBlock("potted_plant_entity", Blocks.flowerPotProperties(), { FlowerPotBlock(Blocks.DIRT, it) })

    private fun registerBlock(
        name: String, 
        properties: BlockBehaviour.Properties, 
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

    fun initialize() {}
}