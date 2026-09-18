package joshxviii.plantz.entity.plant

import joshxviii.plantz.*
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.AreaEffectCloud
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level

class HypnoShroom(type: EntityType<out Plant>, level: Level) : Plant(type, level) {
    override fun registerGoals() {
        super.registerGoals()
    }

    override fun attackGoals() {}

    override fun canBeCollidedWith(other: Entity?): Boolean {
        if (other is Zombie && other.swingTime == 0) {// when colliding with a zombie, the zombie will attack
            val level = other.level() as? ServerLevel
            if (level != null && other.isAlive) {
                val damage = other.getAttribute(Attributes.ATTACK_DAMAGE)?.value?.toFloat() ?: 1f
                if (hurtServer(level, other.damageSources().mobAttack(other), damage)) other.swing(InteractionHand.MAIN_HAND)
            }
        }
        return super.canBeCollidedWith(other)
    }

    override fun actuallyHurt(level: ServerLevel, source: DamageSource, damage: Float) {
        super.actuallyHurt(level, source, damage)
        if (isAsleep) return
        val attacker = source.entity
        if (attacker is LivingEntity && !attacker.isInvulnerable) {
            addParticlesAroundSelf(
                particle = PazServerParticles.HYPNO_SPORE,
                amount = 45..50,
                horizontalSpreadScale = 0.6,
                verticalSpreadScale = 0.6,
                height = 0.6f
            )
            attacker.addEffect(MobEffectInstance(PazEffects.HYPNOTIZE, 800, 0))
            val owner = owner
            if (owner is ServerPlayer) PazCriteria.DISCO_HYPNO.trigger(owner, attacker.`is`(PazEntities.DISCO_ZOMBIE))
            playSound(PazSounds.HYPNOTIZED)
        }
    }

    override fun die(source: DamageSource) {
        super.die(source)
        spawnHypnosisCloud()
    }

    private fun spawnHypnosisCloud() {
        val cloud = AreaEffectCloud(level(), x, y+0.1, z)
        cloud.radius = 2.5f
        cloud.radiusOnUse = -0.5f
        cloud.waitTime = 10
        cloud.duration = 300
        cloud.setPotionDurationScale(0.25f)
        cloud.setCustomParticle(PazServerParticles.HYPNO_SPORE)
        cloud.radiusPerTick = -cloud.radius / cloud.duration.toFloat()
        cloud.addEffect(MobEffectInstance(PazEffects.HYPNOTIZE, 1000, 0))
        level().addFreshEntity(cloud)
    }
}