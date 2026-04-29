package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.ActionGoal
import joshxviii.plantz.ai.goal.ExplodeGoal
import joshxviii.plantz.ai.goal.FurthestAttackableTargetGoal
import joshxviii.plantz.ai.goal.MeleeAttackActionGoal
import joshxviii.plantz.ai.goal.NearestPlantTargetGoal
import joshxviii.plantz.ai.goal.WakeUpSleepingPlantsGoal
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import java.util.EnumSet
import java.util.function.Predicate

class CoffeeBean(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.COFFEE_BEAN, level) {
    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, WakeUpSleepingPlantsGoal(this))
    }
    override fun attackGoals() {}
}