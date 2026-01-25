package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.Spore
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level

class HypnoShroom(type: EntityType<out Mushroom>, level: Level) : Mushroom(PazEntities.HYPNOSHROOM, level) {
    override fun registerGoals() {
        super.registerGoals()

        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
            && target !is Creeper
            && target !is Plant
        })

    }

    override fun actuallyHurt(level: ServerLevel, source: DamageSource, dmg: Float) {
        super.actuallyHurt(level, source, dmg)
        val attacker = source.entity
        if (attacker is LivingEntity) {
            attacker.addEffect(MobEffectInstance(PazEffects.HYPNOTIZE, 800, 0))
        }
    }
}