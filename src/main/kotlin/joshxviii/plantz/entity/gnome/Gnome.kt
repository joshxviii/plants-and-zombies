package joshxviii.plantz.entity.gnome

import PazDataSerializers.GNOME_SOUND_VARIANT
import PazDataSerializers.GNOME_VARIANT
import joshxviii.plantz.PazEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.animal.wolf.*
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.variant.VariantUtils
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import java.util.function.Supplier

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

        val DATA_VARIANT_ID: EntityDataAccessor<Holder<GnomeVariant>> = SynchedEntityData.defineId(Gnome::class.java, GNOME_VARIANT)
        val DATA_SOUND_VARIANT_ID: EntityDataAccessor<Holder<GnomeSoundVariant>> = SynchedEntityData.defineId(Gnome::class.java, GNOME_SOUND_VARIANT)
    }

    fun getTexture(): Identifier {
        val variant: GnomeVariant = variant.value()
        val assetInfo = variant.modelAndTexture.asset()
        return assetInfo.texturePath
    }

    var variant: Holder<GnomeVariant>
        get() = this.entityData.get(DATA_VARIANT_ID)
        set(value) = this.entityData.set(DATA_VARIANT_ID, value)

    var soundVariant: Holder<GnomeSoundVariant>
        get() = this.entityData.get(DATA_SOUND_VARIANT_ID)
        set(value) = this.entityData.set(DATA_SOUND_VARIANT_ID, value)

    private fun getSoundSet(): GnomeSoundVariant.GnomeSoundSet {
        return soundVariant.value().sounds
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(
            DATA_VARIANT_ID,
            GnomeVariants.pickRandomVariant(this.registryAccess(), random)
        )
        entityData.define(
            DATA_SOUND_VARIANT_ID,
            GnomeSoundVariants.pickRandomSoundVariant(this.registryAccess(), random)
        )
    }

    override fun getAmbientSound(): SoundEvent? {
        return super.getAmbientSound()// TODO make custom sounds
    }

    override fun getHurtSound(source: DamageSource): SoundEvent? {
        if (source.entity is Zombie) return SoundEvents.PLAYER_BURP
        return SoundEvents.ROOTED_DIRT_HIT// TODO make custom sounds
    }

    override fun getDeathSound(): SoundEvent? {
        return SoundEvents.ROOTED_DIRT_BREAK// TODO make custom sounds
    }

}