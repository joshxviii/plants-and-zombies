package joshxviii.plantz.entity

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.ai.goal.BeamAttackPlantGoal
import joshxviii.plantz.ai.goal.ProjectileAttackPlantGoal
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level

class FumeShroom(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.FUME_SHROOM, level) {
    var sprayTime = -1

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(2, BeamAttackPlantGoal(
            plantEntity = this,
            beamRange = 8.0,
            beamWidth = 2.0,
            damageType = PazDamageTypes.FUME,
            cooldownTime = 35,
            actionDelay = 12))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
        })
    }

    override fun tick() {
        super.tick()

        if(sprayTime>=0 || state == PlantState.ACTION) sprayTime++
        if (sprayTime in 7..19 && this.isAlive ) {
            val eyeHeight = eyeHeight.toDouble()

            val direction = this.headLookAngle.scale(2.0)
            val speed = 0.4

            val vx = direction.x * speed
            val vy = direction.y * speed
            val vz = direction.z * speed

            repeat(5) {
                val spread = 0.07
                val randomVx = vx + (random.nextGaussian() * spread)
                val randomVy = vy + (random.nextGaussian() * spread)
                val randomVz = vz + (random.nextGaussian() * spread)

                this.level().addParticle(
                    PazServerParticles.FUME_BUBBLE,
                    direction.x * .3 + this.getRandomX(0.2),
                    this.y + eyeHeight - 0.1,
                    direction.z * .3 + this.getRandomZ(0.2),
                    randomVx, randomVy, randomVz
                )
            }
        }
        else if (sprayTime > 20) sprayTime = -1
    }
}