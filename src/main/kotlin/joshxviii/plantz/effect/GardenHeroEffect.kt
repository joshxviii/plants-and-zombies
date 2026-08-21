package joshxviii.plantz.effect

import joshxviii.plantz.GardenHeroRewards
import joshxviii.plantz.PazLootTables
import joshxviii.plantz.raid.WaveType
import joshxviii.plantz.raid.ZombieRaid
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.storage.loot.LootTable

class GardenHeroEffect(
    category: MobEffectCategory,
    color: Int,
) : MobEffect(category, color) {

    companion object {
        const val EFFECT_COLOR = 0x8100AB
    }

    override fun onEffectRemoved(effectInstance: MobEffectInstance, entity: LivingEntity) {
        (entity as? GardenHeroRewards)?.`plantz$setWaveList`(mutableListOf())
        super.onEffectRemoved(effectInstance, entity)
    }

    // add default waves to list when effect is added
    override fun onEffectAdded(effectInstance: MobEffectInstance, entity: LivingEntity) {
        val hero = (entity as? GardenHeroRewards)?: return
        val waveList = hero.`plantz$getWaveList`()
        if (waveList.isEmpty()) {
            val newList = mutableListOf<WaveType>()
            for (i in 0..effectInstance.amplifier.coerceAtMost(ZombieRaid.MAXIMUM_WAVE_COUNT - 1)) {
                newList.add(WaveType.DEFAULT)
            }
            hero.`plantz$setWaveList`(newList)
        }
    }

}