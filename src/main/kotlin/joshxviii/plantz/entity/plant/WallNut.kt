package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazCriteria
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazTags
import joshxviii.plantz.PazTags.EntityTypes.IGNORE_WALLNUT_COLLISION
import joshxviii.plantz.PazTags.EntityTypes.WALLNUT_DEFLECTABLE
import joshxviii.plantz.applyImpulse
import joshxviii.plantz.entity.plant.RollingPlant.Companion.ROLLING
import joshxviii.plantz.item.GardeningGloveItem
import joshxviii.plantz.pazResource
import net.minecraft.core.Direction
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf

open class WallNut(type: EntityType<out Plant>, level: Level) : Plant(type, level), RollingPlant {

    companion object {
        fun wallNutReducedDamage(entity: Entity?, damage: Float): Float {
            return if (entity is Zombie) damage*0.75f else damage
        }

        val ROLLING_ID: Identifier = pazResource("rolling")
        private const val ROLL_FRICTION = 0.99
        private val KNOCK_BACK_MODIFIER = AttributeModifier(ROLLING_ID, 99.0, AttributeModifier.Operation.ADD_VALUE)
        private val STEP_HEIGHT_MODIFIER = AttributeModifier(ROLLING_ID, 0.75, AttributeModifier.Operation.ADD_VALUE)
    }

    private var rolledOverEntities: MutableSet<Int> = mutableSetOf()

    var rolling: Boolean
        get() = this.entityData.get(ROLLING)
        set(value) { this.entityData.set(ROLLING, value) }

    override var rollRotation: Quaternionf = Quaternionf()

    override fun isRolling(): Boolean = rolling

    fun roll(direction: Vec3 = Direction.fromYRot(yRot.toDouble()).unitVec3, power: Float = 0.51f) {
        rolling = true
        applyImpulse(direction, pow = power, uncertainty = 0.1f, ignoreResistance = true)
        getAttribute(Attributes.KNOCKBACK_RESISTANCE)!!.let { if (!it.hasModifier(ROLLING_ID)) it.addTransientModifier(KNOCK_BACK_MODIFIER) }
        getAttribute(Attributes.STEP_HEIGHT)!!.let { if (!it.hasModifier(ROLLING_ID)) it.addTransientModifier(STEP_HEIGHT_MODIFIER) }
    }

    fun stopRoll() {
        rolling = false
        //yBodyRot = Direction.getApproximateNearest(Vec3(cos(yBodyRot.toDouble() * Mth.DEG_TO_RAD), 0.0, sin(yBodyRot.toDouble() * Mth.DEG_TO_RAD))).unitVec3.toAngle()
        deltaMovement = Vec3.ZERO
        applyGridClamp()
        resetRolledOverEntities()

        getAttribute(Attributes.KNOCKBACK_RESISTANCE)!!.let {
            if (it.hasModifier(ROLLING_ID)) it.removeModifier(ROLLING_ID)
        }
        getAttribute(Attributes.STEP_HEIGHT)!!.let {
            if (it.hasModifier(ROLLING_ID)) it.removeModifier(ROLLING_ID)
        }
    }

    fun resetRolledOverEntities() {
        rolledOverEntities.clear()
    }

    override fun canLookAround(): Boolean = false
    override fun clampToGrid(): Boolean = !rolling && super.clampToGrid()

    override fun limitPistonMovement(vec: Vec3): Vec3 {
        val result = super.limitPistonMovement(vec)
        if (result.length() > 0.25) roll(result.normalize())
        return result
    }

    override fun shouldDiscardFriction(): Boolean {
        return rolling || super.shouldDiscardFriction()
    }

    override fun tick() {
        super.tick()
        if (!rolling) return
        if (level() !is ServerLevel) return

        val motion = deltaMovement
        if (motion.horizontalDistance() > 1.0e-4) {
            //yBodyRot = deltaMovement.normalize().toAngle() - 90
            setDeltaMovement(
                motion.x * ROLL_FRICTION,
                motion.y,
                motion.z * ROLL_FRICTION
            )
        }

        if (deltaMovement.horizontalDistance() < 0.085 && leashHolder==null) stopRoll()
    }

    override fun getLookControl(): LookControl = noLookControl

    override fun isInvulnerableTo(level: ServerLevel, source: DamageSource): Boolean {
        if (rolling && source.`is`(PazTags.DamageTypes.IGNORED_BY_ROLLING_NUT)) return true
        return super.isInvulnerableTo(level, source)
    }

    override fun attackedWithGlove(player: Player, item: ItemStack, hand: InteractionHand) {
        if (hasPumpkinArmor()) return
        funnyBounce()
        val direction = player.lookAngle.horizontal()
        roll(direction, power = 0.35f)
        GardeningGloveItem.hurtAndDropPlant(item, player, hand)
    }

    override fun doPush(entity: Entity) {
        super.doPush(entity)
        if (rolling && entity is LivingEntity && entity !is Plant) {
            val level = level() as? ServerLevel?: return
            val source = this.damageSources().source(PazDamageTypes.PLANT, this, if (PazConfig.PLAYER_CREDIT_FOR_PLANT_KILLS) this.rootOwner else this)
            val damage = knownSpeed.length().toFloat() * 7f * PLANT_DAMAGE.toFloat()
            if (entity.hurtServer(level, source, damage)) {
                val vector = entity.position().subtract(position()).normalize()
                entity.applyImpulse(vector, pow = 1.25f, uncertainty = 0.3f)
                rolledOverEntities.add(entity.id)

                (owner as? ServerPlayer)?.let {
                    PazCriteria.BOWLING_TRIGGER.trigger(it, rolledOverEntities.size)
                }
            }
        }
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(ROLLING, false)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.putBoolean("plantz:IsRolling", rolling)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        rolling = input.getBooleanOr("plantz:IsRolling", false)
    }

    override fun attackGoals() {}

    override fun canBeCollidedWith(other: Entity?): Boolean {
        if (rolling) return super.canBeCollidedWith(other)

        hurtOnCollide(other)

        if (other?.`is`(IGNORE_WALLNUT_COLLISION) == true) return false
        return isAlive && other != attachedEntity
    }

    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float): Boolean{
        source.directEntity?.let {
            if (it.`is`(WALLNUT_DEFLECTABLE)) return false
        }
        return super.hurtServer(level, source, damage)
    }

    override fun actuallyHurt(level: ServerLevel, source: DamageSource, damage: Float) {
        super.actuallyHurt(level, source,
            wallNutReducedDamage(source.entity, damage)
        )
    }

    override fun canSurviveOn(block: BlockState): Boolean {
        return true //super.canSurviveOn(block) || !block.getCollisionShape(level(), blockPosition().below()).isEmpty
    }
}