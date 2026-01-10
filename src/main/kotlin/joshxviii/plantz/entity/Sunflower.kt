package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazLootTables
import joshxviii.plantz.ai.goal.GenerateSunGoal
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.gameevent.GameEvent

class Sunflower(
    type: EntityType<out Plant>,
    level: Level,
) : Plant(PazEntities.SUNFLOWER, level) {
    override fun attackGoals() {}

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, GenerateSunGoal(
            plantEntity = this,
            actionDelay = 10
        ))
    }
}