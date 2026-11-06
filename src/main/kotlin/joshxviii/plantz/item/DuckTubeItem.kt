package joshxviii.plantz.item

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class DuckTubeItem(properties: Properties) : Item(properties) {
    companion object {
        fun addToTooltip(consumer: MutableList<Component>) {
            consumer.add(1, Component.translatable("item.plantz.ducky_tube.description")
                .withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC))
        }
    }
}