package joshxviii.plantz.entity.zombie

import joshxviii.plantz.ElectricArcParticleOptions
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazDataSerializers.ELECTRIC_SLIDE_VARIANT
import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazSounds
import joshxviii.plantz.PlasmaBurstOptions
import joshxviii.plantz.ai.goal.BeamAttackGoal
import joshxviii.plantz.ai.goal.MeleeAttackActionGoal
import joshxviii.plantz.ai.goal.NavigateToTargetGoal
import joshxviii.plantz.raid.BossType
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Unit
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.Vec3
import kotlin.jvm.optionals.getOrDefault

class ElectricSlide(type: EntityType<out ElectricSlide>, level: Level) : PazZombie(type, level) {

    companion object {
        val DATA_VARIANT_ID: EntityDataAccessor<ElectricSlideVariant> = SynchedEntityData.defineId(ElectricSlide::class.java, ELECTRIC_SLIDE_VARIANT)

        private val TORNADO_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(ElectricSlide::class.java, EntityDataSerializers.INT)
        private val TORNADO_COOLDOWN_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(ElectricSlide::class.java, EntityDataSerializers.INT)
        private val ZAP_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(ElectricSlide::class.java, EntityDataSerializers.INT)
        private val ZAP_COOLDOWN_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(ElectricSlide::class.java, EntityDataSerializers.INT)
        private val MELEE_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(ElectricSlide::class.java, EntityDataSerializers.INT)

        private const val TORNADO_DURATION = 80
        private const val TORNADO_COOLDOWN = 280
        private const val ZAP_DURATION = 4
        private const val ZAP_COOLDOWN = 150
        private const val ZAP_RANGE = 8.0
    }

    init {
        xpReward = 90
    }

    val tornadoAnimation: AnimationState = AnimationState()
    val zapAnimation: AnimationState = AnimationState()

    var variant: ElectricSlideVariant
        get() = this.entityData.get(DATA_VARIANT_ID)
        set(value) = this.entityData.set(DATA_VARIANT_ID, value)
    private var tornadoTime: Int
        get() = entityData.get(TORNADO_TIME_ID)
        set(value) = entityData.set(TORNADO_TIME_ID, value)
    private var tornadoCooldown: Int
        get() = entityData.get(TORNADO_COOLDOWN_ID)
        set(value) = entityData.set(TORNADO_COOLDOWN_ID, value)
    private var zapTime: Int
        get() = entityData.get(ZAP_TIME_ID)
        set(value) = entityData.set(ZAP_TIME_ID, value)
    private var zapCooldown: Int
        get() = entityData.get(ZAP_COOLDOWN_ID)
        set(value) = entityData.set(ZAP_COOLDOWN_ID, value)
    private var meleeTime: Int
        get() = entityData.get(MELEE_TIME_ID)
        set(value) = entityData.set(MELEE_TIME_ID, value)

    override fun getAmbientSound(): SoundEvent {
        return PazSounds.DISCO_ZOMBIE_AMBIENT
    }

    override fun getHurtSound(source: DamageSource): SoundEvent {
        return PazSounds.DISCO_ZOMBIE_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return PazSounds.DISCO_ZOMBIE_DEATH
    }

    override fun getStepSound(): SoundEvent {
        return SoundEvents.ZOMBIE_STEP
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(DATA_VARIANT_ID, ElectricSlideVariant.getDefault())
        entityData.define(TORNADO_TIME_ID, 0)
        entityData.define(TORNADO_COOLDOWN_ID, TORNADO_COOLDOWN)
        entityData.define(ZAP_TIME_ID, 0)
        entityData.define(ZAP_COOLDOWN_ID, ZAP_COOLDOWN)
        entityData.define(MELEE_TIME_ID, 0)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.store("variant", ElectricSlideVariant.CODEC, variant)
        output.putInt("zap_cooldown", zapCooldown)
        output.putInt("tornado_cooldown", tornadoCooldown)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        variant = input.read("variant", ElectricSlideVariant.CODEC).getOrDefault(ElectricSlideVariant.getDefault())
        zapCooldown = input.getIntOr("zap_cooldown", ZAP_COOLDOWN)
        tornadoCooldown = input.getIntOr("tornado_cooldown", TORNADO_COOLDOWN)
    }

