package joshxviii.plantz

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.fabric.api.client.renderer.v1.render.FabricLayerRenderState
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.block.BlockAndTintGetter
import net.minecraft.client.renderer.block.dispatch.BlockStateModel
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.ItemModelResolver
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.renderer.item.ModelRenderProperties
import net.minecraft.client.resources.model.ResolvableModel
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import org.joml.Matrix4f
import org.joml.Matrix4fc

class DoorShieldItemModel(
    private val displayProperties: ModelRenderProperties
) : ItemModel {
    override fun update(
        output: ItemStackRenderState,
        item: ItemStack,
        resolver: ItemModelResolver,
        displayContext: ItemDisplayContext,
        level: ClientLevel?,
        owner: ItemOwner?,
        seed: Int
    ) {
        val door = item.get(PazComponents.DOOR_SHIELD)?.doorType?.takeIf { it is DoorBlock } ?: return
        output.appendModelIdentityElement(door)
        val layer = output.newLayer()
        layer.setLocalTransform(Matrix4f().translate(0f, 0f, -8/16f))
        displayProperties.applyToLayer(layer, displayContext)
        if (item.hasFoil()) {
            val foilType = ItemStackRenderState.FoilType.STANDARD
            layer.setFoilType(foilType)
            output.setAnimated()
            output.appendModelIdentityElement(foilType)
        }
        val tintGetter = level ?: BlockAndTintGetter.EMPTY
        val lowerState = door.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)
        val upperState = lowerState.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER)
        val blockStateModels = Minecraft.getInstance().modelManager.blockStateModelSet
        layer.setParticleMaterial(blockStateModels.get(lowerState).particleMaterial())
        emitBlockModel(layer, lowerState, tintGetter, 0f, seed.toLong())
        emitBlockModel(layer, upperState, tintGetter, 1f, seed.toLong() + 1L)
    }

    private fun emitBlockModel(
        layer: ItemStackRenderState.LayerRenderState,
        state: BlockState,
        level: BlockAndTintGetter,
        yOffset: Float,
        seed: Long
    ) {
        val model: BlockStateModel = Minecraft.getInstance().modelManager.blockStateModelSet.get(state)
        val emitter = (layer as FabricLayerRenderState).emitter()
        if (yOffset != 0f) {
            emitter.pushTransform { quad ->
                for (vertex in 0..3) quad.pos(vertex, quad.x(vertex), quad.y(vertex) + yOffset, quad.z(vertex))
                true
            }
        }
        try {
            model.emitQuads(
                emitter,
                level,
                BlockPos.ZERO,
                state,
                RandomSource.create(seed),
                { false }
            )
        } finally {
            if (yOffset != 0f) emitter.popTransform()
        }
    }

    class Unbaked : ItemModel.Unbaked {
        private val model: Identifier

        private constructor(model: Identifier) {
            this.model = model
        }

        companion object {
            val MAP_CODEC: MapCodec<Unbaked> = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Identifier.CODEC.fieldOf("model").forGetter(Unbaked::model)
                ).apply(instance, ::Unbaked)
            }
        }

        override fun type(): MapCodec<Unbaked> = MAP_CODEC

        override fun bake(context: ItemModel.BakingContext, transformation: Matrix4fc): ItemModel {
            val resolvedModel = context.blockModelBaker.getModel(model)
            val properties = ModelRenderProperties.fromResolvedModel(
                context.blockModelBaker,
                resolvedModel,
                resolvedModel.topTextureSlots
            )
            return DoorShieldItemModel(properties)
        }

        override fun resolveDependencies(resolver: ResolvableModel.Resolver) {
            resolver.markDependency(model)
        }
    }
}
