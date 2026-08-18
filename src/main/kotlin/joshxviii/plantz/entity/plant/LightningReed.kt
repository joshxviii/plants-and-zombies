package joshxviii.plantz.entity.plant

import joshxviii.plantz.*
import joshxviii.plantz.ai.goal.MeleeAttackActionGoal
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.phys.Vec3

class LightningReed(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.LIGHTNING_REED, level) {
    companion object {
        fun checkLightningReedSpawnRules(
            type: EntityType<out Plant>,
            level: ServerLevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            val isThundering = level.level.isThundering

            return checkValidSpawn(level, pos, spawnReason)
                    && isThundering && pos.y > level.seaLevel - 8
        }
    }

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, MeleeAttackActionGoal(
            usingEntity = this,
            actionDelay = 6,
            cooldownTime = 16,
            damageType = PazDamageTypes.PLANT_ELECTRIC,
            beforeHitEntityEffect = {
                it.addEffect(MobEffectInstance(PazEffects.ELECTRIFIED, 100, 1), this)
                val eyeHeight = eyeHeight.toDouble()
                val direction = this.headLookAngle.scale(0.5)
                (level() as? ServerLevel)?.sendParticles(
                    ElectricArcParticleOptions(
                        Vec3(it.getRandomX(0.2), it.randomY, it.getRandomZ(0.2)),
                        color = 0xBDFDF5,
                        width = 0.15f
                    ),
                    x + direction.x, y + eyeHeight, z + direction.z,
                    1, 0.0, 0.0, 0.0, 0.0
                )
            }
        ))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && target !is Creeper
                    && (target is Zombie
                    || (target is Enemy && isTame)
                    || (target is Player && !isTame))
        })
    }

    override fun tick() {
        super.tick()

        if (tickCount % 3 == 0 && tickCount > 18 && isAlive) {

            val direction = calculateUpVector(this.xRot - 50, this.yHeadRot).scale(0.3)
            this.level().addParticle(
                PazServerParticles.ELECTRIFIED,
                direction.x.toFloat() + this.getRandomX(0.2),
                direction.y.toFloat() + this.y + eyeHeight.toDouble() - 0.1,
                direction.z.toFloat() + this.getRandomZ(0.2),
                0.0, 0.0, 0.0,
            )

        }
    }
}
