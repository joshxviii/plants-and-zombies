package joshxviii.plantz.entity.zombie

import joshxviii.plantz.*
import joshxviii.plantz.PazDataSerializers.SUPER_BRAINZ_VARIANT
import joshxviii.plantz.ai.ZombieState
import joshxviii.plantz.ai.goal.BeamAttackGoal
import joshxviii.plantz.ai.goal.MeleeAttackActionGoal
import joshxviii.plantz.ai.goal.NavigateToTargetGoal
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.jvm.optionals.getOrDefault

class SuperBrainz(type: EntityType<out SuperBrainz>, level: Level) : PazZombie(type, level) {

    companion object {
        val DATA_VARIANT_ID: EntityDataAccessor<SuperBrainzVariant> = SynchedEntityData.defineId(SuperBrainz::class.java, SUPER_BRAINZ_VARIANT)
        val BEAM_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(SuperBrainz::class.java, EntityDataSerializers.INT)
        val BEAM_COOLDOWN_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(SuperBrainz::class.java, EntityDataSerializers.INT)
        val PUNCH_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(SuperBrainz::class.java, EntityDataSerializers.INT)
        val USE_LEFT_ID: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(SuperBrainz::class.java, EntityDataSerializers.BOOLEAN)
        val BEAM_MODE_SPEED_ID: Identifier = pazResource("beam_mode")
        private val BEAM_MODE_SPEED = AttributeModifier(BEAM_MODE_SPEED_ID, -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)

        const val BEAM_COOLDOWN = 280
        const val BEAM_MODE_TIME = 80
    }

    init {
        xpReward = 40
    }

    var xCape = 0.0f
    var yCape = 0.0f
    var zCape = 0.0f
    var xCapeO = 0.0f
    var yCapeO = 0.0f
    var zCapeO = 0.0f

    var walkDist = 0f
    var walkDistO = 0f

    val laserAttackAnimation : AnimationState = AnimationState()
    val rightPunchAnimation : AnimationState = AnimationState()
    val leftPunchAnimation : AnimationState = AnimationState()

    var variant: SuperBrainzVariant
        get() = this.entityData.get(DATA_VARIANT_ID)
        set(value) = this.entityData.set(DATA_VARIANT_ID, value)

    var beamAttackTime: Int
        get() = this.entityData.get(BEAM_TIME_ID)
        set(value) = this.entityData.set(BEAM_TIME_ID, value)

    var punchTime: Int
        get() = this.entityData.get(PUNCH_TIME_ID)
        set(value) = this.entityData.set(PUNCH_TIME_ID, value)

    var useLeft: Boolean
        get() = this.entityData.get(USE_LEFT_ID)
        set(value) = this.entityData.set(USE_LEFT_ID, value)

    var beamCooldown: Int
        get() = this.entityData.get(BEAM_COOLDOWN_ID)
        set(value) = this.entityData.set(BEAM_COOLDOWN_ID, value)

    fun removeBeamModifiers() {
        getAttribute(Attributes.MOVEMENT_SPEED)!!.removeModifier(BEAM_MODE_SPEED_ID)
    }
    fun applyBeamModifiers() {
        getAttribute(Attributes.MOVEMENT_SPEED)!!.addTransientModifier(BEAM_MODE_SPEED)
    }

