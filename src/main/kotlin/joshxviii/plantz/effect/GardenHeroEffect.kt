package joshxviii.plantz.effect

import joshxviii.plantz.PazLootTables
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.storage.loot.LootTable

class GardenHeroEffect(
    category: MobEffectCategory,
    color: Int,
    var lootTables: List<ResourceKey<LootTable>> = listOf(PazLootTables.DEFAULT_HARD_MAIL_REWARD)
) : MobEffect(category, color) {

    override fun shouldApplyEffectTickThisTick(remainingDuration: Int, amplification: Int): Boolean {
        return super.shouldApplyEffectTickThisTick(remainingDuration, amplification)
    }

    override fun applyEffectTick(level: ServerLevel, mob: LivingEntity, amplification: Int): Boolean {
        return true
    }

}