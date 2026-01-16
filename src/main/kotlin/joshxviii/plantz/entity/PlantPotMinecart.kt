package joshxviii.plantz.entity

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazItems
import joshxviii.plantz.block.PlantPotBlock
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.item.SeedPacketItem
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent

class PlantPotMinecart(type: EntityType<out AbstractMinecart>, level: Level) : AbstractMinecart(type, level) {

    override fun interact(player: Player, hand: InteractionHand): InteractionResult {
        val itemStack = player.getItemInHand(hand)
        val serverLevel = this.level()

        if (itemStack.`is`(PazItems.SEED_PACKET)) {
            val plantType = SeedPacketItem.typeFromStack(itemStack)

            val entity = if (serverLevel is ServerLevel) plantType?.create(serverLevel, null, BlockPos.containing(this.position()), EntitySpawnReason.SPAWN_ITEM_USE, true, false) else null

            // snap rotation
            if (entity is Plant) {
                entity.startRiding(this, true, true)
                entity.snapTo(this.position())
                val yaw = this.yRot
                entity.yHeadRot = yaw
                entity.yBodyRot = yaw
                entity.yRot = yaw
            }

            if (entity != null && !serverLevel.addFreshEntity(entity)) {
                entity.discard()
                return InteractionResult.FAIL
            }

            itemStack.consume(1, player)
            entity?.playSound(SoundEvents.BIG_DRIPLEAF_PLACE, 1.0f, 1.0f)
            if (entity is TamableAnimal) entity.tame(player)
            serverLevel.gameEvent(player, GameEvent.ENTITY_PLACE, this.position())
            return InteractionResult.FAIL
        }
        return InteractionResult.PASS
    }

    override fun isRideable(): Boolean = false
    override fun getPickResult(): ItemStack = ItemStack(PazItems.PLANT_POT_MINECART)
    override fun getDropItem(): Item = PazItems.PLANT_POT_MINECART

    override fun getDefaultDisplayBlockState(): BlockState {
        return PazBlocks.PLANT_POT.defaultBlockState().setValue<Direction, Direction>(PlantPotBlock.FACING, Direction.NORTH)
    }
}