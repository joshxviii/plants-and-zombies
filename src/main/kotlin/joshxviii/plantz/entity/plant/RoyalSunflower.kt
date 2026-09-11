package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.GenerateSunGoal
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class RoyalSunflower(
    type: EntityType<out Plant>,
    level: Level,
) : Plant(PazEntities.ROYAL_SUNFLOWER, level) {
    override fun sleepsDuringNight(): Boolean = false

    override fun clampToGrid(): Boolean = false
    override fun canSurviveOn(block: BlockState): Boolean = true

    override fun registerGoals() {
        super.registerGoals()
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && (target is Zombie
                    || (target is Enemy && isTame)
                    || (target is Player && !isTame))
        })
    }
}