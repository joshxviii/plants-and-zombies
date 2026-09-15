package joshxviii.plantz.item

import joshxviii.plantz.entity.zombie.AllStar.Companion.CHARGE_BOOST_ID
import net.minecraft.ChatFormatting
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.sqrt

class FootballHelmetItem(properties: Properties) : Item(properties) {
    companion object {
        fun addToTooltip(consumer: MutableList<Component>, sprintKey: Component) {
            consumer.add(1, Component.translatable("item.plantz.football_helmet.description", sprintKey)
                .withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC))
        }

        private const val DAMAGE_INTERVAL = 35
        private const val KNOCKBACK_STRENGTH = 0.8
    }

    private fun isWearingFootballHelmet(entity: LivingEntity): Boolean {
        return entity.getItemBySlot(EquipmentSlot.HEAD).item is FootballHelmetItem
    }

    fun removeModifiers(entity: LivingEntity?) {
        if (entity == null) return
        entity.getAttribute(Attributes.MOVEMENT_SPEED)?.removeModifier(CHARGE_BOOST_ID)
        entity.getAttribute(Attributes.STEP_HEIGHT)?.removeModifier(CHARGE_BOOST_ID)
    }

    override fun inventoryTick(itemStack: ItemStack, level: ServerLevel, owner: Entity, slot: EquipmentSlot?) {
        super.inventoryTick(itemStack, level, owner, slot)
        if (level.isClientSide || owner !is LivingEntity) return
        if (!isWearingFootballHelmet(owner)) {
            removeModifiers(owner)
            return
        }
        if (!owner.isSprinting || !owner.onGround() || owner.isCrouching) {
            removeModifiers(owner)
            return
        }
        else if (owner.getAttribute(Attributes.MOVEMENT_SPEED)?.getModifier(CHARGE_BOOST_ID) == null) {
            level.playSound(null, owner.blockPosition(), SoundEvents.WIND_CHARGE_BURST.value(), SoundSource.PLAYERS, 0.25f, 1.3f)
            owner.getAttribute(Attributes.MOVEMENT_SPEED)?.addTransientModifier(
                AttributeModifier(CHARGE_BOOST_ID, 0.025, AttributeModifier.Operation.ADD_VALUE)
            )
            owner.getAttribute(Attributes.STEP_HEIGHT)?.addTransientModifier(
                AttributeModifier(CHARGE_BOOST_ID, 0.5, AttributeModifier.Operation.ADD_VALUE)
            )
        }
        if (!level.isClientSide && owner.tickCount % DAMAGE_INTERVAL == 0 && owner.getRandom().nextFloat() > 0.5f) {
            if (slot != null) itemStack.hurtAndBreak(1, owner, slot)
        }

        level.sendParticles(
            BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(owner.blockPosition().below())),
            owner.x, owner.y + 0.05, owner.z, 1, 0.0, 0.0, 0.0, 0.6
        )
        if (owner.tickCount%2==0) level.sendParticles(
            ParticleTypes.WHITE_SMOKE,
            owner.x, owner.randomY, owner.z, 2, 0.1, 0.1, 0.1, 0.05
        )

        val look = owner.lookAngle
        val horizontalLook = Vec3(look.x, 0.0, look.z).normalize()
        if (horizontalLook.lengthSqr() < 0.001) return

        val hitBox: AABB = owner.boundingBox
            .inflate(0.45, 0.15, 0.45)
            .expandTowards(horizontalLook.scale(0.75))

        val targets = level.getEntitiesOfClass(LivingEntity::class.java, hitBox) { target ->
            target !== owner &&
                    target.isAlive &&
                    !target.isSpectator
        }
        if (targets.isEmpty()) return

        for (target in targets) {
            val awayX = target.x - owner.x
            val awayZ = target.z - owner.z
            val length = sqrt(awayX * awayX + awayZ * awayZ).coerceAtLeast(0.001)

            target.knockback(
                KNOCKBACK_STRENGTH,
                -awayX / length,
                -awayZ / length
            )
        }
    }
}