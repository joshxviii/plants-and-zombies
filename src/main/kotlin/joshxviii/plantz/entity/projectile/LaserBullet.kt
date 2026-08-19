package joshxviii.plantz.entity.projectile

import joshxviii.plantz.PaintParticleOptions
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2

class LaserBullet(
    level: Level,
    owner: LivingEntity? = null,
    spawnOffset: Vec2 = Vec2.ZERO,
    color: Int = DEFAULT_COLOR,
    damage: Float = 0.5f,
) : PazProjectile(PazEntities.LASER_BULLET, level, owner, spawnOffset,
    PazDamageTypes.ENERGY, damage, knockback = 0.02
) {
    companion object {
        const val DEFAULT_COLOR: Int = 0xFF99EE
        val COLOR: EntityDataAccessor<Int> = SynchedEntityData.defineId(LaserBullet::class.java, EntityDataSerializers.INT)
    }

    var laserColor: Int
        get() = this.entityData.get(COLOR)
        set(value) = this.entityData.set(COLOR, value)

    init {
        laserColor = color
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(COLOR, DEFAULT_COLOR)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.putInt("laserColor", laserColor)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        laserColor = input.getIntOr("laserColor", DEFAULT_COLOR)
    }

    override fun tick() {
        super.tick()
    }

    override fun lifeTime(): Int = 50
    override fun getDefaultGravity(): Double { return 0.0 }

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        spawnParticle(
            PaintParticleOptions(laserColor, 1.95f),
            amount = 18,
            speed = 0.25
        )
    }

    override fun afterHitEntityEffect(target: LivingEntity) {
        super.afterHitEntityEffect(target)
    }
}