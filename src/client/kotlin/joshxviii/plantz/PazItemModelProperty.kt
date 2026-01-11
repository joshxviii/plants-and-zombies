package joshxviii.plantz

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import joshxviii.plantz.item.SeedPacketItem
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.select.ContextEntityType
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

class PazItemModelProperty : SelectItemModelProperty<ResourceKey<EntityType<*>>> {
    override fun get(
        itemStack: ItemStack, level: ClientLevel?, owner: LivingEntity?, seed: Int, displayContext: ItemDisplayContext
    ): ResourceKey<EntityType<*>>? {
        val entityId = itemStack.get(PazComponents.SEED_PACKET)?.entityId?: return null
        return BuiltInRegistries.ENTITY_TYPE.get(entityId).get().unwrapKey().get()
    }

    override fun type(): SelectItemModelProperty.Type<ContextEntityType, ResourceKey<EntityType<*>>> {
        return TYPE
    }

    override fun valueCodec(): Codec<ResourceKey<EntityType<*>>> {
        return VALUE_CODEC
    }

    companion object {
        val VALUE_CODEC: Codec<ResourceKey<EntityType<*>>> = ResourceKey.codec<EntityType<*>>(Registries.ENTITY_TYPE)
        val TYPE: SelectItemModelProperty.Type<ContextEntityType, ResourceKey<EntityType<*>>> =
            SelectItemModelProperty.Type.create<ContextEntityType, ResourceKey<EntityType<*>>>(
                MapCodec.unit<ContextEntityType>(ContextEntityType())!!, VALUE_CODEC
            )
    }
}