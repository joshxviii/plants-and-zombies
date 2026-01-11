package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.Plant
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import kotlin.math.atan
import kotlin.math.sqrt

class ProjectileAttackPlantGoal(
    plantEntity: Plant,
    cooldownTime: Int = 20,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    val projectileFactory: () -> Projectile,
    val velocity : Double = 0.9,
    val inaccuracy: Float = 1.0f,
    val useHighArc: Boolean = false,
) : PlantActionGoal(plantEntity, cooldownTime, actionDelay, actionStartEffect, actionEndEffect) {
    var seeTime : Int = 0
    var distanceSqr: Double = 0.0
    var attackRadius : Float = 0.0f

    override fun canUse(): Boolean = (
        plantEntity.tickCount>cooldownTime
        && plantEntity.target?.isAlive == true
    )

    override fun stop() {
        super.stop()
        plantEntity.target = null
        seeTime = 0
    }

    override fun canDoAction(): Boolean {// check distance and line of sight
        val target = plantEntity.target ?: return false
        if (!target.isAlive) return false

        distanceSqr = plantEntity.distanceToSqr(target)
        attackRadius = plantEntity.attributes.getValue(Attributes.FOLLOW_RANGE).toFloat()
        val hasLineOfSight = plantEntity.sensing.hasLineOfSight(target)

        if (hasLineOfSight) seeTime++
        else seeTime = 0

        plantEntity.lookControl.setLookAt(target, 30f, 30f)

        return hasLineOfSight && distanceSqr <= (attackRadius * attackRadius)
    }

    override fun doAction() : Boolean {// fire projectile
       val target = plantEntity.target?: return false

        val relativePos = Vec3(
            target.x - plantEntity.x,
            target.y - plantEntity.y,
            target.z - plantEntity.z
        )

        val level = plantEntity.level() as ServerLevel
        val projectile = projectileFactory()

        val arcs = calculateProjectileArcs(relativePos, projectile.gravity, velocity)
        if (arcs==null) {// lose target if unreachable
           plantEntity.target = null
           return false
        }

        val chosenArc = if(useHighArc) arcs.first else arcs.second

        val horizDist = relativePos.horizontalDistance()
        val horizUnitX = relativePos.x / horizDist
        val horizUnitZ = relativePos.z / horizDist
        val horizComp = Mth.cos(chosenArc)

        val shootX = (horizUnitX * horizComp)
        val shootY = Mth.sin(chosenArc).toDouble()
        val shootZ = (horizUnitZ * horizComp)

        Projectile.spawnProjectile(projectile, level, ItemStack.EMPTY) {
            it.shoot(shootX, shootY, shootZ, velocity.toFloat(), inaccuracy)
        }

        plantEntity.playSound(SoundEvents.BUBBLE_POP, 3.0f, 0.4f / (plantEntity.random.nextFloat() * 0.4f + 0.8f))
        return true
    }

    /**
     * Calculates the high-arc (φ₁, steeper) and low-arc (φ₂, flatter) elevation angles (radians)
     * for a projectile to hit the target position with given initial velocity and gravity.
     *
     * @param targetPos Relative target position from launch point (Vec3)
     * @param gravity Projectile gravity
     * @param velocity Initial projectile speed (blocks/tick)
     * @return Pair(highArcAngle, lowArcAngle) or null if impossible (discriminant < 0)
     */
    private fun calculateProjectileArcs(targetPos: Vec3, gravity: Double, velocity: Double): Pair<Double, Double>? {
        val dx = targetPos.x
        val dy = targetPos.y
        val dz = targetPos.z

        val horizDist = sqrt(dx * dx + dz * dz).toFloat()
        if (horizDist <= 0f) return null

        val v2: Double = velocity * velocity
        val v4 = v2 * v2
        val g_d = gravity
        val horiz2_d = horizDist.toDouble() * horizDist
        val discriminant = v4 - g_d * (g_d * horiz2_d + 2.0 * v2 * dy)

        //impossible shot
        if (discriminant < 0.0) return null

        val sqrtDisc = sqrt(discriminant)
        val num1 = v2 + sqrtDisc
        val num2 = v2 - sqrtDisc
        val denom = g_d * horizDist.toDouble()

        val phi1 = atan((num1 / denom))
        val phi2 = atan((num2 / denom))

        return phi1 to phi2
    }
}