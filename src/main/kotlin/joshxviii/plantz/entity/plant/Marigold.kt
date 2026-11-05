package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.GenerateSunGoal
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class Marigold(type: EntityType<out Plant>, level: Level, ) : Plant(type, level) {
    companion object {
        const val REQUIRED_SUN = 32
    }

    override fun attackGoals() {}
    override fun registerGoals() {
        super.registerGoals()
    }

    override fun tick() {
        super.tick()
        if (isGrowingSeeds && tickCount % 25 == 0) {
            val progress = receivedSun / REQUIRED_SUN.toFloat()
            val amount = Mth.floor(progress * 10)
            if (amount > 0) addParticlesAroundSelf(
                particle = ParticleTypes.FIREWORK,
                amount = amount-1..amount+1,
                horizontalSpreadScale = 0.1,
                height = 0f,
                speed = 0.05
            )
        }
    }
    override fun sunRequiredForSeeds(): Int = REQUIRED_SUN
}