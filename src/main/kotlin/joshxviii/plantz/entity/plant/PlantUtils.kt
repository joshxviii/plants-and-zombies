package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazSounds
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potions

object PlantUtils {
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
        success = true
    }
    else if (!isTame) {// try to tame
        if (random.nextFloat() < PazConfig.getTameChance(type)) {
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

enum class PlantGrowNeeds {
    SOIL,
    SUN,
    WATER,
    TIME;
}