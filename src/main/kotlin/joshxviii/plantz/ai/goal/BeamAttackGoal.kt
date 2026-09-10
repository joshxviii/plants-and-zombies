package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.OwnableEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.function.Predicate

class BeamAttackGoal(
    usingEntity: PathfinderMob,
    cooldownTime: Int = 20,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionSuccessEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    actionPredicate: Predicate<PathfinderMob> = Predicate { true },
    val doNotExtendPastTarget: Boolean = false,
    val beamRange : Double = 10.0,
    val beamWidth : Double = 3.25,
    val damageMultiplier: Float = 1.0f,
    val damageType: ResourceKey<DamageType> = PazDamageTypes.PLANT,
    val particleFactory : (startPos: Vec3, targetPos: Vec3) -> Unit = { _, _ -> },
    val afterHitEntityEffect: (target: LivingEntity) -> Unit = {},
) : ActionGoal(usingEntity, cooldownTime, actionDelay, actionStartEffect, actionSuccessEffect, actionEndEffect, actionPredicate) {
    private var piercedEntities: MutableList<Entity>? = null

    override fun canUse(): Boolean = (
        usingEntity.target?.isAlive == true
            && !(usingEntity is Plant && (usingEntity.isAsleep || usingEntity.isGrowingSeeds))
    )

    override fun canDoAction(): Boolean {
        val target = usingEntity.target?: return false
        usingEntity.lookControl.setLookAt(target, 30f, 30f)

        return isInReach(target);
    }

    override fun doAction(): Boolean {
        val target = usingEntity.target ?: return false
        if (!target.isAlive) return false

        piercedEntities = mutableListOf()

        val start = usingEntity.position().add(0.0, usingEntity.eyeHeight.toDouble(), 0.0)

        val vector = target.eyePosition.subtract(start)
        val direction = vector.normalize()

        val end = if (doNotExtendPastTarget && vector.length() < beamRange) start.add(vector)
        else start.add(direction.scale(beamRange))

        val beamAABB = AABB(start, end).inflate(beamWidth / 2.0 + 1.0)

        val candidates = usingEntity.level().getEntities(usingEntity, beamAABB) { entity ->
            entity is LivingEntity && entity.isAlive
        }

        for (target in candidates) {
            if (!usingEntity.canAttack(target as LivingEntity)) continue
            val distToRay = distanceToLineSegment(target.eyePosition, start, end)
            val entityRadiusApprox = target.boundingBox.size / 2.0
            if (distToRay <= (beamWidth / 2.0 + entityRadiusApprox)) {

                val damage : Float = usingEntity.attributes.getValue(Attributes.ATTACK_DAMAGE).toFloat() * damageMultiplier
                val knockback : Double = usingEntity.attributes.getValue(Attributes.ATTACK_KNOCKBACK)
                val source = usingEntity.damageSources().source(damageType, usingEntity,
                    if (PazConfig.PLAYER_CREDIT_FOR_PLANT_KILLS && usingEntity is OwnableEntity) usingEntity.rootOwner else null)

                if (target.hurtServer(usingEntity.level() as ServerLevel, source, damage)) {
                    afterHitEntityEffect(target)
                    target.knockback(
                        knockback,
                        start.x - target.x,
                        start.z - target.z
                    )
                }
                piercedEntities?.add(target)
            }
        }

        particleFactory(start, end)
        return piercedEntities?.isNotEmpty() ?: false
    }

    private fun isInReach(target: LivingEntity): Boolean {
        return usingEntity.boundingBox.inflate(beamRange).intersects(target.boundingBox) &&
                usingEntity.sensing.hasLineOfSight(target)
    }

    private fun distanceToLineSegment(point: Vec3, lineStart: Vec3, lineEnd: Vec3): Double {
        val lineDir = lineEnd.subtract(lineStart)
        val lenSqr = lineDir.lengthSqr()
        if (lenSqr == 0.0) return point.distanceTo(lineStart)

        var t = point.subtract(lineStart).dot(lineDir) / lenSqr
        t = Mth.clamp(t, 0.0, 1.0)

        val projection = lineStart.add(lineDir.scale(t))
        return point.distanceTo(projection)
    }
}