package joshxviii.plantz.item

import joshxviii.plantz.entity.projectile.ThrownSunBottle
import net.minecraft.core.Direction
import net.minecraft.core.Position
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ProjectileItem
import net.minecraft.world.item.ProjectileItem.DispenseConfig
import net.minecraft.world.level.Level

class SunBottleItem(properties: Properties) : Item(properties), ProjectileItem {

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val itemStack = player.getItemInHand(hand)
        level.playSound(
            null,
            player.x,
            player.y,
            player.z,
            SoundEvents.SPLASH_POTION_THROW,
            SoundSource.NEUTRAL,
            0.5f,
            0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f)
        )
        if (level is ServerLevel) {
            Projectile.spawnProjectileFromRotation({ level: ServerLevel, mob: LivingEntity, itemStack: ItemStack ->
                ThrownSunBottle(
                    level,
                    mob,
                    itemStack
                )
            }, level, itemStack, player, -20.0f, 0.7f, 1.0f)
        }

        player.awardStat(Stats.ITEM_USED.get(this))
        itemStack.consume(1, player)
        return InteractionResult.SUCCESS
    }

    override fun asProjectile(
        level: Level,
        position: Position,
        itemStack: ItemStack,
        direction: Direction
    ): Projectile = ThrownSunBottle(level, position = position, itemStack = itemStack)

    override fun createDispenseConfig(): DispenseConfig {
        return DispenseConfig.builder().uncertainty(DispenseConfig.DEFAULT.uncertainty() * 0.5f)
            .power(DispenseConfig.DEFAULT.power() * 1.25f).build()
    }

}