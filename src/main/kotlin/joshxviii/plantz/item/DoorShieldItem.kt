package joshxviii.plantz.item

import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazItems
import joshxviii.plantz.item.component.DoorShieldComponent
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.tags.BlockItemTagsProvider
import net.minecraft.data.tags.TagsProvider
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.BlockTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.item.component.TypedEntityData
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block

class DoorShieldItem(properties: Properties) : Item(properties) {

    override fun getName(itemStack: ItemStack): Component {
        val doorData = itemStack.get(PazComponents.DOOR_SHIELD) ?: return super.getName(itemStack)
        return Component.translatable("item.plantz.door_shield.type", doorData.doorType.name)
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        player.startUsingItem(hand)
        return InteractionResult.CONSUME
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
        return 72000
    }

    override fun onUseTick(level: Level, livingEntity: LivingEntity, itemStack: ItemStack, ticksRemaining: Int) {
        super.onUseTick(level, livingEntity, itemStack, ticksRemaining)
    }

    override fun getUseAnimation(itemStack: ItemStack): ItemUseAnimation {
        return ItemUseAnimation.TOOT_HORN
    }

    companion object {

        fun stackFor(doorType: Block): ItemStack {
            val stack = ItemStack(PazItems.DOOR_SHIELD)
            stack.set(PazComponents.DOOR_SHIELD, DoorShieldComponent(doorType))
            return stack
        }
    }
}