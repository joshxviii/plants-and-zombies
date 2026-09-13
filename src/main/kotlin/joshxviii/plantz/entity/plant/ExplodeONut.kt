package joshxviii.plantz.entity.plant

import joshxviii.plantz.NukeBlastParticleOptions
import joshxviii.plantz.NukeSmokeParticleOptions
import joshxviii.plantz.NukeWaveParticleOptions
import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazSounds
import joshxviii.plantz.entity.plant.ExplosivePlant.Companion.EXPLOSION_CALCULATOR
import net.minecraft.core.Holder
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.util.random.WeightedList
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class ExplodeONut(type: EntityType<out Plant>, level: Level) : WallNut(type, level) {

    override fun tickDeath() {
        if (lastDamageSource?.directEntity != null && deathTime == 0) explode()
        else super.tickDeath()
    }

    override fun doPush(entity: Entity) {
        super.doPush(entity)
        if (isRolling) explode()
    }

    fun explode(
        radius: Float = 4.0f,
        sound: Holder.Reference<SoundEvent> = PazSounds.PLANT_EXPLODE,
        damageType: ResourceKey<DamageType> = PazDamageTypes.PLANT_EXPLODE
    ) {
        val source = this.damageSources().source(damageType, this,
            if (PazConfig.PLAYER_CREDIT_FOR_PLANT_KILLS) this.rootOwner else this)
        level().explode(
            this,
            source,
            EXPLOSION_CALCULATOR,
            x, y, z,
            radius,
            false,
            Level.ExplosionInteraction.MOB,
            ParticleTypes.SMOKE,
            ParticleTypes.EXPLOSION,
            WeightedList.of(),
            sound
        )

        val level = level() as? ServerLevel ?: return
        level.sendParticles(NukeWaveParticleOptions(color = 0xD0370D, scale = 2f),
            x, y, z, 1, 0.0, 0.0, 0.0, 0.0
        )
        level.sendParticles(NukeBlastParticleOptions(color = 0xFFE88D, scale = 1.5f),
            x, y, z, 1, 0.0, 0.0, 0.0, 0.0
        )
        level.sendParticles(NukeSmokeParticleOptions(color = 0xB87878, scale = 0.6f),
            x, y+1, z, 15, 0.0, 0.5, 0.0, 0.0
        )
        discard()
    }
}