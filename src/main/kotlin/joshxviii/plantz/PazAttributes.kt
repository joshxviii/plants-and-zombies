package joshxviii.plantz

import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.RangedAttribute

object PazAttributes {

    val SUN_COST: Holder<Attribute> =
        register("sun_cost", RangedAttribute("attribute.name.sun_cost", 0.0, 0.0, 100.0).setSyncable(true))

    private fun register(name: String, attribute: Attribute): Holder<Attribute> {
        return Registry.registerForHolder(
            BuiltInRegistries.ATTRIBUTE,
            pazResource(name),
            attribute
        )
    }

    fun initialize() {}
}