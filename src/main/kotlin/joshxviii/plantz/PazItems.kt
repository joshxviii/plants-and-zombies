package joshxviii.plantz

import joshxviii.plantz.PazEntities.BROWN_COAT
import joshxviii.plantz.PazEntities.ZOMBIE_YETI
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.item.SeedPacketItem
import joshxviii.plantz.item.component.SeedPacket
import joshxviii.plantz.item.component.SunCost
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.core.dispenser.BlockSource
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.MinecartItem
import net.minecraft.world.item.SpawnEggItem
import net.minecraft.world.level.block.DispenserBlock
import net.minecraft.world.level.gameevent.GameEvent
import java.util.function.Function

object PazItems {
    @JvmField
    val SUN: Item = registerItem(
        "sun",
        properties = Item.Properties().stacksTo(99))
    @JvmField
    val SEED_PACKET: Item = registerItem(
        "seed_packet", ::SeedPacketItem,
        properties = Item.Properties()
            .component(PazComponents.SEED_PACKET, SeedPacket())
            .component(PazComponents.SUN_COST, SunCost())
    )
    @JvmField
    val PLANT_POT_MINECART: Item = registerItem(
        "plant_pot_minecart", { p: Item.Properties -> MinecartItem(PazEntities.PLANT_POT_MINECART ,p) },
        properties = Item.Properties().stacksTo(1)
    )

    @JvmField val BROWN_COAT_SPAWN_EGG: Item = registerSpawnEgg(BROWN_COAT)
    @JvmField val ZOMBIE_YETI_SPAWN_EGG: Item = registerSpawnEgg(ZOMBIE_YETI)

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

    private fun registerSpawnEgg(type: EntityType<*>): Item {
        return registerItem(
            EntityType.getKey(type).path + "_spawn_egg",
            { properties: Item.Properties -> SpawnEggItem(properties) },
            Item.Properties().spawnEgg(type)
        )
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

                    if (entity is Plant) {// snap rotation
                        val yaw = direction.toYRot()
                        entity.yHeadRot = yaw
                        entity.yBodyRot = yaw
                        entity.yRot = yaw
                    }

                    if (entity != null && !serverLevel.addFreshEntity(entity)) {
                        entity.discard()
                        return dispensed
                    }

                    entity?.playSound(SoundEvents.BIG_DRIPLEAF_PLACE)
                    source.level().gameEvent(null, GameEvent.ENTITY_PLACE, source.pos())
                    dispensed.shrink(1)
                }

                return dispensed
            }
        })
    }
}