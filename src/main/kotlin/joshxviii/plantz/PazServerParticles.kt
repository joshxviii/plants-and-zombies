package joshxviii.plantz

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.core.Registry
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries

object PazServerParticles {
    @JvmField val BUTTER_DRIP: SimpleParticleType = register("butter_drip")
    @JvmField val PEA_HIT: SimpleParticleType = register("pea")
    @JvmField val ICE_PEA_HIT: SimpleParticleType = register("pea_ice")
    @JvmField val FIRE_PEA_HIT: SimpleParticleType = register("pea_fire")
    @JvmField val SPORE: SimpleParticleType = register("spore")
    @JvmField val SPORE_HIT: SimpleParticleType = register("spore_hit")
    @JvmField val FUME_BUBBLE: SimpleParticleType = register("fume_bubble")
    @JvmField val EMBER: SimpleParticleType = register("ember")
    @JvmField val SLEEP: SimpleParticleType = register("sleep")
    @JvmField val NOTIFY: SimpleParticleType = register("notify")
    @JvmField val NEEDS_WATER: SimpleParticleType = register("needs_water")
    @JvmField val NEEDS_SUN: SimpleParticleType = register("needs_sun")
    @JvmField val NEEDS_TIME: SimpleParticleType = register("needs_time")
    @JvmField val ZOMBIE_OMEN: SimpleParticleType = register("zombie_omen")

    fun register(name: String): SimpleParticleType {
        val particleType = FabricParticleTypes.simple()
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, pazResource(name), particleType)
        return particleType
    }

    fun initialize() {}
}