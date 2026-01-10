package joshxviii.plantz.entity

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.ai.goal.GenerateSunGoal
import joshxviii.plantz.ai.goal.MeleePlantAttackGoal
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.level.Level

class Chomper(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.CHOMPER, level) {

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, MeleePlantAttackGoal(
            plantEntity = this,
            attackReach = 2.5,
            cooldownTime = 60,
            actionDelay = 10,
            actionStartEffect = {
                this.playSound(SoundEvents.EVOKER_FANGS_ATTACK)
            }
        ))
    }
}