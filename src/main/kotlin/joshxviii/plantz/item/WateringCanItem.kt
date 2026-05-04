package joshxviii.plantz.item

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazSounds
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.BlockTags
import net.minecraft.tags.FluidTags
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.HitResult

class WateringCanItem(properties: Properties) : BlockItem(PazBlocks.WATERING_CAN_BLOCK, properties) {

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val itemStack = player.getItemInHand(hand)
        val storedWaterComponent = itemStack.get(PazComponents.STORED_WATER)

        if (storedWaterComponent?.let { it.storedWater >= it.maxCapacity } == true) return InteractionResult.PASS
        val hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY)
        if (hitResult.type == HitResult.Type.MISS) return InteractionResult.PASS
        else {
            if (hitResult.type == HitResult.Type.BLOCK) {
                val pos: BlockPos = hitResult.blockPos
                if (!level.mayInteract(player, pos)) return InteractionResult.PASS

                if (level.getFluidState(pos).`is`(FluidTags.WATER)) {
                    itemStack.set(PazComponents.STORED_WATER, storedWaterComponent?.addWater())
                    level.gameEvent(player, GameEvent.FLUID_PICKUP, pos)
                    level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 0.9f)
                    return InteractionResult.SUCCESS
                }
            }

            return InteractionResult.PASS
        }

        return InteractionResult.PASS
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos: BlockPos = context.clickedPos
        val player = context.player
        val itemStack = context.itemInHand
        val blockState = level.getBlockState(pos)
        val storedWaterComponent = itemStack.get(PazComponents.STORED_WATER)

        //
        if (player?.isCrouching==true) return super.useOn(context)
        if (storedWaterComponent?.let { it.storedWater <= 0 } == true) return InteractionResult.PASS
        if (context.clickedFace != Direction.DOWN ) {
            var success: Boolean = false
            if (blockState.`is`(BlockTags.CONVERTABLE_TO_MUD)) {// mud conversion
                level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0f, 1.0f)
                level.gameEvent(null, GameEvent.FLUID_PLACE, pos)
                level.setBlockAndUpdate(pos, Blocks.MUD.defaultBlockState())
                success = true
            }
            if (blockState.hasProperty(BlockStateProperties.MOISTURE)) {// farmland
                level.setBlockAndUpdate(pos, blockState.setValue(BlockStateProperties.MOISTURE, 7))
                success = true
            }
            if (blockState.`is`(Blocks.CAULDRON)) {
                val newState: BlockState = Blocks.WATER_CAULDRON.defaultBlockState()
                level.setBlockAndUpdate(pos, newState)
                success = true
            }
            if (blockState.`is`(Blocks.WATER_CAULDRON) && blockState.hasProperty(BlockStateProperties.LEVEL_CAULDRON)) {
                val waterLevel = blockState.getValue(BlockStateProperties.LEVEL_CAULDRON)
                if (waterLevel<3) {
                    level.setBlockAndUpdate(pos, blockState.setValue(BlockStateProperties.LEVEL_CAULDRON, waterLevel+1))
                    success = true
                }
            }
            if (success) {
                itemStack.set(PazComponents.STORED_WATER, storedWaterComponent?.removeWater(2))
                if (!level.isClientSide) {
                    val serverLevel = level as ServerLevel
                    level.playSound(null, pos, PazSounds.WATERING_CAN, SoundSource.BLOCKS)
                    for (i in 0..4) {
                        serverLevel.sendParticles(
                            ParticleTypes.SPLASH,
                            pos.x + level.getRandom().nextDouble(),
                            (pos.y + 1).toDouble(),
                            pos.z + level.getRandom().nextDouble(), 1, 0.0, 0.0, 0.0, 1.0
                        )
                    }
                }
                return InteractionResult.SUCCESS
            }
        }
        return InteractionResult.PASS
    }

    override fun isBarVisible(stack: ItemStack): Boolean {
        stack.get(PazComponents.STORED_WATER)?.let {
            return it.hasWater()
        }
        return false
    }
    override fun getBarColor(stack: ItemStack): Int = 3847130
    override fun getBarWidth(stack: ItemStack): Int {
        stack.get(PazComponents.STORED_WATER)?.let {
            return Mth.ceil(it.storagePercentage() * 13)
        }
        return super.getBarWidth(stack)
    }

    override fun releaseUsing(
        itemStack: ItemStack,
        level: Level,
        entity: LivingEntity,
        remainingTime: Int
    ): Boolean {
        return super.releaseUsing(itemStack, level, entity, remainingTime)
    }

    override fun getUseDuration(itemStack: ItemStack, user: LivingEntity): Int {
        return super.getUseDuration(itemStack, user)
    }

    override fun onUseTick(level: Level, livingEntity: LivingEntity, itemStack: ItemStack, ticksRemaining: Int) {
        super.onUseTick(level, livingEntity, itemStack, ticksRemaining)
    }

    override fun getUseAnimation(itemStack: ItemStack): ItemUseAnimation {
        return super.getUseAnimation(itemStack)
    }
}