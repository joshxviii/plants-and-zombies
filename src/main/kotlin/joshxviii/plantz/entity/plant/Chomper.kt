package joshxviii.plantz.entity.plant

import joshxviii.plantz.*
import joshxviii.plantz.PazTags.EntityTypes.CANNOT_CHOMP
import joshxviii.plantz.ai.goal.MeleeAttackActionGoal
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.chicken.Chicken
import net.minecraft.world.entity.animal.fish.AbstractFish
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class Chomper(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.CHOMPER, level) {

    companion object {
        private val CHOMP_ATTACK_MODIFIER = AttributeModifier(
            pazResource("chomp_attack"), 100.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        )
        val CHEW_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(Chomper::class.java, EntityDataSerializers.INT)
    }

    var chewTime: Int
        get() = this.entityData.get(CHEW_TIME_ID)
        set(value) = this.entityData.set(CHEW_TIME_ID, value)

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(CHEW_TIME_ID, 0)
    }

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, ChompAttackGoal(this))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && target is Zombie
                    || target is AbstractFish
                    || target is Chicken
                    || (target is Enemy && isTame)
                    || (target is Player && !isTame)
        })
    }

    override fun tick() {
        super.tick()
        if (chewTime > 0) {
            chewTime--
            cooldown = chewTime
            coolDownAnimationState.startIfStopped(tickCount - idleAnimationStartTick)
            if (tickCount % 24 == 0) playSound(SoundEvents.CAMEL_EAT, 0.15f, 0.9f) // TODO Custom Sound
            if (random.nextInt(12) == 0) {
                val eyeHeight = eyeHeight.toDouble()
                val direction = this.headLookAngle.scale(1.5)
                val speed = 0.01

                val vx = direction.x * speed
                val vy = direction.y * speed
                val vz = direction.z * speed

                repeat(7) {
                    val spread = 0.1
                    val randomVx = vx + (random.nextGaussian() * spread)
                    val randomVy = vy + (random.nextGaussian() * spread)
                    val randomVz = vz + (random.nextGaussian() * spread)

                    level().addParticle(
                        PazServerParticles.SPORE_HIT,
                        direction.x * .3 + this.getRandomX(0.9),
                        this.y + eyeHeight - 0.2,
                        direction.z * .3 + this.getRandomZ(0.9),
                        randomVx, randomVy, randomVz
                    )
                }
            }
        }
    }

    class ChompAttackGoal(
        val chomperEntity: Chomper,
    ) : MeleeAttackActionGoal(
        usingEntity = chomperEntity,
        attackReach = 1.85,
        cooldownTime = 60,
        actionDelay = 10,
        damageType = PazDamageTypes.CHOMP,
        actionStartEffect = {
            chomperEntity.playSound(PazSounds.CHOMPER_ATTACK)
        },
        actionPredicate = { chomperEntity.chewTime <= 0 }
    ) {
        companion object {
            const val CHEW_TIME = 300
        }

        override fun doAction() : Boolean {
            val target = usingEntity.target?: return false
            if(!target.`is`(CANNOT_CHOMP)) {
                //Add modifier to increase damage for insta kills
                usingEntity.getAttribute(Attributes.ATTACK_DAMAGE)?.addOrUpdateTransientModifier(CHOMP_ATTACK_MODIFIER)
            }

            !super.doAction()

            //remove modifier if it was added
            usingEntity.getAttribute(Attributes.ATTACK_DAMAGE)?.removeModifier(CHOMP_ATTACK_MODIFIER)
            if (!target.isAlive) {
                (usingEntity.level() as ServerLevel).sendParticles(
                    PazServerParticles.SPORE_HIT, target.x,target.y+target.eyeHeight,target.z,
                    30,
                    0.2, 0.2, 0.2,
                    0.32
                )
                target.discard()
                chomperEntity.chewTime = CHEW_TIME
            }
            return true
        }
    }
}