package joshxviii.plantz.block.entity

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazEffects
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block.getId
import net.minecraft.world.level.block.LevelEvent
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.AABB
import kotlin.math.floor

class FlagBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState
) : BlockEntity(PazBlocks.FLAG_BLOCK_ENTITY, worldPosition, blockState) {

    companion object {
        const val MAX_HEALTH: Float = 400f
        const val HEAL_COOLDOWN_TIME = 300
        const val HEAL_TIME = 20

        const val MAX_FLAG_OMEN_DISTANCE = 24.0
    }

    var health : Float = MAX_HEALTH
        set(value) {
            field = value.coerceAtMost(MAX_HEALTH)
        }
    var resetTime : Int = 0

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        if (level.isClientSide) return
        if (resetTime > 0) {
            resetTime--
            if (resetTime == 0) {
                health += MAX_HEALTH * 0.01f

                level.destroyBlockProgress(0, pos, healthToDestroyProgress())
                if (health >= MAX_HEALTH) level.destroyBlockProgress(0, pos, -1)
                else resetTime = HEAL_TIME
                syncToClient()
            }
        }

        if (health <= 0) {
            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, getId(state))
            if (blockState.`is`(PazBlocks.PLANTZ_FLAG)) {
                val item = ItemEntity(level, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), PazBlocks.BRAINZ_FLAG.asItem().defaultInstance)
                level.addFreshEntity(item)
            }
            level.destroyBlock(pos, false)
            level.destroyBlockProgress(0, pos, -1)
        } else if (health < MAX_HEALTH) {
            level.destroyBlockProgress(0, pos, healthToDestroyProgress())
        }

        if (blockState.`is`(PazBlocks.PLANTZ_FLAG)) replaceBadOmenEffect()
    }

    fun replaceBadOmenEffect(distance: Double = MAX_FLAG_OMEN_DISTANCE) {
        val players = level?.getEntitiesOfClass(
            Player::class.java,
            AABB(blockPos).inflate(distance)
        ) { p -> p.hasEffect(MobEffects.BAD_OMEN) }

        players?.forEach { player ->
            val amplification = player.getEffect(MobEffects.BAD_OMEN)?.amplifier ?: 0
            player.removeEffect(MobEffects.BAD_OMEN)
            val effectInstance = MobEffectInstance(PazEffects.ZOMBIE_OMEN, 600, amplification)
            player.addEffect(effectInstance)
        }
    }

    fun hurt(amount: Float) {
        health -= amount
        health = health.coerceAtLeast(0f)
        resetTime = HEAL_COOLDOWN_TIME
        setChanged()
        syncToClient()
        val l = level?: return
        l.playSound(null, blockPos, blockState.soundType.hitSound, SoundSource.BLOCKS, 1.0f, l.random.nextFloat() * 0.2f + 0.6f)
    }

    private fun healthToDestroyProgress(): Int = floor((1f - (health / MAX_HEALTH)) * 10f).toInt()

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.putFloat("Health", health)
        output.putInt("ResetTime", resetTime)
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        health = input.getFloatOr("Health", MAX_HEALTH)
        resetTime = input.getIntOr("ResetTime", 0)
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return saveWithoutMetadata(registries)
    }

    private fun syncToClient() {
        val lvl = level ?: return
        lvl.sendBlockUpdated(blockPos, blockState, blockState, 3)
    }
}
