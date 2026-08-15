package joshxviii.plantz.item

import joshxviii.plantz.PazItems
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.TypedEntityData

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