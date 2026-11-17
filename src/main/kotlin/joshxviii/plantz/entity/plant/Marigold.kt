package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazDataSerializers.DATA_DYE_COLOR
import joshxviii.plantz.PazDataSerializers.DIGGER_VARIANT
import joshxviii.plantz.PazDataSerializers.MARIGOLD_COLORS
import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.GenerateSunGoal
import joshxviii.plantz.entity.zombie.DiggerVariant
import joshxviii.plantz.entity.zombie.DiggerZombie
import joshxviii.plantz.entity.zombie.SoldierZombie
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import kotlin.jvm.optionals.getOrDefault

class Marigold(type: EntityType<out Plant>, level: Level, ) : Plant(type, level) {
    companion object {
        const val REQUIRED_SUN = 32
        val MARIGOLD_COLOR: EntityDataAccessor<MarigoldColor> = SynchedEntityData.defineId(Marigold::class.java, MARIGOLD_COLORS)
    }

    var color: MarigoldColor
        get() = entityData.get(MARIGOLD_COLOR)
        set(value) = entityData.set(MARIGOLD_COLOR, value)

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(MARIGOLD_COLOR, MarigoldColor.getDefault())
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.store("color", MarigoldColor.CODEC, color)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        color = input.read("color", MarigoldColor.CODEC).getOrDefault(MarigoldColor.getDefault())
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