package joshxviii.plantz.entity

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazItems
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.providers.VanillaEnchantmentProviders

object ZombieEquipment {

    val CONE = {
        PazBlocks.CONE.asItem().defaultInstance }
    val BUCKET = {
        Items.BUCKET.defaultInstance }
    val DOOR = {
        PazItems.DOOR_SHIELD.defaultInstance.also { it.damageValue = RandomSource.create().nextIntBetweenInclusive(250, 300) } }
    val COFFIN = {
        PazItems.COFFIN.defaultInstance }

    fun Zombie.equipZombieEquipment(random: RandomSource, equipmentChance: Float) {
        if (getItemBySlot(EquipmentSlot.HEAD).isEmpty) {
            if (random.nextFloat() < 0.28f * equipmentChance) {
                setItemSlot(EquipmentSlot.HEAD, CONE())
                setDropChance(EquipmentSlot.HEAD, 0.02f)
            } else if (random.nextFloat() < 0.11f * equipmentChance && getItemBySlot(EquipmentSlot.HEAD).isEmpty) {
                val bucket = BUCKET()
                setItemSlot(EquipmentSlot.HEAD, bucket)
                setDropChance(EquipmentSlot.HEAD, 0f)
            }
        }

        if (getItemBySlot(EquipmentSlot.OFFHAND).isEmpty && random.nextFloat() < 0.07f * equipmentChance) {
            setItemSlot(EquipmentSlot.OFFHAND, DOOR())
            setDropChance(EquipmentSlot.OFFHAND, 0f)
        }

        if (getItemBySlot(EquipmentSlot.CHEST).isEmpty && random.nextFloat() < 0.05f * equipmentChance) {
            setItemSlot(EquipmentSlot.CHEST, COFFIN())
            setDropChance(EquipmentSlot.CHEST, 0f)
        }
    }

    fun Zombie.enchantZombieEquipment(
        level: ServerLevel,
        random: RandomSource,
        difficulty: DifficultyInstance
    ) {
        val headItem = getItemBySlot(EquipmentSlot.HEAD)
        if (
            !headItem.isEmpty &&
            (headItem.`is`(PazBlocks.CONE.asItem()) || headItem.`is`(Items.BUCKET)) &&
            random.nextFloat() < 0.5f * difficulty.specialMultiplier
        ) {
            EnchantmentHelper.enchantItemFromProvider(
                headItem,
                level.registryAccess(),
                VanillaEnchantmentProviders.MOB_SPAWN_EQUIPMENT,
                difficulty,
                random
            )
            setItemSlot(EquipmentSlot.HEAD, headItem)
        }
    }

}