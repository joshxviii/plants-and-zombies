package joshxviii.plantz.item.model

import com.mojang.serialization.MapCodec
import joshxviii.plantz.PazComponents
import joshxviii.plantz.item.model.BatteryFullness
import joshxviii.plantz.pazResource
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty
import net.minecraft.world.entity.ItemOwner
import net.minecraft.world.item.ItemStack

object BatteryFullness : RangeSelectItemModelProperty {
    @JvmField
    val MAP_CODEC: MapCodec<BatteryFullness> = MapCodec.unit(this)

    override fun get(itemStack: ItemStack, level: ClientLevel?, owner: ItemOwner?, seed: Int): Float {
        val storedSun = itemStack.get(PazComponents.STORED_SUN) ?: return 0f
        if (storedSun.max <= 0) return 0f
        return (1 - (storedSun.storedSun.toFloat() / storedSun.max)).coerceIn(0f, 1f)
    }

    override fun type(): MapCodec<BatteryFullness> = MAP_CODEC

    fun register() {
        RangeSelectItemModelProperties.ID_MAPPER.put(
            pazResource("battery_fullness"), MAP_CODEC
        )
    }
}
