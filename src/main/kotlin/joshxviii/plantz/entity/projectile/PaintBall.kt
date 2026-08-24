package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PaintParticleOptions
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazEntities
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.sheep.Sheep
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class PaintBall(
    level: Level,
    owner: LivingEntity? = null,
    spawnOffset: Vec2 = Vec2.ZERO,
    color: DyeColor = DyeColor.WHITE,
    damage: Float = 0.5f,
) : PazProjectile(PazEntities.PAINT_BALL, level, owner, spawnOffset,
    PazDamageTypes.PAINT, damage, knockback = 0.01
) {
    companion object {
        val COLOR: EntityDataAccessor<Int> = SynchedEntityData.defineId(PaintBall::class.java, EntityDataSerializers.INT)
    }

    var dyeColor: DyeColor
        get() = DyeColor.byId(entityData.get(COLOR))
        set(value) = entityData.set(COLOR, value.id)

    init {
        dyeColor = color
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(COLOR,  DyeColor.WHITE.id)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.store("dyeColor", DyeColor.CODEC, dyeColor)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        input.read("dyeColor", DyeColor.CODEC).ifPresent { dyeColor -> this.dyeColor = dyeColor }
    }

    override fun tick() {
        super.tick()
        spawnParticle(
            PaintParticleOptions(dyeColor.fireworkColor, 0.95f),
            spread = Vec3(0.01,0.01,0.01),
            speed = 0.015
        )
    }

    override fun getDefaultGravity(): Double { return 0.04 }

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(
            PaintParticleOptions(dyeColor.fireworkColor, 1.95f),
            amount = 18,
            speed = 0.25
        )
    }

    override fun afterHitEntityEffect(target: LivingEntity) {
        super.afterHitEntityEffect(target)
        val p = PazEffects.PAINTED[dyeColor] ?: return
        val oldEffectInstance = target.getEffect(p)
        if (oldEffectInstance != null) {
            val effectInstance = MobEffectInstance(p, 220, oldEffectInstance.amplifier+1, false, true, false)
            target.addEffect(effectInstance)
        }
        else {
            val effectInstance = PazEffects.PAINTED[dyeColor]?.let { MobEffectInstance(it, 220, 0, false, true, false) } ?: return
            target.addEffect(effectInstance)
        }
        if (target is Sheep && target.hurtMarked) target.color = dyeColor
    }
}