package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
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
    actionEndEffect: () -> Unit = {},
    actionPredicate: Predicate<PathfinderMob> = Predicate { true },
    val beamRange : Double = 10.0,
    val beamWidth : Double = 3.25,
    val damageType: ResourceKey<DamageType> = PazDamageTypes.PLANT,
    val beamParticles : ParticleOptions? = null,
    val afterHitEntityEffect: (target: LivingEntity) -> Unit = {},
) : ActionGoal(usingEntity, cooldownTime, actionDelay, actionStartEffect, actionEndEffect, actionPredicate) {
    private var piercedEntities: MutableList<Entity>? = null

    override fun canUse(): Boolean = (
        usingEntity.tickCount>cooldownTime
            && usingEntity.target?.isAlive == true
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

        val direction = target.eyePosition.subtract(start).normalize()

        val end = start.add(direction.scale(beamRange))

        val beamAABB = AABB(start, end).inflate(beamWidth / 2.0 + 1.0)

        val candidates = usingEntity.level().getEntities(usingEntity, beamAABB) { entity ->
            entity is LivingEntity && entity !is Plant && entity.isAlive
        }

        for (entity in candidates) {
            if (!usingEntity.canAttack(entity as LivingEntity)) break
            val distToRay = distanceToLineSegment(entity.eyePosition, start, end)
            val entityRadiusApprox = entity.boundingBox.size / 2.0
            if (distToRay <= (beamWidth / 2.0 + entityRadiusApprox)) {

                val damage : Float = usingEntity.attributes.getValue(Attributes.ATTACK_DAMAGE).toFloat()
                val knockback : Double = usingEntity.attributes.getValue(Attributes.ATTACK_KNOCKBACK)
                val source = usingEntity.damageSources().source(damageType, usingEntity)

                if (entity.hurtServer(usingEntity.level() as ServerLevel, source, damage)) {
                    afterHitEntityEffect(entity)
                    entity.knockback(
                        knockback,
                        start.x - entity.x,
                        start.z - entity.z
                    )
                }
                piercedEntities?.add(entity)
            }
        }

        spawnBeamParticles(start, end)
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

     private fun spawnBeamParticles(start: Vec3, end: Vec3) {
         if (beamParticles == null) return
         val steps = (start.distanceTo(end) * 10).toInt()  // Density
         val stepVec = end.subtract(start).scale(1.0 / steps)
         var pos = start
         for (i in 0 until steps) {

             (usingEntity.level() as ServerLevel).sendParticles(
                 beamParticles,
                 pos.x, pos.y, pos.z,
                 2,
                 beamWidth/8, beamWidth/8, beamWidth/8,
                 0.02
             )
             pos = pos.add(stepVec)
         }
     }
}