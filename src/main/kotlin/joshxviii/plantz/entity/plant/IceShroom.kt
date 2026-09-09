package joshxviii.plantz.entity.plant

import joshxviii.plantz.*
import joshxviii.plantz.ai.goal.ExplodeGoal
import net.minecraft.core.Holder
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.AreaEffectCloud
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class IceShroom(type: EntityType<out Plant>, level: Level) : ExplosivePlant(PazEntities.ICE_SHROOM, level) {
    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, ExplodeGoal(
            explosiveEntity = this,
            explosionRadius = 6f,
            activateRange = 4.0,
            startSound = SoundEvents.EMPTY,
            actionEndEffect = { cooldown = 480 },
            actionPredicate = { cooldown <= 0 }
        ))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && (target is Zombie
                    || (target is Enemy && isTame)
                    || (target is Player && !isTame))
        })
    }

    override fun getMaxSwellTime(): Int = 28
    override fun discardOnExplode(): Boolean = false

    override fun explode(
        radius: Float,
        sound: Holder.Reference<SoundEvent>,
        damageType: ResourceKey<DamageType>,
        destroyBlocks: Boolean,
        discardOnExplode: Boolean
    ) {
        swellDir = -1
        swell = 0

        playSound(SoundEvents.PLAYER_HURT_FREEZE, 1.0f, 0.5f)

        if (!level().isClientSide) addParticlesAroundSelf(
            particle = PazServerParticles.ICE_PEA_HIT,
            amount = 60..80,
            horizontalSpreadScale = 1.6,
            verticalSpreadScale = 0.6,
            height = 0.6f,
            speed = 1.4
        )

        val candidates = level().getEntities(this, boundingBox.inflate(radius.toDouble())) { entity ->
            entity is LivingEntity && entity.isAlive && !entity.hasSameRootOwner(this) && owner != entity
        }

        for (entity in candidates) {
            entity as LivingEntity
            entity.addEffect(MobEffectInstance(PazEffects.FROZEN, 300, 0))
            val direction = entity.position().subtract(position()).normalize()
            entity.applyImpulse(direction.x, direction.y+0.2f, direction.z, 1.0f)
        }

    }
}