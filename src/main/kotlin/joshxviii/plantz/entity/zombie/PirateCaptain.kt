package joshxviii.plantz.entity.zombie

import it.unimi.dsi.fastutil.ints.IntList
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazSounds
import joshxviii.plantz.ai.ZombieState
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.ConversionParams
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.SpawnGroupData
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

class PirateCaptain(type: EntityType<out PirateCaptain>, level: Level) : PazZombie(type, level) {

    companion object {
        private fun getFirework(color: DyeColor, flightDuration: Int): ItemStack {
            val rocket = ItemStack(Items.FIREWORK_ROCKET).apply { set(
                DataComponents.FIREWORKS,
                Fireworks(
                    flightDuration.toByte().toInt(),
                    listOf(
                        FireworkExplosion(FireworkExplosion.Shape.BURST, IntList.of(color.fireworkColor), IntList.of(), false, false)
                    )
                )
            ) }
            return rocket
        }
    }

    init {
        xpReward = 80
    }

    override fun handleAttributes(difficultyModifier: Float, spawnReason: EntitySpawnReason) {}

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun canPickUpLoot(): Boolean = false
    override fun isLeftHanded(): Boolean = false

    override fun tick() {
        super.tick()
    }

    override fun remove(reason: RemovalReason) {
        if (reason == RemovalReason.KILLED) {
            convertTo(PazEntities.PIRATE_CAPTAIN_GHOST, ConversionParams.single(this, true, true)) {
                it.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED)
                it.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_SWORD.defaultInstance)
                it.setDropChance(EquipmentSlot.MAINHAND, 0.0f)
            }
        }
        super.remove(reason)
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, ZombieGroupData(false, false))

        setItemSlot(EquipmentSlot.MAINHAND, Items.CROSSBOW.defaultInstance)
        setDropChance(EquipmentSlot.MAINHAND, 0.0f)

        return data
    }
}