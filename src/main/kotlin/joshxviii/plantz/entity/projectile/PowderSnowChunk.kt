package joshxviii.plantz.entity.projectile

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.gamerules.GameRules
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult

class PowderSnowChunk(
    val level: Level,
    val mob: Mob
) : ThrowableItemProjectile(EntityType.SNOWBALL, mob, level, Items.AIR.defaultInstance) {
    private val block : FallingBlockEntity = FallingBlockEntity.fall(this.level(), this.blockPosition().above(), Blocks.POWDER_SNOW.defaultBlockState())

    init {
        this.setOwner(block)
        if((level as ServerLevel).gameRules.get(GameRules.MOB_GRIEFING)==false) block.disableDrop()
    }

    override fun baseTick() {
        super.baseTick()
        if(tickCount<=1) {
            block.deltaMovement = deltaMovement
            block.needsSync = true
            block.startRiding(this)
        }
    }

    override fun tick() {

        super.tick()
        if(tickCount % 2 == 0)
        if (level is ServerLevel) {
            level.sendParticles(
                ParticleTypes.SNOWFLAKE,
                block.x, block.y+0.5, block.z,
                3,
                0.1, 0.1, 0.1,
                0.02
            )
        }
        if (block.isRemoved || !block.isAlive) {
            explode()
        }
    }

    override fun getDefaultItem(): Item = Items.AIR

    override fun onHitEntity(hitResult: EntityHitResult) {
        super.onHitEntity(hitResult)
        val entity = hitResult.entity
        val damage = 3
        entity.hurtServer(level() as ServerLevel, this.damageSources().thrown(this, this.getOwner()), damage.toFloat())
    }

    override fun onHit(hitResult: HitResult) {
        super.onHit(hitResult)
        if (!level().isClientSide) {
            level().broadcastEntityEvent(this, 3.toByte())
            explode()
        }
    }

    private fun explode() {
        if (level is ServerLevel) {
            level.sendParticles(
                ParticleTypes.SNOWFLAKE,
                block.x, block.y, block.z,
                80,
                0.13, 0.13, 0.13,
                0.1
            )
        }
        discard()
    }
}