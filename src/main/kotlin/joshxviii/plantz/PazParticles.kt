package joshxviii.plantz

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.core.Registry
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries

object PazParticles {
    val PEA_HIT: SimpleParticleType = register("pea")
    val ICE_PEA_HIT: SimpleParticleType = register("pea_ice")

    fun register(name: String): SimpleParticleType {
        val particleType = FabricParticleTypes.simple()
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, pazResource(name), particleType)
        return particleType
    }

    fun initialize() {}
}