package joshxviii.plantz

import joshxviii.plantz.item.SeedPacketItem
import joshxviii.plantz.item.component.SeedPacket
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import java.util.function.Function

object PazItems {
    @JvmField
    val SUN: Item = registerItem("sun")
    @JvmField
    val SEED_PACKET: Item = registerItem(
        "seed_packet", { p: Item.Properties -> SeedPacketItem(p) },
        Item.Properties().component(PazComponents.SEED_PACKET, SeedPacket(null))
    )

    private fun registerItem(
        name: String,
        itemFactory: Function<Item.Properties, Item> = { p: Item.Properties -> Item(p) },
        properties: Item.Properties = Item.Properties()
    ) : Item {

        val key = ResourceKey.create(Registries.ITEM, pazResource(name) )
        val item = itemFactory.apply(properties.setId(key))
        Registry.register(BuiltInRegistries.ITEM, key, item)

        return item
    }

    fun initialize() {}
}