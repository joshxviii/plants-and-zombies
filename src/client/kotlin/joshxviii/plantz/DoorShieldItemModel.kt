package joshxviii.plantz

import com.mojang.serialization.MapCodec
import joshxviii.plantz.PazComponents
import net.minecraft.client.Minecraft
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.ItemModelResolver
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.resources.model.ResolvableModel
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.item.Items
import org.joml.Matrix4fc

class DoorShieldItemModel(
    private val doorModels: Map<Block, ItemModel>
) : ItemModel {
    companion object {

        fun doorBlocks(): Iterable<Pair<Block, Identifier>> {
            val reg = BuiltInRegistries.BLOCK
            val resourceManager = Minecraft.getInstance().resourceManager
            return reg.filterIsInstance<DoorBlock>().mapNotNull { block ->
                val item = block.asItem()
                if (item == Items.AIR) return@mapNotNull null
                val itemKey = BuiltInRegistries.ITEM.getKey(item)
                val id = Identifier.fromNamespaceAndPath(itemKey.namespace, "item/${itemKey.path}")
                val modelLocation = Identifier.fromNamespaceAndPath(id.namespace, "models/${id.path}.json")
                if (resourceManager.getResource(modelLocation).isPresent) block to id else null
            }
        }
    }

    override fun update(
        output: ItemStackRenderState,
        item: ItemStack,
        resolver: ItemModelResolver,
        displayContext: ItemDisplayContext,
        level: ClientLevel?,
        owner: ItemOwner?,
        seed: Int
    ) {
        val door = item.get(PazComponents.DOOR_SHIELD)?.doorType ?: Blocks.OAK_DOOR
        (doorModels[door] ?: doorModels[Blocks.OAK_DOOR])?.let {
            output.appendModelIdentityElement(it)
            it.update(output, item, resolver, displayContext, level, owner, seed)
        }
    }

    class Unbaked : ItemModel.Unbaked {
        companion object {
            val MAP_CODEC: MapCodec<Unbaked> = MapCodec.unit(Unbaked())
        }

        override fun type(): MapCodec<Unbaked> = MAP_CODEC

        override fun bake(context: ItemModel.BakingContext, transformation: Matrix4fc): ItemModel {
            val models = mutableMapOf<Block, ItemModel>()
            for ((block, id) in doorBlocks()) {
                models[block] = ItemModelUtils.plainModel(id).bake(context, transformation)
            }

            return DoorShieldItemModel(models)
        }

        override fun resolveDependencies(resolver: ResolvableModel.Resolver) {
            for ((_, id) in doorBlocks()) resolver.markDependency(id)
        }
    }
}
