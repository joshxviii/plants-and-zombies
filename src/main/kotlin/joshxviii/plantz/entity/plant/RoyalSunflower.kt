package joshxviii.plantz.entity.plant

import joshxviii.plantz.BeamParticleOptions
import joshxviii.plantz.ElectricArcParticleOptions
import joshxviii.plantz.NukeSmokeParticleOptions
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.ai.ZombieState
import joshxviii.plantz.ai.goal.BeamAttackGoal
import joshxviii.plantz.ai.goal.GenerateSunGoal
import joshxviii.plantz.ai.goal.NavigateToTargetGoal
import joshxviii.plantz.entity.zombie.SuperBrainzVariant
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.pathfinder.PathType
import net.minecraft.world.phys.Vec3

class RoyalSunflower(
    type: EntityType<out Plant>,
    level: Level,
) : Plant(PazEntities.ROYAL_SUNFLOWER, level) {
    companion object {
        const val BEAM_COLOR = 0xFFFF00
    }

    override fun sleepsDuringNight(): Boolean = false

    override fun clampToGrid(): Boolean = false
    override fun canSurviveOn(block: BlockState): Boolean = true

    override fun registerGoals() {
        super.registerGoals()

        goalSelector.addGoal(4, BeamAttackGoal(
            this,
            beamRange = 32.0,
            beamWidth = 1.5,
            actionDelay = 10,
            doNotExtendPastTarget = false,
            damageType = PazDamageTypes.ENERGY,
            damageMultiplier = 1.0f,
            particleFactory = { startPos, endPos ->
                val laserStart = calculateUpVector(this.xRot + 95, this.yHeadRot + 25).scale(0.9).add(startPos)
                (level() as ServerLevel).sendParticles(
                    BeamParticleOptions(endPos.offsetRandom(random, .25f),
                        color = BEAM_COLOR, width = 1.2f, lifeTime = 12),
                    true, true,
                    laserStart.x, laserStart.y, laserStart.z,
                    1, 0.0, 0.0, 0.0, 0.0
                )
                (level() as ServerLevel).sendParticles(
                    NukeSmokeParticleOptions(color = BEAM_COLOR, scale = 0.1f),
                    endPos.x, endPos.y, endPos.z,
                    3, 0.0, 0.0, 0.0, 0.0
                )
                (level() as ServerLevel).sendParticles(
                    PazServerParticles.POP,
                    endPos.x, endPos.y, endPos.z,
                    6, 0.1, 0.1, 0.1, 0.0
                )
            },
            afterHitEntityEffect = {}
        ))
        goalSelector.addGoal(3, NavigateToTargetGoal(this, keepAwayDistance = 8.0, alwaysFaceTarget = false))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && (target is Zombie
                    || (target is Enemy && isTame)
                    || (target is Player && !isTame))
        })
    }
}