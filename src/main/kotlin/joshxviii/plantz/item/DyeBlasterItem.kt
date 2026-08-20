package joshxviii.plantz.item

import joshxviii.plantz.entity.projectile.PaintBall
import net.minecraft.core.component.DataComponents
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
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.item.ProjectileWeaponItem
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import java.util.function.Predicate

class DyeBlasterItem(properties: Properties) : ProjectileWeaponItem(properties) {

    companion object {
        val DYES: Predicate<ItemStack> = Predicate { itemStack: ItemStack -> itemStack.`is`(ItemTags.DYES) }
    }

    override fun onUseTick(level: Level, livingEntity: LivingEntity, itemStack: ItemStack, ticksRemaining: Int) {
        super.onUseTick(level, livingEntity, itemStack, ticksRemaining)
        val ammo = livingEntity.getProjectile(itemStack)
        if (ammo.isEmpty) return
        if (ticksRemaining%4 != 0) return
        level.playSound(
            null,
            livingEntity.x,
            livingEntity.y,
            livingEntity.z,
            SoundEvents.LAVA_POP,
            SoundSource.PLAYERS,
            1.0f,
            1.0f / (level.getRandom().nextFloat() * 0.4f + 1.2f) + 4.5f
        )
        itemStack.hurtAndBreak(1, livingEntity, livingEntity.usedItemHand)
        if (level.random.nextFloat() < 0.4f) draw(itemStack, ammo, livingEntity)
        if (level is ServerLevel) shoot(level, livingEntity, livingEntity.usedItemHand, itemStack, listOf(ammo), 1.2f, 8.0f, false, null)
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