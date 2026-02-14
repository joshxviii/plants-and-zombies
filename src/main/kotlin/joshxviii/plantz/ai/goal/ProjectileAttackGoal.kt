package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazSounds
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.phys.Vec3
import java.util.function.Predicate
import kotlin.math.atan
import kotlin.math.sqrt

class ProjectileAttackGoal(
    usingEntity: PathfinderMob,
    cooldownTime: Int = 20,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    actionPredicate: Predicate<PathfinderMob> = Predicate { true },
    val projectileFactory: () -> Entity,
    val velocity : Double = 0.9,
    val inaccuracy: Float = 0.8f,
    val useHighArc: Boolean = false,
    val soundEvent: SoundEvent = PazSounds.PROJECTILE_FIRE,
) : ActionGoal(usingEntity, cooldownTime, actionDelay, actionStartEffect, actionEndEffect, actionPredicate) {
    var distanceSqr: Double = 0.0
    var attackRadius : Float = 0.0f

    override fun canUse(): Boolean = (
        usingEntity.tickCount>cooldownTime
            && usingEntity.target?.isAlive == true
            && !(usingEntity is Plant && (usingEntity.isAsleep || usingEntity.isGrowingSeeds))
    )

    override fun stop() {
        super.stop()
        usingEntity.isAggressive = false
        usingEntity.stopUsingItem()
        usingEntity.target = null
    }

    override fun canDoAction(): Boolean {// check distance and line of sight
        val target = usingEntity.target ?: return false
        if (!target.isAlive) return false

        distanceSqr = usingEntity.distanceToSqr(target)
        attackRadius = usingEntity.attributes.getValue(Attributes.FOLLOW_RANGE).toFloat()

        usingEntity.lookControl.setLookAt(target, 30f, 30f)
        usingEntity.isAggressive = true

        return distanceSqr <= (attackRadius * attackRadius)
    }

    override fun tick() {
        super.tick()

        if (usingEntity.isUsingItem) {
            if (!canDoAction()) {
                usingEntity.isAggressive = false
                usingEntity.stopUsingItem()
            }
            else {
                val pullTime: Int = usingEntity.ticksUsingItem
                if (pullTime >= actionDelay-1) {
                    usingEntity.stopUsingItem()
                }
            }
        } else usingEntity.startUsingItem(ProjectileUtil.getWeaponHoldingHand(usingEntity, Items.BOW))
    }

    override fun doAction() : Boolean {// fire projectile
       val target = usingEntity.target?: return false

        val level = usingEntity.level() as ServerLevel
        val projectile = projectileFactory()
        if (projectile is Projectile) Projectile.spawnProjectile(projectile, level, ItemStack.EMPTY)
        else level.addFreshEntity(projectile)

        val targetPos = calculateMovingTargetPosition(target, projectile)
        val arcs = calculateProjectileArcs(targetPos, projectile.gravity, velocity)
        if (arcs==null) {// lose target if unreachable
            projectile.discard()
            usingEntity.target = null
            return false
        }

        val finalAngle = if(useHighArc) arcs.first else arcs.second

        val horizDist = targetPos.horizontalDistance()

        val horizUnitX = targetPos.x / horizDist
        val horizUnitZ = targetPos.z / horizDist
        val horizComp = Mth.cos(finalAngle)

        val shootX = (horizUnitX * horizComp)
        val shootY = Mth.sin(finalAngle).toDouble()
        val shootZ = (horizUnitZ * horizComp)

        if (projectile is Projectile) projectile.shoot(shootX, shootY, shootZ, velocity.toFloat(), inaccuracy)
        else {
            projectile.deltaMovement = getMovementToShoot(shootX, shootY, shootZ, velocity.toFloat(), inaccuracy)
            projectile.needsSync = true
        }

        usingEntity.playSound(soundEvent, 3.0f, 0.4f / (usingEntity.random.nextFloat() * 0.4f + 0.8f))
        return true
    }

    private fun calculateMovingTargetPosition(target: LivingEntity, projectile: Entity): Vec3 {
        val basePos = Vec3(
            target.x - projectile.x,
            target.boundingBox.minY + (target.bbHeight / 3.0) - projectile.y,
            target.z - projectile.z
        )
        return basePos
        //TODO

        val targetVel: Vec3 = target.deltaMovement

        var predicted = basePos

        val g = projectile.gravity
        val v = velocity

        val maxLeadTicks = 60.0
        val iterations = 10
        val epsilon = 1.0e-3

        for (i in 0 until iterations) {
            val arcs = calculateProjectileArcs(predicted, g, v) ?: break
            val angle = if (useHighArc) arcs.first else arcs.second

            val horizDist = predicted.horizontalDistance()
            if (horizDist < 1.0e-6) break

            val cos = Mth.cos(angle).toDouble()
            if (cos <= 1.0e-6) break

            val t = (horizDist / (v * cos)).coerceIn(0.0, maxLeadTicks)

            val next = basePos.add(targetVel.scale(t))
            if (next.distanceTo(predicted) < epsilon) {
                predicted = next
                break
            }
            predicted = next
        }

        return predicted
    }

    /**
     * Calculates the high-arc (φ₁, steeper) and low-arc (φ₂, flatter) elevation angles (radians)
     * for a projectile to hit the target position with given initial velocity and gravity.
     *
     * @param targetPos Relative target position from launch point (Vec3)
     * @param g Projectile's gravity
     * @param velocity Initial projectile speed (blocks/tick)
     * @return Pair(highArcAngle, lowArcAngle)
     */
    private fun calculateProjectileArcs(targetPos: Vec3, g: Double, velocity: Double): Pair<Double, Double>? {
        val dx = targetPos.x
        val dy = targetPos.y
        val dz = targetPos.z

        val horizDist = sqrt(dx * dx + dz * dz)
        if (horizDist <= 0f) return null

        val v2: Double = velocity*velocity
        val v4 = v2 * v2
        val horiz2_d = horizDist * horizDist
        var discriminant = v4 - g * (g * horiz2_d + 2.0 * v2 * dy)

        //impossible shot
        if (discriminant < 0.0) discriminant = 0.0

        val sqrtDisc = sqrt(discriminant)
        val denom = g * horizDist

        val phi1 = atan((v2 + sqrtDisc) / denom)
        val phi2 = atan((v2 - sqrtDisc) / denom)

        return phi1 to phi2
    }

    fun getMovementToShoot(xd: Double, yd: Double, zd: Double, pow: Float, uncertainty: Float): Vec3 {
        return Vec3(xd, yd, zd)
            .normalize()
            .add(
                usingEntity.random.triangle(0.0, 0.0172275 * uncertainty),
                usingEntity.random.triangle(0.0, 0.0172275 * uncertainty),
                usingEntity.random.triangle(0.0, 0.0172275 * uncertainty)
            )
            .scale(pow.toDouble())
    }
}