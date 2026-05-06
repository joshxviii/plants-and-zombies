package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazSounds
import joshxviii.plantz.entity.plant.PlantUtils.EXPLOSION_DAMAGE_CALCULATOR
import joshxviii.plantz.getTotalSun
import joshxviii.plantz.removeSunFromStorageAndInventory
import net.minecraft.ChatFormatting
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.random.WeightedList
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.OwnableEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.level.ExplosionDamageCalculator
import net.minecraft.world.level.Level
import net.minecraft.world.level.SimpleExplosionDamageCalculator
import java.util.Optional

object PlantUtils {
    val EXPLOSION_DAMAGE_CALCULATOR: ExplosionDamageCalculator = SimpleExplosionDamageCalculator(false, true, Optional.of<Float>(1f), Optional.ofNullable(null))
}

fun Plant.explode(
    radius: Float = 4.0f,
    sound: Holder.Reference<SoundEvent> = PazSounds.PLANT_EXPLODE,
    damageType: ResourceKey<DamageType> = PazDamageTypes.PLANT_AOE,
) {
    val level = this.level()
    val source = this.damageSources().source(damageType, this,
        if (PazConfig.PLAYER_CREDIT_FOR_PLANT_KILLS) this.rootOwner else this)
    level.explode(
        this,
        source,
        EXPLOSION_DAMAGE_CALCULATOR,
        x, y, z,
        radius,
        false,
        Level.ExplosionInteraction.MOB,
        ParticleTypes.SMOKE,
        ParticleTypes.EXPLOSION,
        WeightedList.of(),
        sound
    )
    this.discard()
}

// PLANT ITEM INTERACTIONS
// sun interaction
fun Plant.processSunItem(player: Player, item: ItemStack, hand: InteractionHand, growNeeds: PlantGrowNeeds): Boolean {
    val hasStoredSun = item.get(PazComponents.STORED_SUN)?.hasSun() == true
    val isSunItem = item.`is`(PazItems.SUN)
    if (!hasStoredSun && !isSunItem) return false
    val level = level() as? ServerLevel?: return false
    var success = false

    if (isTame && health < maxHealth) {// heal
        sunHeal(1)
        addParticlesAroundSelf(particle = ParticleTypes.HAPPY_VILLAGER)
        success = true
    }
    else if (!isTame) {// try to tame
        if (random.nextFloat() < (1f - (PazConfig.getSunCost(type) / 14f))*0.2f) {
            tame(player)
            level.broadcastEntityEvent(this, 7.toByte())
        } else level.broadcastEntityEvent(this, 6.toByte())
        success = true
    }
    else if (growNeeds == PlantGrowNeeds.SUN && verifyOwner(player)) {// grow seeds
        playSound(
            SoundEvents.BUBBLE_POP, 1.0f,
            receivedSun.toFloat()/sunRequiredForSeeds() + 0.9f
        )
        if (receivedSun++ >= sunRequiredForSeeds()) awardSeedPacket(player)
        success = true
    }

    if (success) {
        if (hasStoredSun) item.set(PazComponents.STORED_SUN, item.get(PazComponents.STORED_SUN)?.removeSun(1))
        else item.consume(1, player)
    }
    return success
}
// watering interaction
fun Plant.processWateringItem(player: Player, item: ItemStack, hand: InteractionHand, growNeeds: PlantGrowNeeds): Boolean {
    if (growNeeds != PlantGrowNeeds.WATER) return false
    val isWaterBottle = item.components.get(DataComponents.POTION_CONTENTS)?.`is`(Potions.WATER) == true
    val isWaterBucket = item.`is`(Items.WATER_BUCKET)
    val hasStoredWater = item.get(PazComponents.STORED_WATER)?.hasWater() == true
    val waterAmount = when (true) {
        isWaterBottle -> {
            player.setItemInHand(hand, ItemUtils.createFilledResult(item, player, ItemStack(Items.GLASS_BOTTLE)))
            this.playSound(SoundEvents.BOTTLE_EMPTY)
            1
        }
        isWaterBucket -> {
            player.setItemInHand(hand, ItemStack(Items.BUCKET))
            this.playSound(SoundEvents.BUCKET_EMPTY, 1.0f, 1.0f)
            8
        }
        (hasStoredWater) -> {
            this.playSound(PazSounds.WATERING_CAN)
            item.set(PazComponents.STORED_WATER, item.get(PazComponents.STORED_WATER)?.removeWater(2))
            2
        }
        else -> 0
    }
    this.receivedWater+=waterAmount
    if (waterAmount>0) {
        addParticlesAroundSelf()
        funnyBounce()
        return true
    }
    return false
}
// seed packet interaction
fun Plant.processSeedPacketInteraction(player: Player, packet: ItemStack): PacketInteractionResult {
    val type = packet.get(DataComponents.ENTITY_DATA)?.type()
    val availableSun = player.getTotalSun()
    val sunCost = packet.get(PazComponents.SUN_COST)?.sunCost?: 0
    val cantAfford = sunCost > availableSun && !player.hasInfiniteMaterials()

    val result = when (type) {
        PazEntities.COFFEE_BEAN -> {
            when {
                isGrowingSeeds -> {
                    player.sendOverlayMessage(Component.translatable("message.plantz.no_coffee_while_growing").withStyle(ChatFormatting.RED))
                    PacketInteractionResult.FAIL
                }
                cantAfford -> PacketInteractionResult.CANT_AFFORD
                isAsleep -> {
                    applyCoffeeBuff()
                    PacketInteractionResult.SUCCESS
                }
                else -> PacketInteractionResult.FAIL
            }
        }
        else -> PacketInteractionResult.NO_INTERACTION
    }
    // show message
    if (result == PacketInteractionResult.CANT_AFFORD) player.sendOverlayMessage(Component.translatable("message.plantz.not_enough_sun", availableSun, sunCost).withStyle(ChatFormatting.RED))
    // remove used sun
    if (result == PacketInteractionResult.SUCCESS && !player.hasInfiniteMaterials()) player.removeSunFromStorageAndInventory(sunCost)
    return result
}
enum class PacketInteractionResult {
    SUCCESS,
    FAIL,
    CANT_AFFORD,
    NO_INTERACTION
}

enum class PlantGrowNeeds {
    SOIL,
    SUN,
    WATER,
    TIME;
}