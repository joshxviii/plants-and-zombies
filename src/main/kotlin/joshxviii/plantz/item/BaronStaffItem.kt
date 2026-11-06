package joshxviii.plantz.item

import joshxviii.plantz.ElectricArcParticleOptions
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazSounds
import joshxviii.plantz.PlasmaBurstOptions
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Mob
import net.minecraft.server.level.ServerLevel
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
        const val USE_DURATION = 8
        private const val BEAM_RANGE = 14.0
        private const val BEAM_WIDTH = 0.1
        private const val BEAM_DAMAGE = 7.0f
        private const val BEAM_COLOR = 0xaf5dfa
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        player.startUsingItem(hand)
        return InteractionResult.CONSUME
    }

    override fun onUseTick(level: Level, livingEntity: LivingEntity, itemStack: ItemStack, ticksRemaining: Int) {
        if (ticksRemaining==USE_DURATION)
            level.playSound(livingEntity, livingEntity.blockPosition(), PazSounds.ELECTRIC_ZAP, livingEntity.soundSource, 0.3f, 2.0f)
        if (ticksRemaining==1) shootBeam(level, livingEntity, itemStack, BEAM_DAMAGE)
    }

    fun shootBeam(livingEntity: LivingEntity, damage: Float) {
        shootBeam(livingEntity.level(), livingEntity, null, damage)
    }

    private fun shootBeam(
        level: Level,
        livingEntity: LivingEntity,
        itemStack: ItemStack?,
        damage: Float
    ) {
        level.playSound(livingEntity, livingEntity.blockPosition(), PazSounds.SUPER_BRAINZ_LAZER, livingEntity.soundSource, 0.6f, 1.2f)
        val serverLevel = level as? ServerLevel ?: return
        val startPos = livingEntity.eyePosition.add(livingEntity.lookAngle.subtract(Vec3.Y_AXIS).scale(0.05))
        val maxEndPos = startPos.add(livingEntity.headLookAngle.scale(BEAM_RANGE))
        val blockHit = level.clip(
            ClipContext(startPos, maxEndPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingEntity)
        )
        val endPos = if (blockHit.type == HitResult.Type.MISS) maxEndPos else blockHit.location
        val beamBounds = AABB(startPos, endPos).inflate(BEAM_WIDTH+0.5 / 2.0)

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

        val damageSource = livingEntity.damageSources().source(PazDamageTypes.ENERGY, livingEntity, livingEntity)
        val entities = level.getEntities(livingEntity, beamBounds) { entity ->
            entity.isAlive && entity.boundingBox.inflate(BEAM_WIDTH+0.5 / 2.0)
                .clip(startPos, endPos).isPresent &&
                (livingEntity !is Mob ||
                    entity !is LivingEntity || livingEntity.canAttack(entity))
        }.sortedBy { it.distanceToSqr(startPos) }

        for (entity: Entity in entities) {
            entity.hurtServer(serverLevel, damageSource, damage)
        }

        itemStack?.hurtAndBreak(1, livingEntity, livingEntity.usedItemHand)
    }

    override fun getUseDuration(itemStack: ItemStack, user: LivingEntity): Int {
        return USE_DURATION
    }

    override fun getUseAnimation(itemStack: ItemStack): ItemUseAnimation {
        return ItemUseAnimation.SPEAR
    }
}