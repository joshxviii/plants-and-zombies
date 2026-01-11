package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.entity.Plant
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

class BeamAttackPlantGoal(
    plantEntity: Plant,
    cooldownTime: Int = 20,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    val beamRange : Double = 10.0,
    val beamWidth : Double = 3.25,
    val damageType: ResourceKey<DamageType> = PazDamageTypes.PLANT,
    val beamParticles : ParticleOptions? = null
) : PlantActionGoal(plantEntity, cooldownTime, actionDelay, actionStartEffect, actionEndEffect) {
    private var piercedEntities: MutableList<Entity>? = null

    override fun canUse(): Boolean = (
        plantEntity.tickCount>cooldownTime
        && plantEntity.target?.isAlive == true
    )

    override fun canDoAction(): Boolean {
        val target = plantEntity.target?: return false
        plantEntity.lookControl.setLookAt(target, 30f, 30f)

        return isInReach(target);
    }

    override fun doAction(): Boolean {
        val target = plantEntity.target ?: return false
        if (!target.isAlive) return false

        piercedEntities = mutableListOf()

        val start = plantEntity.position().add(0.0, plantEntity.eyeHeight.toDouble(), 0.0)

        val direction = target.eyePosition.subtract(start).normalize()

        val end = start.add(direction.scale(beamRange))

        val beamAABB = AABB(start, end).inflate(beamWidth / 2.0 + 1.0)

        val candidates = plantEntity.level().getEntities(plantEntity, beamAABB) { entity ->
            entity is LivingEntity &&
                    entity is Enemy &&
                    entity != plantEntity &&
                    entity.isAlive
        }

        for (entity in candidates) {
            val distToRay = distanceToLineSegment(entity.eyePosition, start, end)
            val entityRadiusApprox = entity.boundingBox.getSize() / 2.0
            if (distToRay <= (beamWidth / 2.0 + entityRadiusApprox)) {

                val damage : Float = plantEntity.attributes.getValue(Attributes.ATTACK_DAMAGE).toFloat()
                val source = plantEntity.damageSources().source(damageType, plantEntity)

                entity.hurtServer(plantEntity.level() as ServerLevel, source, damage)
                piercedEntities?.add(entity)
            }
        }

        spawnBeamParticles(start, end)
        return piercedEntities?.isNotEmpty() ?: false
    }

    private fun isInReach(target: LivingEntity): Boolean {
        return plantEntity.boundingBox.inflate(beamRange).intersects(target.boundingBox) &&
                plantEntity.sensing.hasLineOfSight(target)
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

             (plantEntity.level() as ServerLevel).sendParticles(
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