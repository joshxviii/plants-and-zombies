package joshxviii.plantz.entity.plant

import com.mojang.logging.LogUtils
import joshxviii.plantz.*
import joshxviii.plantz.item.SeedPacketItem
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.ProblemReporter.ScopedCollector
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.item.component.TypedEntityData
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.TagValueInput
import net.minecraft.world.level.storage.TagValueOutput
import net.minecraft.world.phys.Vec3


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
    val infiniteWater = player.hasInfiniteMaterials()
    val waterAmount = when (true) {
        isWaterBottle -> {
            if (!infiniteWater) player.setItemInHand(hand, ItemUtils.createFilledResult(item, player, ItemStack(Items.GLASS_BOTTLE)))
            this.playSound(SoundEvents.BOTTLE_EMPTY)
            1
        }
        isWaterBucket -> {
            if (!infiniteWater) player.setItemInHand(hand, ItemStack(Items.BUCKET))
            this.playSound(SoundEvents.BUCKET_EMPTY, 1.0f, 1.0f)
            8
        }
        (hasStoredWater) -> {
            this.playSound(PazSounds.WATERING_CAN)
            if (!infiniteWater) item.set(PazComponents.STORED_WATER, item.get(PazComponents.STORED_WATER)?.removeWater(2))
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

// glove interaction
fun Plant.processGloveItem(player: Player, item: ItemStack, hand: InteractionHand): Boolean {
    if (!item.`is`(PazItems.GARDENING_GLOVE)) return false
    if (isAttached()) return false
    when {
        // owner check
        !verifyOwner(player) -> return false
        // roll wallnut
        (this is WallNut && this.rolling) -> {
            dropAllLeashConnections(player)
            deltaMovement = Vec3.ZERO
        }
        // hold plant
        (player.isSecondaryUseActive) -> {
            if (item.has(DataComponents.ENTITY_DATA)) {
                player.sendOverlayMessage(Component.translatable("message.plantz.glove_full").withStyle(ChatFormatting.RED))
                return true
            }

            val data = this.saveAsCompoundTag()
            item.set(DataComponents.ENTITY_DATA, TypedEntityData.of(this.type, data))

            playSound(SoundEvents.ARMOR_EQUIP_LEATHER.value())
            addParticlesAroundSelf(level(), ParticleTypes.DUST_PLUME, verticalSpreadScale = 0.5, height = 0f)
            this.discard()
            return true
        }
        // exit early when holding plant
        (item.has(DataComponents.ENTITY_DATA)) -> return false
        // pet :)
        else -> {
            this.funnyBounce()
            addParticlesAroundSelf(level(), ParticleTypes.HEART, amount = 0..0, height = eyeHeight)
        }
    }
    return true
}

// seed packet interaction
fun Plant.processSeedPacketInteraction(player: Player, itemStack: ItemStack, blockState: BlockState? = null): PacketInteractionResult {
    if (!verifyOwner(player)) return PacketInteractionResult.FAIL
    val type = itemStack.get(DataComponents.ENTITY_DATA)?.type()
    val availableSun = player.getTotalSun()
    val sunCost = itemStack.get(PazComponents.SUN_COST)?.getSunCost(type)?: 0
    val cantAfford = sunCost > availableSun && !player.hasInfiniteMaterials()

    val result = when (type) {
        PazEntities.COFFEE_BEAN -> {
            when {
                !isAsleep -> PacketInteractionResult.NO_INTERACTION
                isGrowingSeeds -> {
                    player.sendOverlayMessage(Component.translatable("message.plantz.growing", name.copy().withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.DARK_RED))
                    PacketInteractionResult.FAIL
                }
                cantAfford -> PacketInteractionResult.CANT_AFFORD
                coffeeBuff>0 -> PacketInteractionResult.FAIL
                else -> {
                    applyCoffeeBuff()
                    PacketInteractionResult.SUCCESS
                }
            }
        }
        PazEntities.PUMPKIN -> pumpkinSeedInteraction(itemStack, cantAfford, player)
        else -> when (this) {// placing plant into pumpkin
            is Pumpkin -> if (isGrowingSeeds) PacketInteractionResult.NO_INTERACTION else pumpkinSeedInteraction(itemStack, cantAfford, player)
            else -> PacketInteractionResult.NO_INTERACTION
        }
    }
    // show message
    if (result == PacketInteractionResult.CANT_AFFORD) player.sendOverlayMessage(Component.translatable("message.plantz.not_enough_item", PazItems.SUN.name(), availableSun, sunCost).withStyle(ChatFormatting.RED))
    // remove used sun
    if (result == PacketInteractionResult.SUCCESS && !player.hasInfiniteMaterials()) {
        player.removeSunFromStorageAndInventory(sunCost)
        SeedPacketItem.applyCooldown(itemStack, player)
    }
    return result
}

fun Plant.pumpkinSeedInteraction(itemStack: ItemStack, cantAfford: Boolean, player: Player): PacketInteractionResult {
    if (this.hasPumpkinArmor()) return PacketInteractionResult.NO_INTERACTION
    if (this !is Pumpkin) {
        applyPumpkinArmor(pumpkinData = itemStack.get(DataComponents.ENTITY_DATA))
        return PacketInteractionResult.SUCCESS
    }
    val seedData = itemStack.get(DataComponents.ENTITY_DATA)?: return PacketInteractionResult.NO_INTERACTION
    val seedType = seedData.type()
    if (BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(seedType).`is`(PazTags.EntityTypes.CANNOT_PUMPKIN)) return PacketInteractionResult.NO_INTERACTION
    if (cantAfford) return PacketInteractionResult.CANT_AFFORD
    (level() as? ServerLevel)?.let { level ->
        seedType.create(
            level, EntityType.createDefaultStackConfig(level, itemStack, null), blockPosition(), EntitySpawnReason.SPAWN_ITEM_USE, true, true
        )?.let {
            val pumpkinData = this.saveAsCompoundTag()
            val filteredData = pumpkinData.copy().apply {
                remove("CustomName")
                remove("plantz:State")
                remove("plantz:Cooldown")
            }
            val mergedData = TypedEntityData.of(seedType, seedData.copyTagWithoutId().merge(filteredData))
            mergedData.loadInto(it)
            if (it is Plant) it.applyPumpkinArmor(pumpkinData = TypedEntityData.of(this.type, pumpkinData))
            if (level.addFreshEntity(it)) discard()
        }
        return PacketInteractionResult.SUCCESS
    }
    return PacketInteractionResult.SUCCESS
}

fun Entity.saveAsCompoundTag(discards: List<String> = listOf("Pos")): CompoundTag {
    if (level().isClientSide) return CompoundTag()
    ScopedCollector(problemPath(), LogUtils.getLogger()).use { reporter ->
        val output = TagValueOutput.createWithContext(reporter, registryAccess())
        saveWithoutId(output)
        if (this is Plant) plantSaveData(output)
        discards.forEach { output.discard(it) }
        output.discard("leash")
        output.discard("UUID")
        return output.buildResult()
    }
}

fun Entity.loadFromCompoundTag(data: CompoundTag) {
    ScopedCollector(problemPath(), LogUtils.getLogger()).use { reporter ->
        val newData = TagValueInput.create(reporter, registryAccess(), data)
        newData
        this.load(newData)
    }
}

enum class PacketInteractionResult {
    SUCCESS,
    FAIL,
    CANT_AFFORD,
    NO_INTERACTION
}

enum class PlantGrowNeeds {
    CANNOT_GROW,
    SOIL,
    SUN,
    WATER,
    TIME;
}