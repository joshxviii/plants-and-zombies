package joshxviii.plantz.item

import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazItems
import joshxviii.plantz.entity.zombie.ZombieRobot
import joshxviii.plantz.getItemCount
import joshxviii.plantz.name
import joshxviii.plantz.removeItemFromInventory
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.TypedEntityData
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.AABB

class BlueprintItem(
    properties: Properties,
    val color: DyeColor = DyeColor.WHITE
) : Item(properties) {

    override fun getName(itemStack: ItemStack): Component {
        val component = itemStack.get(DataComponents.ENTITY_DATA) ?: return super.getName(itemStack)
        val entityId = BuiltInRegistries.ENTITY_TYPE.getKey(component.type())

        val entityName = Component.translatable("entity.${entityId.namespace}.${entityId.path}")
        return Component.translatable("item.plantz.blueprint.entity", entityName)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level: Level = context.level as? ServerLevel ?: return InteractionResult.SUCCESS
        val pos = context.clickedPos
        val blockState = level.getBlockState(pos)
        val spawnPos = if (blockState.getCollisionShape(level, pos).isEmpty) pos else pos.relative(context.clickedFace)
        return tryBuild(level, context.player, context.itemInHand, spawnPos, context.clickedFace, context.horizontalDirection, context.hand)
    }

    fun tryBuild(
        level: Level,
        player: Player?,
        itemStack: ItemStack,
        pos: BlockPos,
        face: Direction,
        horizontalDir: Direction,
        hand: InteractionHand = InteractionHand.MAIN_HAND
    ): InteractionResult {
        if (level !is ServerLevel || player == null) return InteractionResult.PASS

        val component = itemStack.get(DataComponents.ENTITY_DATA)
        val entityType = component?.type()

        val spawnPos = if (level.getBlockState(pos).getCollisionShape(level, pos).isEmpty) pos
        else pos.relative(face)

        val availableAlloy = player.getItemCount(PazItems.BRAINZ_ALLOY)
        val alloyCost = itemStack.get(PazComponents.BRAINZ_ALLOY_COST)?.getAlloyCost(entityType) ?: 0
        if (alloyCost > availableAlloy && !player.hasInfiniteMaterials()) {
            player.sendOverlayMessage(
                Component.translatable("message.plantz.not_enough_item", PazItems.BRAINZ_ALLOY.name(), availableAlloy, alloyCost).withStyle(ChatFormatting.RED)
            )
            return InteractionResult.FAIL
        }

        val entity = entityType?.create(
            level,
            EntityType.createDefaultStackConfig(level, itemStack, player),
            spawnPos,
            EntitySpawnReason.SPAWN_ITEM_USE,
            true,
            face == Direction.UP
        )?: return InteractionResult.FAIL

        if (entity is LivingEntity) {
            val spawnBlockCollisionShape = level.getBlockState(spawnPos).getCollisionShape(level, spawnPos).let { if (it.isEmpty.not()) it.bounds() else null }
            val entityBox = entity.boundingBox.move(spawnPos.multiply(-1))
            if (!(spawnBlockCollisionShape==null || !entityBox.intersects(spawnBlockCollisionShape))) {
                player.sendOverlayMessage(
                    Component.translatable("message.plantz.cannot_place", entity.name.copy().withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.DARK_RED)
                )
                return InteractionResult.FAIL
            }
            val yaw = horizontalDir.opposite.toYRot()
            entity.yHeadRot = yaw
            entity.yBodyRot = yaw
            entity.yRot = yaw
        }

        entity.let {
            val existingPlants = level.getEntitiesOfClass(LivingEntity::class.java, AABB(it.blockPosition()))
            if (existingPlants.isNotEmpty()) {
                player.sendOverlayMessage(
                    Component.translatable("message.plantz.cannot_place", entity.name.copy().withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.DARK_RED)
                )
                return InteractionResult.FAIL
            }
        }

        if (!level.addFreshEntity(entity)) {
            entity.discard()
            return InteractionResult.FAIL
        }

        itemStack.hurtAndBreak(1, player, hand)
        if (!player.hasInfiniteMaterials()) {
            player.removeItemFromInventory(PazItems.BRAINZ_ALLOY, alloyCost)
        }
        entity.playSound(SoundEvents.COPPER_GRATE_PLACE)
        level.sendParticles(
            ParticleTypes.CAMPFIRE_COSY_SMOKE, entity.x, entity.y, entity.z,
            6, 0.2, 0.2, 0.2, 0.01
        )
        if (entity is ZombieRobot) entity.owner = player
        level.gameEvent(player, GameEvent.ENTITY_PLACE, spawnPos)

        return InteractionResult.SUCCESS
    }


    companion object {
        fun stackFor(type: EntityType<*>): ItemStack {
            val stack = ItemStack(PazItems.BLUEPRINT)
            stack.set(DataComponents.ENTITY_DATA, TypedEntityData.of(type, CompoundTag()))

            return stack
        }

        fun typeFromStack(itemStack: ItemStack): EntityType<*>? {
            val type = itemStack.get(DataComponents.ENTITY_DATA)?.type()?: return null
            return type
        }
    }

}