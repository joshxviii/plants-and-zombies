package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazSounds
import joshxviii.plantz.hasSameRootOwner
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.SpawnGroupData
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

class PotatoMine(type: EntityType<out Explosive>, level: Level) : Explosive(PazEntities.POTATO_MINE, level) {
    override fun registerGoals() {
        super.registerGoals()
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        cooldown = 190 + random.nextInt(-20, 20)
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData)
    }

    override fun tick() {
        super.tick()
        if (cooldown>0) coolDownAnimationState.startIfStopped(tickCount)
        if (swell == getMaxSwellTime()) potatoMineExplode()
    }

    fun potatoMineExplode() {
        explode(
            radius = 1f,
            sound = PazSounds.POTATOMINE_EXPLODE,
        )
        addParticlesAroundSelf(
            particle = ItemParticleOption(
                ParticleTypes.ITEM,
                Items.POTATO
            ),
            amount = 22..24,
            speed = 0.2,
        )
        addParticlesAroundSelf(
            particle = ParticleTypes.LARGE_SMOKE,
            amount = 3..3,
            speed = 0.1,
        )
        discard()
    }

    override fun getMaxSwellTime() = 4
    override fun doPush(entity: Entity) {
        if (isGrowingSeeds || cooldown > 0) return
        if (entity is Plant || (entity is Player && isTame) || this.hasSameRootOwner(entity)) return
        potatoMineExplode()
    }
}