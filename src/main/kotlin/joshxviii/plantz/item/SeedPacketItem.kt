package joshxviii.plantz.item

import com.google.common.base.Predicate
import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazEntities.getSunCostFromType
import joshxviii.plantz.PazItems
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.item.component.SeedPacket
import joshxviii.plantz.item.component.SunCost
import net.minecraft.ChatFormatting
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.UseCooldown
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.AABB
import java.util.Optional

class SeedPacketItem(properties: Properties) : Item(properties) {

    override fun getName(itemStack: ItemStack): Component {
        val component = itemStack.get(PazComponents.SEED_PACKET) ?: return super.getName(itemStack)
        val entityId = component.entityId?: return super.getName(itemStack)

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

            if (entity is Plant) {// check if valid block to plant
                if (!entity.canSurviveOn(level.getBlockState(spawnPos.below()))) {
                    entity.discard()
                    return InteractionResult.FAIL
                }
                val yaw = context.horizontalDirection.opposite.toYRot()
                entity.yHeadRot = yaw
                entity.yBodyRot = yaw
                entity.yRot = yaw
                entity.state = PlantState.GROW
            }

            // check that player has enough sun to plant
            val availableSun = player?.inventory?.countItem(PazItems.SUN) ?: 0
            val sunCost = itemStack.get(PazComponents.SUN_COST)?.sunCost ?: 0
            if (sunCost > availableSun && player?.isCreative == false) {
                player.displayClientMessage(
                    Component.translatable("message.plantz.not_enough_sun", availableSun, sunCost)
                        .withStyle(ChatFormatting.RED), true
                )
                return InteractionResult.FAIL
            }

            // Prevent spawn if there's already a Plant in that block
            val aabb = AABB(spawnPos)
            val existingPlants = level.getEntitiesOfClass(Plant::class.java, aabb)
            if (existingPlants.isNotEmpty()) {
                player?.displayClientMessage(
                    Component.translatable("message.plantz.already_planted")
                        .withStyle(ChatFormatting.RED), true
                )
                return InteractionResult.FAIL
            }

            if (entity != null && !level.addFreshEntity(entity)) {
                entity.discard()
                return InteractionResult.FAIL
            }

            if (entity != null) {
                itemStack.consume(1, player)
                if (player?.isCreative == false) {
                    player.inventory.clearOrCountMatchingItems(Predicate<ItemStack> {
                        it.`is`(PazItems.SUN)
                    }, sunCost, player.inventoryMenu.getCraftSlots())
                    player.cooldowns?.addCooldown(itemStack, 100)
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
            stack.set(PazComponents.SUN_COST, SunCost(getSunCostFromType(type)))
            stack.set(DataComponents.USE_COOLDOWN, UseCooldown(100.0f, Optional.ofNullable(id)))

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
