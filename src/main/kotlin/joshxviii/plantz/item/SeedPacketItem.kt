package joshxviii.plantz.item

import joshxviii.plantz.*
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.FluidTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.TypedEntityData
import net.minecraft.world.item.component.UseCooldown
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import java.util.*

class SeedPacketItem(properties: Properties) : Item(properties) {

    override fun components(): DataComponentMap {
        return super.components().apply {}
    }

    override fun getDefaultInstance(): ItemStack {
        val default = super.getDefaultInstance()
        default.set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.PIG, CompoundTag()))
        return default
    }

    override fun asItem(): Item {
        return super.asItem()
    }

    override fun getName(itemStack: ItemStack): Component {
        val component = itemStack.get(DataComponents.ENTITY_DATA) ?: return super.getName(itemStack)
        val entityId = BuiltInRegistries.ENTITY_TYPE.getKey(component.type())

        val entityName = Component.translatable("entity.${entityId.namespace}.${entityId.path}")
        return Component.translatable("item.plantz.seed_packet.entity", entityName)
    }

    override fun interactLivingEntity(
        itemStack: ItemStack,
        player: Player,
        target: LivingEntity,
        type: InteractionHand
    ): InteractionResult {
        return InteractionResult.SUCCESS
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val itemStack = player.getItemInHand(hand)

        val component = itemStack.get(DataComponents.ENTITY_DATA)
        val entityType = component?.type()?: return InteractionResult.PASS
        val waterPlaceable = BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(entityType).`is`(PazTags.EntityTypes.PLANTABLE_ON_WATER)

        if (waterPlaceable) {// check water result first
            val waterHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY)
            if (waterHitResult.type == HitResult.Type.MISS) return InteractionResult.PASS
            else {
                if (waterHitResult.type == HitResult.Type.BLOCK) {
                    val pos: BlockPos = waterHitResult.blockPos

                    if (!level.mayInteract(player, pos)) return InteractionResult.PASS

                    if (level.getFluidState(pos).`is`(FluidTags.WATER)) {
                        UseOnContext(player, hand, waterHitResult).let {
                            return tryPlant(level, player, entityType, itemStack, pos.above(), Direction.UP, it.horizontalDirection, checkWater = true)
                        }
                    }
                }
            }
        }
        // check block result
        val blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE)
        if (blockHitResult.type == HitResult.Type.MISS) return InteractionResult.PASS
        else {
            if (blockHitResult.type == HitResult.Type.BLOCK) {
                val pos: BlockPos = blockHitResult.blockPos
                if (!level.mayInteract(player, pos)) return InteractionResult.PASS
                UseOnContext(player, hand, blockHitResult).let {
                    return tryPlant(level, player, entityType, itemStack, pos, it.clickedFace, it.horizontalDirection)
                }
            }
        }
        return InteractionResult.PASS
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        return InteractionResult.PASS
    }

    fun tryPlant(
        level: Level,
        player: Player?,
        entityType: EntityType<*>,
        itemStack: ItemStack,
        pos: BlockPos,
        face: Direction,
        horizontalDir: Direction,
        checkWater: Boolean = false
    ): InteractionResult {
        if (level !is ServerLevel || player == null) return InteractionResult.PASS

        val spawnPos = if (level.getBlockState(pos).getCollisionShape(level, pos).isEmpty) pos
        else pos.relative(face)

        val availableSun = player.getTotalSun()
        val sunCost = itemStack.get(PazComponents.SUN_COST)?.getSunCost(entityType) ?: 0
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
            !checkWater,
            face == Direction.UP
        )?: return InteractionResult.FAIL

        if (entity is Plant) {
            val spawnBlockCollisionShape = level.getBlockState(spawnPos).getCollisionShape(level, spawnPos).let { if (it.isEmpty.not()) it.bounds() else null }
            val entityBox = entity.boundingBox.move(spawnPos.multiply(-1))
            if (
                !(entity.canSurviveOn(level.getBlockState(spawnPos.below())) || checkWater)
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
            // set cooldown
            val group = BuiltInRegistries.ENTITY_TYPE.getKey(entityType)
            if (PazConfig.PLANT_COOLDOWN_ENABLED) {
                val cooldownTime = PazConfig.getCooldownTime(sunCost)
                itemStack.set(DataComponents.USE_COOLDOWN, UseCooldown(cooldownTime, Optional.of(group)))
                player.cooldowns.addCooldown(group, (cooldownTime*20).toInt())
            } else {
                player.cooldowns.removeCooldown(group)
                itemStack.set(DataComponents.USE_COOLDOWN, UseCooldown(0f))
            }
        }
        entity.playSound(SoundEvents.BIG_DRIPLEAF_PLACE)
        if (entity is TamableAnimal) entity.tame(player)
        level.gameEvent(player, GameEvent.ENTITY_PLACE, spawnPos)

        return InteractionResult.SUCCESS_SERVER
    }


    companion object {
        fun stackFor(type: EntityType<*>): ItemStack {
            val stack = ItemStack(PazItems.SEED_PACKET)
            stack.set(DataComponents.ENTITY_DATA, TypedEntityData.of(type, CompoundTag()))

            return stack
        }

        fun typeFromStack(itemStack: ItemStack): EntityType<*>? {
            val type = itemStack.get(DataComponents.ENTITY_DATA)?.type()?: return null
            return type
        }
    }
}
