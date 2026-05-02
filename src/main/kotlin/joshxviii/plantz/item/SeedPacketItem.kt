package joshxviii.plantz.item

import com.google.common.base.Predicate
import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazItems
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.getTotalSun
import joshxviii.plantz.item.component.SeedPacket
import joshxviii.plantz.removeSunFromStorageAndInventory
import net.minecraft.ChatFormatting
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.AABB
import java.util.*

class SeedPacketItem(properties: Properties) : Item(properties) {

    override fun getName(itemStack: ItemStack): Component {
        val component = itemStack.get(PazComponents.SEED_PACKET) ?: return super.getName(itemStack)
        val entityId = component.entityId

        val entityName = Component.translatable("entity.${entityId.namespace}.${entityId.path}")
        return Component.translatable("item.plantz.seed_packet.entity", entityName)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val player = context.player
        val itemStack = context.itemInHand

        if (level is ServerLevel) {
            val entityId = itemStack.get(PazComponents.SEED_PACKET)?.entityId ?: return InteractionResult.FAIL
            val id = BuiltInRegistries.ENTITY_TYPE.get(entityId)

            val type = if (!id.isEmpty) id.get().value() else return InteractionResult.FAIL

            val pos = context.clickedPos
            val clickedFace = context.clickedFace
            val blockState = level.getBlockState(pos)

            val spawnPos = if (blockState.getCollisionShape(level, pos).isEmpty) pos
            else pos.relative(clickedFace)

            val entity = type.create(
                level,
                EntityType.createDefaultStackConfig(level, itemStack, player),
                spawnPos,
                EntitySpawnReason.SPAWN_ITEM_USE,
                true,
                clickedFace == Direction.UP
            )

            // check that player has enough sun to plant
            val availableSun = player?.getTotalSun() ?: 0
            val sunCost = itemStack.get(PazComponents.SEED_PACKET)?.getSunCost() ?: 0
            if (sunCost > availableSun && player?.hasInfiniteMaterials() == false) {
                player.sendOverlayMessage(
                    Component.translatable("message.plantz.not_enough_sun", availableSun, sunCost)
                        .withStyle(ChatFormatting.RED)
                )
                return InteractionResult.FAIL
            }

            // check if space is valid (no intersecting block bounding box and plant can survive)
            if (entity is Plant) {
                val spawnBlockCollisionShape = level.getBlockState(spawnPos).getCollisionShape(level, spawnPos).let { if (it.isEmpty.not()) it.bounds() else null }
                val entityBox = entity.boundingBox.move(spawnPos.multiply(-1))
                if (
                    !entity.canSurviveOn(level.getBlockState(spawnPos.below()))
                    || !(spawnBlockCollisionShape==null || !entityBox.intersects(spawnBlockCollisionShape))
                ) {
                    player?.sendOverlayMessage(
                        Component.translatable("message.plantz.cannot_survive")
                            .withStyle(ChatFormatting.RED)
                    )
                    return InteractionResult.FAIL
                }
                val yaw = context.horizontalDirection.opposite.toYRot()
                entity.yHeadRot = yaw
                entity.yBodyRot = yaw
                entity.yRot = yaw
            }

            // Prevent spawn if there's already a Plant in that block
            val aabb = entity?.let {
                val existingPlants = level.getEntitiesOfClass(Plant::class.java, AABB(it.blockPosition()))
                if (existingPlants.isNotEmpty()) {
                    player?.sendOverlayMessage(
                        Component.translatable("message.plantz.already_planted")
                            .withStyle(ChatFormatting.RED)
                    )
                    return InteractionResult.FAIL
                }
            }

            if (entity != null && !level.addFreshEntity(entity)) {
                entity.discard()
                return InteractionResult.FAIL
            }

            if (entity != null) {
                itemStack.consume(1, player)
                if (player?.hasInfiniteMaterials() == false) {
                    player.removeSunFromStorageAndInventory(sunCost)
                    //player.cooldowns?.addCooldown(itemStack, 100)
                }
                entity.playSound(SoundEvents.BIG_DRIPLEAF_PLACE)
                if (entity is TamableAnimal && player != null) entity.tame(player)
                level.gameEvent(player, GameEvent.ENTITY_PLACE, spawnPos)
            }
        }

        return InteractionResult.SUCCESS_SERVER
    }

    companion object {
        fun stackFor(type: EntityType<*>): ItemStack {
            val stack = ItemStack(PazItems.SEED_PACKET)
            val id = BuiltInRegistries.ENTITY_TYPE.getKey(type)

            stack.set(PazComponents.SEED_PACKET, SeedPacket(id))

            return stack
        }

        fun typeFromStack(itemStack: ItemStack): EntityType<*>? {
            val entityId = itemStack.get(PazComponents.SEED_PACKET)?.entityId?: return null
            val id = BuiltInRegistries.ENTITY_TYPE.get(entityId)
            val type = if (!id.isEmpty) id.get().value() else return null
            return type
        }
    }
}
