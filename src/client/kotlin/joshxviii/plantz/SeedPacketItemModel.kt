package joshxviii.plantz

import com.mojang.serialization.MapCodec
import net.minecraft.client.Minecraft
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.ItemModel
import net.minecraft.client.renderer.item.ItemModelResolver
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.ResolvableModel
import net.minecraft.client.resources.model.cuboid.ItemTransform
import net.minecraft.client.resources.model.sprite.Material
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.AtlasIds
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.joml.Matrix4f
import org.joml.Matrix4fc
import org.joml.Vector3f

class SeedPacketItemModel(
    private val base: ItemModel,
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
            return pazResource("item/seed_packet/${id.path}")
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
        output.appendModelIdentityElement(base)

        base.update(output, item, resolver, displayContext, level, owner, seed)
        output.appendModelIdentityElement(base)

        overlays[type]?.let {
            it.update(output, item, resolver, displayContext, level, owner, seed)
        }
    }

    class Unbaked : ItemModel.Unbaked {
        companion object {
            val MAP_CODEC: MapCodec<Unbaked> = MapCodec.unit(Unbaked())
            val BASE_MODEL: Identifier = pazResource("item/seed_packet/seed_packet")
        }

        override fun type(): MapCodec<Unbaked> = MAP_CODEC

        override fun bake(context: ItemModel.BakingContext, transformation: Matrix4fc): ItemModel {
            val base = ItemModelUtils.plainModel(BASE_MODEL).bake(context, transformation)

            val entityIcons = mutableMapOf<EntityType<*>, ItemModel>()
            for (type in plantEntityTypes()) {
                val id = overlayIdFor(type)
                entityIcons[type] = ItemModelUtils.plainModel(id).bake(context, transformation)
            }

            return SeedPacketItemModel(base, entityIcons)
        }

        override fun resolveDependencies(resolver: ResolvableModel.Resolver) {
            resolver.markDependency(BASE_MODEL)
            for (type in plantEntityTypes()) {
                overlayIdFor(type).let { resolver.markDependency(it) }
            }
        }
    }

}
