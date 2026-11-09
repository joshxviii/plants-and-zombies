package joshxviii.plantz.item

import joshxviii.plantz.PazItems
import joshxviii.plantz.PazTags
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.FluidTags
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.Vec3

class DuckTubeItem(properties: Properties) : Item(properties) {
    companion object {
        fun addToTooltip(consumer: MutableList<Component>) {
            consumer.add(1, Component.translatable("item.plantz.ducky_tube.description")
                .withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC))
        }

        const val DUCKY_TUBE_DAMAGE_INTERVAL = 45
        fun floatOnWater(entity: LivingEntity) {
            //Duck tube / water plants floating
            val item: ItemStack = entity.getItemBySlot(EquipmentSlot.LEGS)
            if (!item.`is`(PazItems.DUCKY_TUBE) && !entity.`is`(PazTags.EntityTypes.PLANTABLE_ON_WATER)) return
            if (entity is Player && entity.abilities.flying) return
            val fluidType: Fluid = entity.level()
                .getBlockState(BlockPos.containing(entity.position().add(0.0, entity.bbHeight * 0.5, 0.0)))
                .fluidState.type
            if (fluidType === Fluids.EMPTY) return

            var upwardForce = if (fluidType === Fluids.LAVA) 0.1 else 0.015

            // submerged
            if (entity.isEyeInFluid(FluidTags.WATER)) upwardForce = 0.135
            if (entity.isEyeInFluid(FluidTags.LAVA)) upwardForce = 0.15

            // sneaking
            if (entity.isShiftKeyDown) upwardForce *= if (fluidType === Fluids.LAVA) 0.0 else 0.2

            entity.addDeltaMovement(Vec3(0.0, upwardForce, 0.0))

            entity.fallDistance = 0.0

            if (!entity.level()
                    .isClientSide && entity.tickCount % DUCKY_TUBE_DAMAGE_INTERVAL == 0 && entity.getRandom()
                    .nextFloat() > 0.5f
            ) item.hurtAndBreak(1, entity, EquipmentSlot.LEGS)
        }
    }

    override fun inventoryTick(itemStack: ItemStack, level: ServerLevel, owner: Entity, slot: EquipmentSlot?) {
        super.inventoryTick(itemStack, level, owner, slot)
    }
}