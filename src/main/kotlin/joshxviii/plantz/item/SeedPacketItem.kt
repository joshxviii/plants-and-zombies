package joshxviii.plantz.item

import joshxviii.plantz.*
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.item.component.SunCost
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.FluidTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import net.minecraft.world.item.component.TypedEntityData
import net.minecraft.world.item.component.UseCooldown
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import java.util.*
import java.util.function.Consumer
import kotlin.jvm.optionals.getOrNull

class SeedPacketItem(properties: Properties) : Item(properties) {


    override fun getName(itemStack: ItemStack): Component {
        val component = itemStack.get(DataComponents.ENTITY_DATA) ?: return super.getName(itemStack)
        val entityId = BuiltInRegistries.ENTITY_TYPE.getKey(component.type())

        val entityName = Component.translatable("entity.${entityId.namespace}.${entityId.path}")
        return Component.translatable("item.plantz.seed_packet.entity", entityName)
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY)
        if (hitResult.type == HitResult.Type.MISS) return InteractionResult.PASS
        else {
            if (hitResult.type == HitResult.Type.BLOCK) {
                val pos: BlockPos = hitResult.blockPos

                if (!level.mayInteract(player, pos)) return InteractionResult.PASS

                if (level.getFluidState(pos).`is`(FluidTags.WATER)) {
                    UseOnContext(player, hand, hitResult).let {}
                }
            }

            return InteractionResult.PASS
        }
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        return tryPlant(
            context.level,
            context.player,
            context.itemInHand,
            context.clickedPos,
            context.clickedFace,
            context.horizontalDirection
        )
    }

    fun tryPlant(
        level: Level,
        player: Player?,
        itemStack: ItemStack,
        pos: BlockPos,
        face: Direction,
        horizontalDir: Direction
    ): InteractionResult {
        if (level !is ServerLevel || player == null) return InteractionResult.PASS

        val component = itemStack.get(DataComponents.ENTITY_DATA) ?: return InteractionResult.FAIL
        val entityType = component.type()

        val spawnPos = if (level.getBlockState(pos).getCollisionShape(level, pos).isEmpty) pos
        else pos.relative(face)

        val availableSun = player.getTotalSun()
        val sunCost = itemStack.get(PazComponents.SUN_COST)?.sunCost ?: 0
        if (sunCost > availableSun && !player.hasInfiniteMaterials()) {
            player.sendOverlayMessage(
                Component.translatable("message.plantz.not_enough_sun", availableSun, sunCost).withStyle(ChatFormatting.RED)
            )
            return InteractionResult.FAIL
        }

        val entity = entityType.create(
            level,
            EntityType.createDefaultStackConfig(level, itemStack, player),
            spawnPos,
            EntitySpawnReason.SPAWN_ITEM_USE,
            true,
            face == Direction.UP
        )?: return InteractionResult.FAIL

        if (entity is Plant) {
            val spawnBlockCollisionShape = level.getBlockState(spawnPos).getCollisionShape(level, spawnPos).let { if (it.isEmpty.not()) it.bounds() else null }
            val entityBox = entity.boundingBox.move(spawnPos.multiply(-1))
            if (
                !entity.canSurviveOn(level.getBlockState(spawnPos.below()))
                || !(spawnBlockCollisionShape==null || !entityBox.intersects(spawnBlockCollisionShape))
            ) {
                player.sendOverlayMessage(
                    Component.translatable("message.plantz.cannot_survive").withStyle(ChatFormatting.RED)
                )
                return InteractionResult.FAIL
            }
            val yaw = horizontalDir.opposite.toYRot()
            entity.yHeadRot = yaw
            entity.yBodyRot = yaw
            entity.yRot = yaw
        }

        entity.let {
            val existingPlants = level.getEntitiesOfClass(Plant::class.java, AABB(it.blockPosition()))
            if (existingPlants.isNotEmpty()) {
                player.sendOverlayMessage(
                    Component.translatable("message.plantz.already_planted").withStyle(ChatFormatting.RED)
                )
                return InteractionResult.FAIL
            }
        }

        if (!level.addFreshEntity(entity)) {
            entity.discard()
            return InteractionResult.FAIL
        }

        itemStack.consume(1, player)
        if (!player.hasInfiniteMaterials()) {
            player.removeSunFromStorageAndInventory(sunCost)
            if (PazConfig.PLANT_COOLDOWN_ENABLED) player.cooldowns.addCooldown(itemStack, PazConfig.getCooldownTime(sunCost))
        }
        entity.playSound(SoundEvents.BIG_DRIPLEAF_PLACE)
        if (entity is TamableAnimal) entity.tame(player)
        level.gameEvent(player, GameEvent.ENTITY_PLACE, spawnPos)

        return InteractionResult.SUCCESS_SERVER
    }


    companion object {
        fun stackFor(type: EntityType<*>): ItemStack {
            val stack = ItemStack(PazItems.SEED_PACKET)
            val sunCost = PazConfig.getSunCost(type)
            stack.set(DataComponents.ENTITY_DATA, TypedEntityData.of(type, CompoundTag()))
            stack.set(PazComponents.SUN_COST, SunCost(sunCost))
            stack.set(DataComponents.USE_COOLDOWN, UseCooldown(1f, Optional.of(BuiltInRegistries.ENTITY_TYPE.getKey(type))))

            return stack
        }

        fun typeFromStack(itemStack: ItemStack): EntityType<*>? {
            val type = itemStack.get(DataComponents.ENTITY_DATA)?.type()?: return null
            return type
        }
    }
}
