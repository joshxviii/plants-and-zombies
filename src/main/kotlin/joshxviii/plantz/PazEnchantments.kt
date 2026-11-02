package joshxviii.plantz

import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.util.Unit

object PazEnchantments {
    @JvmField
    val LOB_PLANT: DataComponentType<Unit> = Registry.register(
        BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE,
        pazResource("lob_plant"),
        DataComponentType.builder<Unit>().persistent(Unit.CODEC).build()
    )

    fun initialize() {}
}