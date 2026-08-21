package joshxviii.plantz

import joshxviii.plantz.advancement.*
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.advancements.CriterionTrigger
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries

object PazCriteria {

    @JvmField
    val RAID_WAVE_TRIGGER: RaidWaveTrigger = registerTrigger("raid_wave", RaidWaveTrigger())

    @JvmField
    val SEND_MAIL = registerCriteria("send_mail", SimpleCriterionTrigger(SimpleCheckCriteria.CODEC))

    @JvmField
    val RECEIVE_HERO_MAIL = registerCriteria("receive_hero_mail", SimpleCriterionTrigger(SimpleCheckCriteria.CODEC))

    @JvmField
    val RELOCATION = registerCriteria("relocate", SimpleCriterionTrigger(SimpleCheckCriteria.CODEC))

    @JvmField
    val GROW_SEEDS = registerCriteria("grow_seeds", SimpleCriterionTrigger(SimpleCheckCriteria.CODEC))

    @JvmField
    val GRAVE_BUSTER_BUST = registerCriteria("grave_buster_bust", SimpleCriterionTrigger(SimpleCheckCriteria.CODEC))

    @JvmField
    val PLANT_POT_MINECRAFT = registerCriteria("plant_pot_minecart", SimpleCriterionTrigger(SimpleCheckCriteria.CODEC))

    @JvmField
    val DISCO_HYPNO = registerCriteria("disco_hypno", SimpleCriterionTrigger(SimpleCheckCriteria.CODEC))

    @JvmField
    val WIN_ZOMBIE_RAID = registerCriteria("win_zombie_raid", SimpleCriterionTrigger(SimpleCheckCriteria.CODEC))

    @JvmField
    val START_HARDEST_RAID = registerCriteria("start_hardest_raid", SimpleCriterionTrigger(SimpleCheckCriteria.CODEC))

    fun <T, E : SimpleCriterionCondition<T>> registerCriteria(
        name: String,
        trigger: SimpleCriterionTrigger<T, E>
    ): SimpleCriterionTrigger<T, E> {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, pazResource(name), trigger)
    }

    fun <T : CriterionTrigger<*>> registerTrigger(name: String, criterion: T): T {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, pazResource(name), criterion)
    }

    fun initialize() {}
}