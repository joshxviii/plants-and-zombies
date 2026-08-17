package joshxviii.plantz.entity.plant

import joshxviii.plantz.NukeBlastParticleOptions
import joshxviii.plantz.NukeSmokeParticleOptions
import joshxviii.plantz.NukeWaveParticleOptions
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazTags.EntityTypes.WALLNUT_DEFLECTABLE
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class ExplodeONut(type: EntityType<out ExplosivePlant>, level: Level) : ExplosivePlant(PazEntities.EXPLODE_O_NUT, level) {

    override fun attackGoals() {}

    override fun getZenGrownSeedType(): EntityType<*> = if (random.nextFloat() < 0.8f) PazEntities.WALL_NUT else super.getZenGrownSeedType()

    override fun canBeCollidedWith(other: Entity?): Boolean = WallNut.wallNutCollision(this, other)

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean {
        source.directEntity?.let {
            if (it.`is`(WALLNUT_DEFLECTABLE)) return false
        }
        return super.hurtServer(level, source, damage)
    }

    override fun actuallyHurt(level: ServerLevel, source: DamageSource, damage: Float) {
        val reducedDamage = if (source.entity is Zombie) damage*0.666f else damage
        super.actuallyHurt(level, source, reducedDamage)
    }

    override fun tickDeath() {
        if (lastDamageSource?.directEntity != null && deathTime == 0) explode()
        else super.tickDeath()
    }

    override fun explode(
        radius: Float,
        sound: Holder.Reference<SoundEvent>,
        damageType: ResourceKey<DamageType>,
        destroyBlocks: Boolean,
        discardOnExplode: Boolean
    ) {
        super.explode(3f, sound, damageType, destroyBlocks, discardOnExplode)
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
    }

    override fun canSurviveOn(block: BlockState): Boolean {
        return super.canSurviveOn(block) || !block.getCollisionShape(level(), blockPosition().below()).isEmpty
    }
}