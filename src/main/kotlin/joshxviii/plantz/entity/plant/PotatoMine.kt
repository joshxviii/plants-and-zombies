package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazSounds
import joshxviii.plantz.PazTags
import joshxviii.plantz.hasSameRootOwner
import net.minecraft.core.Holder
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.SpawnGroupData
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState

class PotatoMine(type: EntityType<out ExplosivePlant>, level: Level) : ExplosivePlant(PazEntities.POTATO_MINE, level) {
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
    }

    override fun explode(
        radius: Float,
        sound: Holder.Reference<SoundEvent>,
        damageType: ResourceKey<DamageType>,
        destroyBlocks: Boolean,
        discardOnExplode: Boolean
    ) {
        super.explode(
            radius = 1f,
            sound = PazSounds.POTATOMINE_EXPLODE,
            damageType,
            destroyBlocks,
            discardOnExplode
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
    }

    override fun getMaxSwellTime() = 4
    override fun doPush(entity: Entity) {
        if (isGrowingSeeds || cooldown > 0) return
        if (entity is Plant || (entity is Player && isTame) || this.hasSameRootOwner(entity)) return
        explode()
    }

    override fun canSurviveOn(block: BlockState): Boolean {
        return super.canSurviveOn(block) || block.`is`(PazTags.BlockTags.SURVIVES_ON_POTATOMINE)
    }
}