package joshxviii.plantz

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType

object PazDamageTypes {
    // seed packets
    @JvmField
    val PLANT = registerDamageType("plant")
    @JvmField
    val PLANT_AOE = registerDamageType("plant_explode")
    @JvmField
    val FREEZE = registerDamageType("plant_freeze")
    @JvmField
    val FIRE = registerDamageType("plant_fire")
    @JvmField
    val CHOMP = registerDamageType("plant_chomp")
    @JvmField
    val FUME = registerDamageType("plant_fume")
    @JvmField
    val ZOMBIE_EAT = registerDamageType("zombie_eat")
    @JvmField
    val ZOMBIE_SMASH = registerDamageType("zombie_smash")
    @JvmField
    val ZOMBIE_TRAMPLE = registerDamageType("zombie_trample")

    private fun registerDamageType(
        name: String
    ) : ResourceKey<DamageType> {
        return ResourceKey.create(Registries.DAMAGE_TYPE, pazResource(name) )
    }

    fun initialize() {}
}