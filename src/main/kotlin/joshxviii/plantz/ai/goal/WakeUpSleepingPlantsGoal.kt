package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.plant.Plant
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.phys.AABB
import java.util.EnumSet

class WakeUpSleepingPlantsGoal(
    plantEntity: Plant,
    val maxPlants: Int = 1,
) : ActionGoal(plantEntity)
{
    var targets: List<Plant> = listOf()
    val targetConditions: TargetingConditions
    val followDistance = usingEntity.getAttribute(Attributes.FOLLOW_RANGE)?.value?: 1.0

    init {
        this.setFlags(EnumSet.of<Flag>(Flag.TARGET))
        this.targetConditions = TargetingConditions.forNonCombat().range(followDistance)
    }

    override fun canUse(): Boolean {
        return if (usingEntity.getRandom().nextInt(5) != 0) false
        else (usingEntity.tickCount > cooldownTime
                && usingEntity.isAlive
                && !(usingEntity is Plant && (usingEntity.isAsleep || usingEntity.isGrowingSeeds)))
    }

    override fun canDoAction(): Boolean {
        return findTargets().isNotEmpty()
    }

    fun getPlantSearchArea(followDistance: Double): AABB {
        return usingEntity.boundingBox.inflate(followDistance, followDistance+1, followDistance)
    }

    fun findTargets(): List<Plant> {
        val level = usingEntity.level() as? ServerLevel ?: return listOf()
        targets = level.getNearbyEntities(
            Plant::class.java,
            targetConditions,
            usingEntity,
            getPlantSearchArea(followDistance)
        )
            .filter { it.isAsleep && !it.isGrowingSeeds }
            .sortedBy { it.distanceToSqr(usingEntity) }
            .take(maxPlants)
        return targets
    }

    override fun doAction(): Boolean {
        for (plant in targets) {
            plant.applyCoffeeBuff()
        }
        usingEntity.discard()
        return true
    }
}