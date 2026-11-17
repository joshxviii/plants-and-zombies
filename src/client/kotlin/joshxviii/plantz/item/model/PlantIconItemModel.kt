package joshxviii.plantz.item.model

import com.mojang.serialization.MapCodec
import net.minecraft.client.Minecraft
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
    private val overlays: Map<EntityType<*>, ItemModel>,
    private val variants: Map<Identifier, ItemModel> = emptyMap()
) : ItemModel {
    companion object {

        fun plantEntityTypes(): Iterable<EntityType<*>> {
            val reg = BuiltInRegistries.ENTITY_TYPE
            val resourceManager = Minecraft.getInstance().resourceManager
            return reg.mapNotNull { type ->
                val id = overlayIdFor(type)
                val modelLocation = Identifier.fromNamespaceAndPath(id.namespace, "models/${id.path}.json")
                if (resourceManager.getResource(modelLocation).isPresent) type else null
            }
        }

        /** Entity data keys whose string value selects an icon variant, e.g. `plant_icon/marigold/red`. Add more keys to extend. */
        val VARIANT_KEYS = listOf("color")

        /** Finds every variant model under `models/item/plant_icon/<plant>/`. */
        fun variantIdsFor(type: EntityType<*>): List<Identifier> {
            val base = overlayIdFor(type)
            val prefix = "models/${base.path}"
            return Minecraft.getInstance().resourceManager
                .listResources(prefix) { it.path.endsWith(".json") }.keys
                .filter { it.namespace == base.namespace && it.path.startsWith("$prefix/") }
                .map { Identifier.fromNamespaceAndPath(it.namespace, it.path.removePrefix("models/").removeSuffix(".json")) }
        }

        fun variantIdFor(type: EntityType<*>, data: net.minecraft.nbt.CompoundTag): Identifier {
            val base = overlayIdFor(type)
            val name = VARIANT_KEYS.firstNotNullOfOrNull { data.getString(it).orElse(null) }
            return Identifier.fromNamespaceAndPath(base.namespace, "${base.path}/${name ?: type.let { BuiltInRegistries.ENTITY_TYPE.getKey(it).path }}")
        }

        fun overlayIdFor(type: EntityType<*>): Identifier {
            val id = BuiltInRegistries.ENTITY_TYPE.getKey(type)
            return Identifier.fromNamespaceAndPath(id.namespace, "item/plant_icon/${id.path}")
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

        val model = if (type != null) variants[variantIdFor(type, data.copyTagWithoutId())] ?: variants[variantIdFor(type, net.minecraft.nbt.CompoundTag())] ?: overlays[type] else null
        model?.let {
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
                entityIcons[type] = ItemModelUtils.plainModel(overlayIdFor(type)).bake(context, transformation)
            }

            val variantIcons = mutableMapOf<Identifier, ItemModel>()
            for (type in plantEntityTypes()) for (id in variantIdsFor(type)) {
                variantIcons[id] = ItemModelUtils.plainModel(id).bake(context, transformation)
            }

            return PlantIconItemModel(entityIcons, variantIcons)
        }

        override fun resolveDependencies(resolver: ResolvableModel.Resolver) {
            for (type in plantEntityTypes()) {
                resolver.markDependency(overlayIdFor(type))
                variantIdsFor(type).forEach { resolver.markDependency(it) }
            }
        }
    }
}
