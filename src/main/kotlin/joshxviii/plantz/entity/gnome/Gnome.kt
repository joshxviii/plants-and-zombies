package joshxviii.plantz.entity.gnome

import PazDataSerializers.GNOME_SOUND_VARIANT
import PazDataSerializers.GNOME_VARIANT
import net.minecraft.core.BlockPos
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundEvent
import net.minecraft.util.RandomSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.block.state.BlockState

class Gnome(type: EntityType<out Gnome>, level: Level) : Mob(type, level) {

    companion object {
        fun checkGnomeSpawnRules(
            type: EntityType<out Mob>,
            level: ServerLevelAccessor,
            spawnReason: EntitySpawnReason,
            pos: BlockPos,
            random: RandomSource
        ): Boolean {
            return (EntitySpawnReason.ignoresLightRequirements(spawnReason))
                    && checkMobSpawnRules(type, level, spawnReason, pos, random)
                    && pos.y < level.seaLevel
        }

        val DATA_VARIANT_ID: EntityDataAccessor<GnomeVariant> = SynchedEntityData.defineId(Gnome::class.java, GNOME_VARIANT)
        val DATA_SOUND_VARIANT_ID: EntityDataAccessor<GnomeSoundVariant> = SynchedEntityData.defineId(Gnome::class.java, GNOME_SOUND_VARIANT)
    }

    var variant: GnomeVariant
        get() = this.entityData.get(DATA_VARIANT_ID)
        set(value) = this.entityData.set(DATA_VARIANT_ID, value)

    var soundVariant: GnomeSoundVariant
        get() = this.entityData.get(DATA_SOUND_VARIANT_ID)
        set(value) = this.entityData.set(DATA_SOUND_VARIANT_ID, value)

    val soundSet = soundVariant.getSoundSet()

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(
            DATA_VARIANT_ID,
            GnomeVariant.pickRandomVariant()
        )
        entityData.define(
            DATA_SOUND_VARIANT_ID,
            GnomeSoundVariant.pickRandomVariant()
        )
    }

    override fun getAmbientSound(): SoundEvent? {
        return soundSet.ambientSound.value()
    }

    override fun getHurtSound(source: DamageSource): SoundEvent? {
        return soundSet.hurtSound.value()
    }

    override fun getDeathSound(): SoundEvent? {
        return soundSet.deathSound.value()
    }

    override fun playStepSound(pos: BlockPos, blockState: BlockState) {
        this.playSound(soundSet.stepSound.value(), 0.15f, 1.0f)
    }
}