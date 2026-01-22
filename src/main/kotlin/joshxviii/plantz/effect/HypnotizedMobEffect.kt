package joshxviii.plantz.effect

import joshxviii.plantz.PazEffects.HYPNOTIZED_GOAL_ATTACHMENT
import joshxviii.plantz.PazEntities.PLANT_TEAM
import joshxviii.plantz.PazTags
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.mixin.MobAccessor
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal

/**
 *
 */
class HypnotizedMobEffect(category: MobEffectCategory, color: Int) : MobEffect(category, color) {
    companion object {
    }

    override fun onEffectStarted(effectInstance: MobEffectInstance, entity: LivingEntity) {
        super.onEffectStarted(effectInstance, entity)
        if (entity !is Plant) {
            val scoreboard = entity.level().server?.scoreboard?: return
            if (entity.team != PLANT_TEAM) scoreboard.addPlayerToTeam(entity.scoreboardName, PLANT_TEAM)

            if(entity is Mob) {
                val goal = NearestAttackableTargetGoal(entity, LivingEntity::class.java, true) { target, level ->
                    target.`is`(PazTags.EntityTypes.ZOMBIE_RAIDERS) }

                if (goal != null) (entity as MobAccessor).targetSelector.addGoal(0, goal)
                entity.setAttached(HYPNOTIZED_GOAL_ATTACHMENT,goal)
            }
        }
    }

    override fun onEffectRemoved(effectInstance: MobEffectInstance, entity: LivingEntity) {
        super.onEffectRemoved(effectInstance, entity)
        if (entity !is Plant) {
            val scoreboard = entity.level().server?.scoreboard?: return
            if (entity.team == PLANT_TEAM) scoreboard.removePlayerFromTeam(entity.scoreboardName, PLANT_TEAM)

            if(entity is Mob) {
                val goal = entity.getAttached<Goal>(HYPNOTIZED_GOAL_ATTACHMENT)
                if (goal != null) (entity as MobAccessor).targetSelector.removeGoal(goal)
            }
        }
    }
}