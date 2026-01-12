package joshxviii.plantz.item

import com.mojang.authlib.minecraft.client.MinecraftClient
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazEntities.getSunCostFromType
import joshxviii.plantz.PazItems
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.entity.Plant
import joshxviii.plantz.item.component.SeedPacket
import joshxviii.plantz.item.component.SunCost
import joshxviii.plantz.pazResource
import net.minecraft.ChatFormatting
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomModelData
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.AABB
import java.util.function.Consumer

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

        val component = itemStack.get(PazComponents.SEED_PACKET) ?: return InteractionResult.PASS
        val entityId = component.entityId?: return InteractionResult.FAIL
        val id = BuiltInRegistries.ENTITY_TYPE.get(entityId)

        val type = if (!id.isEmpty) id.get().value() else return InteractionResult.FAIL

        val pos = context.clickedPos
        val clickedFace = context.clickedFace
        val blockState = level.getBlockState(pos)

        val spawnPos = if (blockState.getCollisionShape(level, pos).isEmpty) pos
        else pos.relative(clickedFace)

        // Prevent spawn if there's already a Plant in that block
        val aabb = AABB(spawnPos)
        val existingPlants = level.getEntitiesOfClass(Plant::class.java, aabb)
        if (existingPlants.isNotEmpty()) {
            player?.displayClientMessage(
                Component.translatable("message.plantz.already_planted").withStyle(ChatFormatting.RED),
                true
            )
            return InteractionResult.FAIL
        }

        val entity = if (level is ServerLevel) type.create(level, null, spawnPos, EntitySpawnReason.SPAWN_ITEM_USE, true, clickedFace == Direction.UP) else null

        if (entity is Plant) {// check if valid block to plant
            if (!entity.canSurviveOn(spawnPos.below())) {
                entity.discard()
                return InteractionResult.FAIL
            }
            val yaw = context.horizontalDirection.opposite.toYRot()
            entity.yHeadRot = yaw
            entity.yBodyRot = yaw
            entity.yRot = yaw
            entity.state = PlantState.GROW
        }

        if (entity != null && !level.addFreshEntity(entity)) {
            entity.discard()
            return InteractionResult.FAIL
        }

        itemStack.consume(1, player)
        entity?.playSound(SoundEvents.BIG_DRIPLEAF_PLACE)
        if (entity is TamableAnimal && player != null) entity.tame(player)
        level.gameEvent(player, GameEvent.ENTITY_PLACE, spawnPos)

        return InteractionResult.SUCCESS_SERVER
    }

    companion object {
        fun stackFor(type: EntityType<*>): ItemStack {
            val stack = ItemStack(PazItems.SEED_PACKET)
            val id = BuiltInRegistries.ENTITY_TYPE.getKey(type)

            stack.set(PazComponents.SEED_PACKET, SeedPacket(id))
            stack.set(PazComponents.SUN_COST, SunCost(getSunCostFromType(type)))

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
