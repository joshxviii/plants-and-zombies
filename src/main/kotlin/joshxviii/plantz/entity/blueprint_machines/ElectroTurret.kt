package joshxviii.plantz.entity.blueprint_machines

import joshxviii.plantz.ElectricArcParticleOptions
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEffects
import joshxviii.plantz.ai.goal.MeleeAttackActionGoal
import joshxviii.plantz.entity.zombie.ZombieRobot
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

class ElectroTurret(type: EntityType<out ElectroTurret>, level: Level) : ZombieRobot(type, level) {

    override fun tick() {
        super.tick()

        if (actionTime>0) {
            actionAnimation.startIfStopped(tickCount)
            if (actionTime++>30) {
                actionAnimation.stop()
                actionTime=0
            }
        }
    }

    override fun registerGoals() {
        super.registerGoals()

        this.goalSelector.addGoal(1, MeleeAttackActionGoal(
            usingEntity = this,
            actionDelay = 9,
            damageType = PazDamageTypes.ZAP,
            usePredicate = { actionTime<=0 },
            actionStartEffect = { actionTime=1 },
            beforeHitEntityEffect = {
                it.addEffect(MobEffectInstance(PazEffects.ELECTRIFIED, 180, 0), this)
                val direction = this.headLookAngle.scale(0.5)
                (level() as? ServerLevel)?.sendParticles(
                    ElectricArcParticleOptions(
                        Vec3(it.getRandomX(0.2), it.randomY, it.getRandomZ(0.2)),
                        color = 0x65EBD8,
                        width = 0.19f
                    ),
                    x + direction.x, y + 0.4, z + direction.z,
                    1, 0.0, 0.0, 0.0, 0.0
                )
            }
        ))
    }

}