    override fun getMoveControl(): MoveControl<*> = if (tornadoTime > 0) noMoveControl else super.getMoveControl()

    override fun tick() {
        super.tick()
        if (tornadoCooldown>0) tornadoCooldown--
        if(tornadoTime>0) {
            if (tornadoTime == 1) playSound(PazSounds.DISCO_ZOMBIE_BOOGIE, 1.0f, 1.0f)
            tornadoAnimation.startIfStopped(tickCount)
            if (tornadoTime++ > TORNADO_DURATION) {
                tornadoAnimation.stop()
                tornadoTime = 0
                tornadoCooldown = TORNADO_COOLDOWN + random.nextInt(20)
            }
        }
        if (zapCooldown>0) zapCooldown--
        if(zapTime>0) {
            zapAnimation.startIfStopped(tickCount)
            if (zapTime++ > ZAP_DURATION) {
                zapAnimation.stop()
                zapTime = 0
                zapCooldown = ZAP_COOLDOWN + random.nextInt(20)
            }
        }
        if (meleeTime>0) {
            if (meleeTime++>22)  meleeTime=0
        }
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(3, NavigateToTargetGoal(this, keepAwayDistance = 2.5))
        goalSelector.addGoal(4, MeleeAttackActionGoal(
            usingEntity = this,
            damageMultiplier = 0.5f,
            actionDelay = 4,
            usePredicate = { meleeTime<=0 },
            actionStartEffect = {
                meleeTime = 1
                swingForAttack(usedItemHand)
            },
            afterHitEntityEffect = {
                it.addEffect(MobEffectInstance(PazEffects.ELECTRIFIED, 140, 1), this)
            }
        ))
        goalSelector.addGoal(4, BeamAttackGoal(
            this,
            beamRange = 12.0,
            beamWidth = 1.0,
            actionDelay = 5,
            doNotExtendPastTarget = true,
            damageType = PazDamageTypes.ZOMBIE_ELECTRIC,
            damageMultiplier = 0.75f,
            actionSuccessEffect = {
                if (zapTime<=0) zapTime = 1
            },
            actionPredicate = {
                zapCooldown<=0 && tornadoTime<=0
            },
            particleFactory = { startPos, endPos ->
                val laserStart = calculateUpVector(this.xRot + 95, this.yHeadRot + 25).scale(0.9).add(startPos)

                (level() as? ServerLevel)?.sendParticles(
                    PlasmaBurstOptions(color = variant.color, scale = 1.25f), laserStart.x, laserStart.y, laserStart.z,
                    1, 0.0, 0.0, 0.0, 0.0
                )
                (level() as? ServerLevel)?.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK, laserStart.x, laserStart.y, laserStart.z,
                    20, 0.0, 0.0, 0.0, 0.525
                )

                playSound(PazSounds.ELECTRIC_ZAP)
                (level() as ServerLevel).sendParticles(
                    ElectricArcParticleOptions(endPos.offsetRandom(random, .25f),
                        color = variant.color, width = 0.3f * if (variant == ElectricSlideVariant.BOOGALOO) 1.5f else 1f),
                    laserStart.x, laserStart.y, laserStart.z,
                    1, 0.0, 0.0, 0.0, 0.0
                )

                (level() as? ServerLevel)?.sendParticles(
                    PlasmaBurstOptions(color = variant.color, scale = 1.5f), endPos.x, endPos.y, endPos.z,
                    1, 0.0, 0.0, 0.0, 0.0
                )
            },
            afterHitEntityEffect = {
                it.addEffect(MobEffectInstance(PazEffects.ELECTRIFIED, 100, 0))
            }
        ))
        goalSelector.addGoal(3, ElectricTornadoGoal())
    }

    override fun canSpawnSprintParticle(): Boolean {
        return (getItemBySlot(EquipmentSlot.FEET).`is`(PazItems.ROLLER_SKATES) && knownSpeed.horizontalDistance() > 0.1f) || super.canSpawnSprintParticle()
    }

    override fun addBehaviourGoals() {
        addBehaviourGoalsNoMelee()
    }

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun getTypeName(): Component {
        return if (variant != ElectricSlideVariant.SLIDE) Component.translatable("entity.plantz.electric_slide.variant.${variant.name.lowercase()}")
        else super.getTypeName()
    }

    override fun onSpawnedFromItem() {
        if (variant == ElectricSlideVariant.BOOGALOO) BossType.bossify(this)
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, groupData)
        val skates = PazItems.ROLLER_SKATES.defaultInstance.apply { set(DataComponents.UNBREAKABLE, Unit.INSTANCE) }
        setItemSlot(EquipmentSlot.FEET, skates)
        setDropChance(EquipmentSlot.FEET, 0f)
        return data
    }

    private inner class ElectricTornadoGoal : Goal() {

        override fun canUse(): Boolean {
            if (tornadoTime > 0) return true
            if (tornadoTime != 0 || tornadoCooldown > 0 || zapTime > 0 || !isAggressive) return false
            val target = target?.takeIf { it.isAlive } ?: return false
            return distanceToSqr(target) <= ZAP_RANGE * ZAP_RANGE
        }


        override fun requiresUpdateEveryTick(): Boolean = true

        override fun start() {
            tornadoTime = 1
        }

        override fun tick() {
            if(tornadoTime in 11..60) {
                if (tickCount % 4 == 0) zapRandomTargets()
                if (tornadoTime % 2 == 0) (level() as? ServerLevel)?.sendParticles(
                    ParticleTypes.ELECTRIC_SPARK, getRandomX(0.8), randomY, getRandomZ(0.8),
                    6, 0.0, 0.0, 0.0, 0.6
                )
                if (tornadoTime % 3 == 0) (level() as? ServerLevel)?.sendParticles(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE, getRandomX(0.2), y, getRandomZ(0.2),
                    1, 0.0, 0.0, 0.0, 0.1
                )
            }
        }

        override fun stop() {

        }

        fun zapRandomTargets() {
            val candidates = level().getEntities(this@ElectricSlide, boundingBox.inflate(ZAP_RANGE)) { entity ->
                entity is LivingEntity && entity.isAlive && this@ElectricSlide.canAttack(entity)
            }.apply { shuffle() }.take(1)

            if (candidates.isNotEmpty()) playSound(PazSounds.ELECTRIC_ZAP)
            for (entity in candidates) {
                val level = level() as? ServerLevel?: return
                val damage : Float = this@ElectricSlide.attributes.getValue(Attributes.ATTACK_DAMAGE).toFloat()
                val success = entity.hurtServer(level, damageSources().source(PazDamageTypes.ZOMBIE_ELECTRIC, this@ElectricSlide, this@ElectricSlide), damage * .5f)
                if (success) {
                    entity as LivingEntity
                    entity.addEffect(MobEffectInstance(PazEffects.ELECTRIFIED, 260, 0))
                    (level() as ServerLevel).sendParticles(
                        ElectricArcParticleOptions(
                            Vec3(entity.getRandomX(0.2), entity.randomY, entity.getRandomZ(0.2)),
                            color = variant.color, width = 0.3f * if (variant == ElectricSlideVariant.BOOGALOO) 1.5f else 1f),
                        getRandomX(0.5), randomY, getRandomZ(0.5),
                        1, 0.0, 0.0, 0.0, 0.0
                    )
                }
            }
        }
    }
}