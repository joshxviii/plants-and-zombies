package joshxviii.plantz.item

import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import net.minecraft.world.item.context.UseOnContext
import java.util.function.Consumer

class GardeningGloveItem(properties: Properties) : Item(properties) {

    companion object {

        fun addToTooltip(
            context: TooltipContext,
            consumer: MutableList<Component>,
            flag: TooltipFlag,
            components: DataComponentGetter
        ) {
            val data = components.get(DataComponents.ENTITY_DATA) ?: return
            val id = BuiltInRegistries.ENTITY_TYPE.getKey(data.type())
            val entityName = Component.translatable("entity.${id.namespace}.${id.path}")
                .withStyle(ChatFormatting.DARK_GREEN)

            consumer.add(1, Component.translatable("item.plantz.gardening_glove.entity", entityName)
                .withStyle(ChatFormatting.GRAY))
        }

        fun hurtAndDropPlant(item: ItemStack, owner: LivingEntity, hand: InteractionHand) {
            if (item.nextDamageWillBreak()) {
                item.get(DataComponents.ENTITY_DATA)?.let { data ->
                    val breakItem = SeedPacketItem.stackFor(data.type())
                    ItemEntity(owner.level(), owner.x, owner.eyeY, owner.z, breakItem).let {
                        owner.level().addFreshEntity(it)
                    }
                }
            }
            item.hurtAndBreak(1, owner, hand)
        }
    }

    override fun getUseAnimation(itemStack: ItemStack): ItemUseAnimation {
        return ItemUseAnimation.BRUSH
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val stack = context.itemInHand
        if (!stack.has(DataComponents.ENTITY_DATA)) return InteractionResult.PASS

        val level = context.level
        if (level !is ServerLevel) return InteractionResult.SUCCESS

        val result = SeedPacketItem.tryPlant(
            level = level,
            player = context.player,
            itemStack = stack,
            pos = context.clickedPos,
            face = context.clickedFace,
            horizontalDir = context.horizontalDirection,
            ignoreSunRequirement = true,
            consumeItem = false
        )

        if (result == InteractionResult.SUCCESS) {
            stack.remove(DataComponents.ENTITY_DATA)
            context.player?.let {
                GardeningGloveItem.hurtAndDropPlant(stack, it, context.hand)
            }
        }
        return result
    }

}
