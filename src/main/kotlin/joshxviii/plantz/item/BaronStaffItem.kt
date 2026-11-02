package joshxviii.plantz.item

import joshxviii.plantz.BeamParticleOptions
import joshxviii.plantz.ElectricArcParticleOptions
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazSounds
import joshxviii.plantz.PlasmaBurstOptions
import net.minecraft.world.entity.Entity
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3

class BaronStaffItem(properties: Properties) : Item(properties) {
    companion object {
        private const val BEAM_RANGE = 14.0
        private const val BEAM_WIDTH = 0.1
        private const val BEAM_DAMAGE = 8.0f
        private const val BEAM_COLOR = 0xaf5dfa
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        player.startUsingItem(hand)
        return InteractionResult.CONSUME
    }

    override fun onUseTick(level: Level, livingEntity: LivingEntity, itemStack: ItemStack, ticksRemaining: Int) {
        if ((livingEntity as? Player)?.cooldowns?.isOnCooldown(itemStack) == true) return
        (livingEntity as Player).cooldowns.addCooldown(itemStack, 20)
        shootBeam(level, livingEntity, itemStack)
    }

    private fun shootBeam(level: Level, livingEntity: LivingEntity, itemStack: ItemStack) {
        level.playSound(livingEntity, livingEntity.blockPosition(), PazSounds.SUPER_BRAINZ_LAZER, SoundSource.NEUTRAL, 0.9f, 1.2f)
        val serverLevel = level as? ServerLevel ?: return
        val startPos = livingEntity.eyePosition.add(livingEntity.lookAngle.subtract(Vec3.Y_AXIS).scale(0.05))
        val maxEndPos = startPos.add(livingEntity.lookAngle.scale(BEAM_RANGE))
        val blockHit = level.clip(
            ClipContext(startPos, maxEndPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingEntity)
        )
        val endPos = if (blockHit.type == HitResult.Type.MISS) maxEndPos else blockHit.location
        val beamBounds = AABB(startPos, endPos).inflate(BEAM_WIDTH / 2.0)

        serverLevel.sendParticles(
            ElectricArcParticleOptions(
                endPos.offsetRandom(livingEntity.random, 0.02f),
                color = BEAM_COLOR,
                width = BEAM_WIDTH.toFloat(),
            ),
            startPos.x, startPos.y, startPos.z,
            1, 0.0, 0.0, 0.0, 0.0
        )

        serverLevel.sendParticles(
            PlasmaBurstOptions(color = BEAM_COLOR, scale = 1.2f), endPos.x, endPos.y, endPos.z,
            2, 0.0, 0.0, 0.0, 0.0
        )

        val damageSource = livingEntity.damageSources().source(PazDamageTypes.ENERGY, livingEntity)
        val entities = level.getEntities(livingEntity, beamBounds) { entity ->
            entity.isAlive && entity.boundingBox.inflate(BEAM_WIDTH / 2.0)
                .clip(startPos, endPos).isPresent
        }.sortedBy { it.distanceToSqr(startPos) }

        var hitEntity = false
        for (entity: Entity in entities) {
            if (entity.hurtServer(serverLevel, damageSource, BEAM_DAMAGE)) hitEntity = true
        }

        itemStack.hurtAndBreak(1, livingEntity, livingEntity.usedItemHand)
    }

    override fun getUseDuration(itemStack: ItemStack, user: LivingEntity): Int {
        return 6
    }

    override fun getUseAnimation(itemStack: ItemStack): ItemUseAnimation {
        return ItemUseAnimation.SPEAR
    }
}