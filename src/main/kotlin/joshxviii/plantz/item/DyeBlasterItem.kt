package joshxviii.plantz.item

import joshxviii.plantz.PazSounds
import joshxviii.plantz.entity.projectile.PaintBall
import joshxviii.plantz.percentFormat
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.ItemTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.item.ProjectileWeaponItem
import net.minecraft.world.item.component.ChargedProjectiles
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec2
import java.util.function.Predicate

class DyeBlasterItem(properties: Properties) : ProjectileWeaponItem(properties) {

    companion object {
        fun addToTooltip(consumer: MutableList<Component>) {
            consumer.add(1, Component.translatable("item.plantz.dye_blaster.description", AMMO_CHANCE.percentFormat())
                .withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC))
        }

        val DYES: Predicate<ItemStack> = Predicate { itemStack: ItemStack -> itemStack.`is`(ItemTags.DYES) }
        const val AMMO_CHANCE: Float = 0.4f
    }

    override fun useOnRelease(itemStack: ItemStack): Boolean {
        return super.useOnRelease(itemStack)
    }

    override fun onUseTick(level: Level, livingEntity: LivingEntity, itemStack: ItemStack, ticksRemaining: Int) {
        super.onUseTick(level, livingEntity, itemStack, ticksRemaining)
        val ammo = itemStack.get(DataComponents.CHARGED_PROJECTILES)?.items?.firstOrNull()?.create()
        if (ammo == null) {
            reload(level, livingEntity, itemStack)
            return
        }
        if (ticksRemaining%4 != 0) return
        level.playSound(livingEntity, livingEntity.blockPosition(), PazSounds.DYE_BLASTER, SoundSource.NEUTRAL, 0.3f, 0.7f)
        itemStack.hurtAndBreak(1, livingEntity, livingEntity.usedItemHand)
        if (level is ServerLevel) {
            if (level.random.nextFloat() < AMMO_CHANCE) {
                itemStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY)
                itemStack.remove(DataComponents.DYED_COLOR)
                reload(level, livingEntity, itemStack)
            }
            shoot(level, livingEntity, livingEntity.usedItemHand, itemStack, listOf(ammo), 1.2f, 8.0f, false, null)
        }
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val itemStack: ItemStack = player.getItemInHand(hand)
        val foundProjectile = !player.getProjectile(itemStack).isEmpty
        if (!player.hasInfiniteMaterials() && !foundProjectile && !player.isAlive) return InteractionResult.FAIL
        else {
            player.startUsingItem(hand)
            return InteractionResult.CONSUME
        }
    }

    fun reload(level: Level, livingEntity: LivingEntity, itemStack: ItemStack): ItemStack {
        val ammo = draw(itemStack, livingEntity.getProjectile(itemStack), livingEntity).firstOrNull()?: ItemStack.EMPTY
        if (!ammo.isEmpty && ammo.`is`(ItemTags.DYES)) {
            itemStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles(mutableListOf(ItemStackTemplate(ammo.item))))
            ammo.get(DataComponents.DYE)?.fireworkColor?.let { itemStack.set(DataComponents.DYED_COLOR, DyedItemColor(it)) }
            level.playSound(livingEntity, livingEntity.blockPosition(), SoundEvents.CROSSBOW_LOADING_END.value(), SoundSource.NEUTRAL, 0.3f, 1.4f)
        }
        return ammo
    }

    override fun getUseDuration(itemStack: ItemStack, user: LivingEntity): Int {
        return 72000
    }

    override fun getUseAnimation(itemStack: ItemStack): ItemUseAnimation {
        return ItemUseAnimation.CROSSBOW
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        return super.useOn(context)
    }

    override fun getAllSupportedProjectiles(): Predicate<ItemStack> = DYES

    override fun getDefaultProjectileRange(): Int = 12

    override fun createProjectile(
        level: Level, shooter: LivingEntity, heldItem: ItemStack, projectile: ItemStack, isCrit: Boolean
    ): Projectile {
        val color = projectile.get(DataComponents.DYE) ?: DyeColor.entries.toTypedArray().filter { it != DyeColor.BLACK }.random()
        return PaintBall(
            level,
            shooter,
            spawnOffset = Vec2(0.5f,-0.0f),
            color = color
        )
    }

    override fun shootProjectile(
        shooter: LivingEntity,
        projectileEntity: Projectile,
        index: Int,
        power: Float,
        uncertainty: Float,
        angle: Float,
        targetOverrride: LivingEntity?
    ) {
        projectileEntity.shootFromRotation(shooter, shooter.xRot, shooter.yRot + angle, 0f, power, uncertainty)
    }
}