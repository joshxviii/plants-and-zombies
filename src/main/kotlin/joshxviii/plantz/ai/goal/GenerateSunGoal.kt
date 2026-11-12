package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazSounds
import joshxviii.plantz.entity.Sun
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.level.block.entity.BeaconBlockEntity.playSound
import java.util.function.Predicate

class GenerateSunGoal(
    usingEntity: Plant,
    cooldownTime: Int = 900,
    actionDelay: Int = 0,
    actionStartEffect: () -> Unit = {},
    actionSuccessEffect: () -> Unit = {},
    actionEndEffect: () -> Unit = {},
    actionPredicate: Predicate<PathfinderMob> = Predicate { true },
    val sunAmount: Int = 1,
    val generatesAtNight : Boolean = false,
): ActionGoal(usingEntity, cooldownTime, actionDelay, actionStartEffect, actionSuccessEffect, actionEndEffect, actionPredicate,
    cooldownVariationRange = -10..20) {

    override fun tick() {
        super.tick()
        if (usingEntity !is Plant) return
        if (usingEntity.cooldown < 100 && usingEntity.tickCount % 20 == 0) {
            if (usingEntity.random.nextFloat() < 0.3f) usingEntity.playSound(PazSounds.PLANT_GLOW, 0.2f, 1.8f)
        }
    }

    override fun canUse(): Boolean = (
        usingEntity.isAlive
            && !(usingEntity is Plant && (usingEntity.isAsleep || usingEntity.isGrowingSeeds))
    )

    override fun canDoAction(): Boolean = (generatesAtNight || (usingEntity as? Plant)?.sunIsVisible() == true)

    override fun doAction() : Boolean {
        val serverLevel = usingEntity.level() as? ServerLevel ?: return false
        Sun.award(serverLevel, usingEntity.position(), if (usingEntity.isBaby) (sunAmount/2).coerceAtLeast(1) else sunAmount )
        usingEntity.playSound(PazSounds.PLANT_GENERATE_SUN, 0.3f, 1.0f)
        return true
    }
}