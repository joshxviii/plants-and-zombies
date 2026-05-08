package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazDataSerializers.DATA_SWELL_DIR
import joshxviii.plantz.PazSounds
import net.minecraft.core.Holder
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.util.random.WeightedList
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.ExplosionDamageCalculator
import net.minecraft.world.level.Level
import net.minecraft.world.level.SimpleExplosionDamageCalculator
import java.util.Optional

abstract class Explosive(type: EntityType<out Explosive>, level: Level) : Plant(type, level) {

    companion object {
        val EXPLOSION_CALCULATOR: ExplosionDamageCalculator = SimpleExplosionDamageCalculator(false, true, Optional.of<Float>(1f), Optional.ofNullable(null))
        val DESTRUCTIVE_EXPLOSION_CALCULATOR: ExplosionDamageCalculator = SimpleExplosionDamageCalculator(true, false, Optional.of<Float>(1.5f), Optional.ofNullable(null))

        val SWELL_DIR: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(Explosive::class.java, DATA_SWELL_DIR)
    }

    var swellDir: Int
        get() = this.entityData.get(SWELL_DIR)
        set(value) {
            this.entityData.set(SWELL_DIR, value)
        }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(SWELL_DIR, 0)
    }

    override fun tick() {
        super.tick()
        calculateSwell()
    }

    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        val itemStack = player.getItemInHand(hand)
        val level = level()
        if (level is ServerLevel) {
            // flint and steel interaction
            if (itemStack.`is`(Items.FLINT_AND_STEEL)) {
                if (cooldown<0 && !isAsleep) {
                    swellDir=2
                    playSound(SoundEvents.FLINTANDSTEEL_USE)
                    return InteractionResult.SUCCESS_SERVER
                } else return InteractionResult.FAIL
            }
        }
        return super.mobInteract(player, hand)
    }

    open fun getMaxSwellTime() : Int = 30
    var oldSwell = 0; var swell = 0
    fun getSwelling(a: Float): Float = Mth.lerp(a, oldSwell.toFloat(), swell.toFloat()) / (getMaxSwellTime() - 2).toFloat()

    fun calculateSwell() {
        oldSwell = swell
        swell = (swell + swellDir.coerceIn(-1,1)).coerceIn(0, getMaxSwellTime())
    }

    fun explode(
        radius: Float = 4.0f,
        sound: Holder.Reference<SoundEvent> = PazSounds.PLANT_EXPLODE,
        damageType: ResourceKey<DamageType> = PazDamageTypes.PLANT_AOE,
        destroyBlocks: Boolean = false,
    ) {
        val level = this.level()
        val source = this.damageSources().source(damageType, this,
            if (PazConfig.PLAYER_CREDIT_FOR_PLANT_KILLS) this.rootOwner else this)
        level.explode(
            this,
            source,
            EXPLOSION_CALCULATOR,
            x, y, z,
            radius,
            false,
            Level.ExplosionInteraction.MOB,
            ParticleTypes.SMOKE,
            ParticleTypes.EXPLOSION,
            WeightedList.of(),
            sound
        )
        if (destroyBlocks) level.explode(
            this,
            null,
            DESTRUCTIVE_EXPLOSION_CALCULATOR,
            x, y, z,
            radius*.5f,
            false,
            Level.ExplosionInteraction.MOB,
            ParticleTypes.SMOKE,
            ParticleTypes.EXPLOSION,
            WeightedList.of(),
            SoundEvents.ITEM_BREAK
        )
    }

}