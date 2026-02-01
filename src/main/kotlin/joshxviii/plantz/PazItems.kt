package joshxviii.plantz

import joshxviii.plantz.PazEntities.BACKUP_DANCER
import joshxviii.plantz.PazEntities.BROWN_COAT
import joshxviii.plantz.PazEntities.DISCO_ZOMBIE
import joshxviii.plantz.PazEntities.GARGANTUAR
import joshxviii.plantz.PazEntities.GNOME
import joshxviii.plantz.PazEntities.MINER
import joshxviii.plantz.PazEntities.NEWSPAPER_ZOMBIE
import joshxviii.plantz.PazEntities.ZOMBIE_YETI
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.item.NewspaperItem
import joshxviii.plantz.item.SeedPacketItem
import joshxviii.plantz.item.SunItem
import joshxviii.plantz.item.component.BlocksProjectileDamage
import joshxviii.plantz.item.component.SeedPacket
import joshxviii.plantz.item.component.SunCost
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents
import net.fabricmc.fabric.api.registry.FuelValueEvents
import net.fabricmc.fabric.impl.item.ItemComponentTooltipProviderRegistryImpl
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponents
import net.minecraft.core.dispenser.BlockSource
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.*
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.component.UseCooldown
import net.minecraft.world.item.equipment.Equippable
import net.minecraft.world.level.block.DispenserBlock
import net.minecraft.world.level.gameevent.GameEvent
import java.util.function.Function

object PazItems {
    @JvmField
    val SUN: Item = registerItem(
        "sun", ::SunItem,
        properties = Item.Properties().stacksTo(99))
    @JvmField
    val NEWSPAPER: Item = registerItem(
        "newspaper", ::NewspaperItem,
        properties = Item.Properties()
            .component(PazComponents.BLOCKS_PROJECTILE_DAMAGE, BlocksProjectileDamage(
                slot = EquipmentSlotGroup.HAND,
                breakChance = 0.15f)
            ).component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)
)
    @JvmField
    val SEED_PACKET: Item = registerItem(
        "seed_packet", ::SeedPacketItem,
        properties = Item.Properties()
            .component(PazComponents.SEED_PACKET, SeedPacket())
            .component(PazComponents.SUN_COST, SunCost())
            .component(DataComponents.USE_COOLDOWN, UseCooldown(100.0f))
    )
    @JvmField
    val PLANT_POT_MINECART: Item = registerItem(
        "plant_pot_minecart", { p: Item.Properties -> MinecartItem(PazEntities.PLANT_POT_MINECART ,p) },
        properties = Item.Properties().stacksTo(1)
    )

    @JvmField val BROWN_COAT_SPAWN_EGG: Item = registerSpawnEgg(BROWN_COAT)
    @JvmField val NEWSPAPER_ZOMBIE_SPAWN_EGG: Item = registerSpawnEgg(NEWSPAPER_ZOMBIE)
    @JvmField val MINER_SPAWN_EGG: Item = registerSpawnEgg(MINER)
    @JvmField val ZOMBIE_YETI_SPAWN_EGG: Item = registerSpawnEgg(ZOMBIE_YETI)
    @JvmField val DISCO_ZOMBIE_SPAWN_EGG: Item = registerSpawnEgg(DISCO_ZOMBIE)
    @JvmField val BACKUP_DANCER_SPAWN_EGG: Item = registerSpawnEgg(BACKUP_DANCER)
    @JvmField val GARGANTUAR_SPAWN_EGG: Item = registerSpawnEgg(GARGANTUAR)

    @JvmField val GNOME_SPAWN_EGG: Item = registerSpawnEgg(GNOME)

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
        val entityId = EntityType.getKey(type).path
        return registerItem(
           "${entityId}_spawn_egg", ::SpawnEggItem,
            Item.Properties().spawnEgg(type)
        )
    }

    fun initialize() {

        FuelValueEvents.BUILD.register { builder, context ->
            builder.add(SUN, context.baseSmeltTime())
            builder.add(NEWSPAPER, context.baseSmeltTime())
        }

        // Modify components
        ItemComponentTooltipProviderRegistryImpl.addLast(PazComponents.SEED_PACKET)
        ItemComponentTooltipProviderRegistryImpl.addLast(PazComponents.SUN_COST)
        ItemComponentTooltipProviderRegistryImpl.addLast(PazComponents.BLOCKS_PROJECTILE_DAMAGE)

        DefaultItemComponentEvents.MODIFY.register {
            it.modify(Items.BUCKET) { builder ->
                builder.set(PazComponents.BLOCKS_PROJECTILE_DAMAGE, BlocksProjectileDamage(breakChance = .05f))
                builder.set(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD)
                    .setEquipSound(SoundEvents.ARMOR_EQUIP_IRON)
                    .build()
                )

                val armorModifier = ItemAttributeModifiers.builder()
                    .add(
                        Attributes.ARMOR,
                        AttributeModifier(pazResource("bucket_armor"), 1.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD
                    ).add(
                        Attributes.KNOCKBACK_RESISTANCE,
                        AttributeModifier(pazResource("bucket_knockback_resistance"), 0.1, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD
                    ).build()

                builder.set(DataComponents.ATTRIBUTE_MODIFIERS, armorModifier)
            }
        }

        // Dispenser behavior
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