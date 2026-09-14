package joshxviii.plantz

import com.mojang.serialization.MapCodec
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.ItemModelResolver
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.resources.model.ResolvableModel
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Matrix4fc

class PlantIconItemModel(
    private val overlays: Map<EntityType<*>, ItemModel>
) : ItemModel {
    companion object {

        fun plantEntityTypes(): Iterable<EntityType<*>> {
            val reg = BuiltInRegistries.ENTITY_TYPE
            return reg.mapNotNull { type ->
                return@mapNotNull type
                // for plantz namespace only:
//                val id = BuiltInRegistries.ENTITY_TYPE.getKey(type) ?: return@mapNotNull null
//                if (id.namespace == PazMain.MODID) type else null
            }
        }

        fun overlayIdFor(type: EntityType<*>): Identifier {
            val id = BuiltInRegistries.ENTITY_TYPE.getKey(type)
            return pazResource("item/plant_icon/${id.path}")
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
        val data = item.get(DataComponents.ENTITY_DATA)
        val type = data?.type()

        overlays[type]?.let {
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
            val entityIcons = mutableMapOf<EntityType<*>, ItemModel>()
            for (type in plantEntityTypes()) {
                val id = overlayIdFor(type)
                entityIcons[type] = ItemModelUtils.plainModel(id).bake(context, transformation)
            }

            return PlantIconItemModel(entityIcons)
        }

        override fun resolveDependencies(resolver: ResolvableModel.Resolver) {
            for (type in plantEntityTypes()) {
                overlayIdFor(type).let { resolver.markDependency(it) }
            }
        }
    }
}
