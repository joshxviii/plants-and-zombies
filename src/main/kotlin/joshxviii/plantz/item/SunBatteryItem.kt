package joshxviii.plantz.item

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

class SunBatteryItem(properties: Properties) : Item(properties) {

    override fun isBarVisible(stack: ItemStack): Boolean {
        stack.get(PazComponents.STORED_SUN)?.let {
            return it.hasSun()
        }
        return false
    }
    override fun getBarColor(stack: ItemStack): Int {
        stack.get(PazComponents.STORED_SUN)?.let {
            return if (it.isFull()) 14369328 else 16768870
        }
        return super.getBarColor(stack)
    }
    override fun getBarWidth(stack: ItemStack): Int {
        stack.get(PazComponents.STORED_SUN)?.let {
            return Mth.ceil(it.storagePercentage() * 13)
        }
        return super.getBarWidth(stack)
    }

}