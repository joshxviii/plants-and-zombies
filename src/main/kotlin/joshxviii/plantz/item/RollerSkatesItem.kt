package joshxviii.plantz.item

import joshxviii.plantz.pazResource
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class RollerSkatesItem(properties: Properties) : Item(properties) {
    companion object {
        fun addToTooltip(consumer: MutableList<Component>) {
            consumer.add(1, Component.translatable("item.plantz.roller_skates.description")
                .withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC))
        }
        val ROLLER_SKATES_ID = pazResource("roller_skates")
        private const val DAMAGE_INTERVAL = 35

        fun isSkating(entity: LivingEntity): Boolean {
            if (entity is Player && !entity.isSprinting) return false
            return entity.getItemBySlot(EquipmentSlot.FEET).item is RollerSkatesItem
        }
    }

    fun removeModifiers(entity: LivingEntity?) {
        if (entity == null) return
        entity.getAttribute(Attributes.FRICTION_MODIFIER)?.removeModifier(ROLLER_SKATES_ID)
        entity.getAttribute(Attributes.MOVEMENT_SPEED)?.removeModifier(ROLLER_SKATES_ID)
        entity.getAttribute(Attributes.STEP_HEIGHT)?.removeModifier(ROLLER_SKATES_ID)
    }

    override fun inventoryTick(itemStack: ItemStack, level: ServerLevel, owner: Entity, slot: EquipmentSlot?) {
        super.inventoryTick(itemStack, level, owner, slot)
        if (level.isClientSide || owner !is LivingEntity) return
        if (isSkating(owner)) {
            owner.getAttribute(Attributes.FRICTION_MODIFIER)?.let {
                if (!it.hasModifier(ROLLER_SKATES_ID)) it.addTransientModifier(
                    AttributeModifier(ROLLER_SKATES_ID, -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                )
            }
            owner.getAttribute(Attributes.MOVEMENT_SPEED)?.let {
                if (!it.hasModifier(ROLLER_SKATES_ID)) it.addTransientModifier(
                    AttributeModifier(ROLLER_SKATES_ID, 0.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                )
            }
            owner.getAttribute(Attributes.STEP_HEIGHT)?.let {
                if (!it.hasModifier(ROLLER_SKATES_ID)) it.addTransientModifier(
                    AttributeModifier(ROLLER_SKATES_ID, 0.85, AttributeModifier.Operation.ADD_VALUE)
                )
            }
        }
        else  {
            removeModifiers(owner)
            return
        }

        if (!level.isClientSide && owner.tickCount % DAMAGE_INTERVAL == 0 && owner.getRandom().nextFloat() > 0.5f) {
            if (slot != null) itemStack.hurtAndBreak(1, owner, slot)
        }
    }
}