    override fun tick() {
        super.tick()
        if (beamCooldown>0) beamCooldown--
        if(beamAttackTime>0 && state != ZombieState.FLYING) {
            if (beamAttackTime==1) applyBeamModifiers()
            laserAttackAnimation.startIfStopped(tickCount)
            val laserStart = calculateUpVector(this.xRot + 95, this.yHeadRot + 25).scale(0.9).add(eyePosition)
            (level() as? ServerLevel)?.sendParticles(
                ParticleTypes.ELECTRIC_SPARK, laserStart.x, laserStart.y, laserStart.z,
                4, 0.0, 0.0, 0.0, 0.5
            )
            if (beamAttackTime++>BEAM_MODE_TIME) {
                laserAttackAnimation.stop()
                beamAttackTime=0
                beamCooldown = BEAM_COOLDOWN + random.nextInt(20)
                removeBeamModifiers()
            }
        }
        if(punchTime>0) {
            if (useLeft) leftPunchAnimation.startIfStopped(tickCount)
            else rightPunchAnimation.startIfStopped(tickCount)
            if (punchTime++>14) {
                rightPunchAnimation.stop()
                leftPunchAnimation.stop()
                punchTime=0
                useLeft = !useLeft
            }
        }

        updateCapeState()
        val level = level() as? ServerLevel?: return
        if (tickCount % 60 == 0) when (state) {
            ZombieState.IDLE -> {
                target?.let {
                    if (it.y > y + 4.5) state = ZombieState.FLYING
                }
                if (moveControl.wantedY > y + 4.5) state = ZombieState.FLYING
            }
            ZombieState.FLYING -> {
                val floorHeight = y - level.getHeight(Heightmap.Types.WORLD_SURFACE, blockPosition()).toDouble()
                if (floorHeight<2) state = ZombieState.IDLE
            }
            else -> {}
        }
        if (state == ZombieState.FLYING && tickCount%2==0) level.sendParticles(
            ParticleTypes.WHITE_SMOKE,
            x, boundingBox.ysize*0.5+y, z, 2, 0.1, 0.2, 0.1, 0.05
        )
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(DATA_VARIANT_ID, SuperBrainzVariant.getDefault())
        entityData.define(BEAM_TIME_ID, 0)
        entityData.define(BEAM_COOLDOWN_ID, BEAM_COOLDOWN)
        entityData.define(PUNCH_TIME_ID, 0)
        entityData.define(USE_LEFT_ID, false)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.store("variant", SuperBrainzVariant.CODEC, variant)
        output.putInt("beamCooldown", beamCooldown)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        variant = input.read("variant", SuperBrainzVariant.CODEC).getOrDefault(SuperBrainzVariant.pickRandomVariant())
        beamCooldown = input.getIntOr("beamCooldown", BEAM_COOLDOWN)
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(3, NavigateToTargetGoal(this))
        goalSelector.addGoal(4, MeleeAttackActionGoal(// smash
            this,
            damageType = DamageTypes.MOB_ATTACK,
            actionDelay = 8,
            usePredicate = {
                punchTime<=0 && beamAttackTime<=0
            },
            actionStartEffect = {
                punchTime=1
            }
        ))
        goalSelector.addGoal(4, BeamAttackGoal(
            this,
            beamRange = 12.0,
            beamWidth = 1.0,
            actionDelay = 10,
            doNotExtendPastTarget = true,
            damageType = PazDamageTypes.ZOMBIE_ENERGY,
            damageMultiplier = 0.2f,
            actionPredicate = {
                state != ZombieState.FLYING && beamCooldown<=0 && punchTime<=0
            },
            particleFactory = { startPos, endPos ->
                val laserStart = calculateUpVector(this.xRot + 95, this.yHeadRot + 25).scale(0.9).add(startPos)
                if (variant != SuperBrainzVariant.ELECTRO) (level() as ServerLevel).sendParticles(
                    BeamParticleOptions(endPos.offsetRandom(random, .25f),
                        color = variant.beamColor, width = 0.28f, lifeTime = 10),
                    laserStart.x, laserStart.y, laserStart.z,
                    1, 0.0, 0.0, 0.0, 0.0
                )
                else (level() as ServerLevel).sendParticles(
                    ElectricArcParticleOptions(endPos.offsetRandom(random, .25f),
                        color = variant.beamColor, width = 0.14f),
                    laserStart.x, laserStart.y, laserStart.z,
                    1, 0.0, 0.0, 0.0, 0.0
                )
                (level() as ServerLevel).sendParticles(
                    NukeSmokeParticleOptions(color = variant.beamColor, scale = 0.1f),
                    endPos.x, endPos.y, endPos.z,
                    3, 0.0, 0.0, 0.0, 0.0
                )
                (level() as ServerLevel).sendParticles(
                    PazServerParticles.POP,
                    endPos.x, endPos.y, endPos.z,
                    2, 0.0, 0.0, 0.0, 0.0
                )
            },
            afterHitEntityEffect = {
                if (beamAttackTime<=0) beamAttackTime = 1
                when (variant) {
                    SuperBrainzVariant.SUPER -> {}
                    SuperBrainzVariant.TOXIC -> it.addEffect(MobEffectInstance(MobEffects.POISON, 100, 0))
                    SuperBrainzVariant.ELECTRO -> it.addEffect(MobEffectInstance(PazEffects.ELECTRIFIED, 100, 0))
                }
            }
        ))
    }

    override fun addBehaviourGoals() {
        addBehaviourGoalsNoMelee()
    }

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    fun updateCapeState() {
        walkDistO = walkDist
        val delta = deltaMovement

        xCapeO = xCape
        yCapeO = yCape
        zCapeO = zCape

        val dx = (x - xCape).toFloat()
        val dy = (y - yCape).toFloat()
        val dz = (z - zCape).toFloat()

        if (dx * dx + dy * dy + dz * dz > 100.0) {
            xCape = x.toFloat()
            yCape = y.toFloat()
            zCape = z.toFloat()
            xCapeO = xCape
            yCapeO = yCape
            zCapeO = zCape
        } else {
            xCape += dx * 0.25f
            yCape += dy * 0.25f
            zCape += dz * 0.25f
        }

        val horizontalSpeed = delta.horizontalDistance()
        walkDist += horizontalSpeed.toFloat()
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, ZombieGroupData(false, false))

        beamCooldown = BEAM_COOLDOWN

        if (spawnReason != EntitySpawnReason.CONVERSION) {
            setCanBreakDoors(true)
        }

        return data
    }
}