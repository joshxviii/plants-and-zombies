package joshxviii.plantz.effect

import joshxviii.plantz.GardenHeroRewards
import joshxviii.plantz.PazLootTables
import joshxviii.plantz.raid.WaveType
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

    override fun onEffectRemoved(effectInstance: MobEffectInstance, entity: LivingEntity) {
        (entity as? GardenHeroRewards)?.`plantz$setWaveList`(mutableListOf())
        super.onEffectRemoved(effectInstance, entity)
    }

}