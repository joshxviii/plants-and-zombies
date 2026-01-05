package joshxviii.plantz.item

import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazItems
import joshxviii.plantz.entity.Plant
import joshxviii.plantz.item.component.SeedPacket
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.phys.AABB
import java.util.function.Consumer

class SeedPacketItem(properties: Properties) : Item(properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val player = context.player
        val itemStack = context.itemInHand

        if (level.isClientSide) return InteractionResult.SUCCESS

        val component = itemStack.get(PazComponents.SEED_PACKET) ?: return InteractionResult.PASS
        val entityId: Identifier = component.entityId ?: return InteractionResult.PASS

        val type = BuiltInRegistries.ENTITY_TYPE.get(entityId).get().value() as EntityType<*>

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

        val entity = type.spawn(level as ServerLevel, spawnPos, EntitySpawnReason.SPAWN_ITEM_USE)
        if (entity == null) return InteractionResult.FAIL

        itemStack.consume(1, player)

        entity.playSound(SoundEvents.BIG_DRIPLEAF_PLACE, 1.0f, 1.0f)
        if (entity is TamableAnimal && player != null) entity.tame(player)
        if (!level.addFreshEntity(entity)) {
            return InteractionResult.PASS
        }

        return InteractionResult.CONSUME
    }

    override fun getName(itemStack: ItemStack): Component {
        val component = itemStack.get(PazComponents.SEED_PACKET) ?: return super.getName(itemStack)
        val entityId = component.entityId ?: return super.getName(itemStack)

        val entityName = Component.translatable("entity.${entityId.namespace}.${entityId.path}")
        return Component.translatable("item.plantz.seed_packet.entity", entityName)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        display: TooltipDisplay,
        builder: Consumer<Component>,
        flag: TooltipFlag
    ) {
        super.appendHoverText(stack, context, display, builder, flag)
        stack.addToTooltip(PazComponents.SEED_PACKET, context, display, builder, flag)
    }

    companion object {
        fun stackFor(type: EntityType<*>): ItemStack {
            val stack = ItemStack(PazItems.SEED_PACKET)
            val id = BuiltInRegistries.ENTITY_TYPE.getKey(type)
            stack.set(PazComponents.SEED_PACKET, SeedPacket(id))
            return stack
        }
    }
}
