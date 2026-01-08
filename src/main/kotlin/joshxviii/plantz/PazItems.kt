package joshxviii.plantz

import joshxviii.plantz.entity.Plant
import joshxviii.plantz.item.SeedPacketItem
import joshxviii.plantz.item.component.SeedPacket
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.core.dispenser.BlockSource
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.DispenserBlock
import net.minecraft.world.level.gameevent.GameEvent
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

    fun initialize() {// Dispenser behavior
        DispenserBlock.registerBehavior(
            SEED_PACKET, object : DefaultDispenseItemBehavior() {
            public override fun execute(source: BlockSource, dispensed: ItemStack): ItemStack {
                val direction: Direction = source.state().getValue<Direction>(DispenserBlock.FACING)
                val serverLevel = source.level()
                val plantType = SeedPacketItem.typeFromStack(dispensed)

                if (plantType != null){

                    val entity = plantType.create(
                        serverLevel,
                        null,
                        source.pos().relative(direction),
                        EntitySpawnReason.DISPENSER,
                        direction != Direction.UP,
                        false
                    )
                    // snap rotation
                    if (entity is Plant && entity.snapSpawnRotation()) {
                        val yaw = direction.toYRot()
                        entity.yHeadRot = yaw
                        entity.yBodyRot = yaw
                        entity.yRot = yaw
                    }

                    if (entity != null && !serverLevel.addFreshEntity(entity)) {
                        entity.discard()
                        return dispensed
                    }

                    source.level().gameEvent(null, GameEvent.ENTITY_PLACE, source.pos())
                    dispensed.shrink(1)
                }

                return dispensed
            }
        })
    }
}