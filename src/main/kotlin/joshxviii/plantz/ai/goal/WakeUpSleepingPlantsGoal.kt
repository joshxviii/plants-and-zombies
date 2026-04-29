package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.plant.Plant
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.phys.AABB
import java.util.EnumSet
import java.util.function.Predicate

class WakeUpSleepingPlantsGoal(
    usingEntity: Plant,
    cooldownTime: Int = 20,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    actionPredicate: Predicate<PathfinderMob> = Predicate { true },
    val maxPlants: Int = 1,
): ActionGoal(usingEntity, cooldownTime, actionDelay, actionStartEffect, actionEndEffect, actionPredicate, -10..20) {
    var targets: List<Plant> = listOf()
    val targetConditions: TargetingConditions
    val followDistance = usingEntity.getAttribute(Attributes.FOLLOW_RANGE)?.value?: 1.0

    init {
        this.setFlags(EnumSet.of<Flag>(Flag.TARGET))
        this.targetConditions = TargetingConditions.forNonCombat().range(followDistance)
    }

    override fun canUse(): Boolean {
        return (usingEntity.tickCount > cooldownTime
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
        for (plant in targets) plant.applyCoffeeBuff()
        return true
    }
}