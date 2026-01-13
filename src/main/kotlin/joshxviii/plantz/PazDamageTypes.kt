package joshxviii.plantz

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType

object PazDamageTypes {
    // seed packets
    @JvmField
    val PLANT = registerDamageType("plant")
    @JvmField
    val FREEZE = registerDamageType("freeze_plant")
    @JvmField
    val FIRE = registerDamageType("fire_plant")
    @JvmField
    val CHOMP = registerDamageType("chomp_plant")
    @JvmField
    val FUME = registerDamageType("fume_plant")

    private fun registerDamageType(
        name: String
    ) : ResourceKey<DamageType> {
        return ResourceKey.create(Registries.DAMAGE_TYPE, pazResource(name) )
    }

    fun initialize() {}
}