package joshxviii.plantz

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.core.Registry
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries

object PazServerParticles {
    @JvmField val PEA_HIT: SimpleParticleType = register("pea")
    @JvmField val ICE_PEA_HIT: SimpleParticleType = register("pea_ice")
    @JvmField val FIRE_PEA_HIT: SimpleParticleType = register("pea_fire")
    @JvmField val SPORE: SimpleParticleType = register("spore")
    @JvmField val SPORE_HIT: SimpleParticleType = register("spore_hit")
    @JvmField val FUME_BUBBLE: SimpleParticleType = register("fume_bubble")
    @JvmField val EMBER: SimpleParticleType = register("ember")
    @JvmField val SLEEP: SimpleParticleType = register("sleep")
    @JvmField val ZOMBIE_OMEN: SimpleParticleType = register("zombie_omen")

    fun register(name: String): SimpleParticleType {
        val particleType = FabricParticleTypes.simple()
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, pazResource(name), particleType)
        return particleType
    }

    fun initialize() {}
}