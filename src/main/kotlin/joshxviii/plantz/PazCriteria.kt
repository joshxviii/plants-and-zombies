package joshxviii.plantz

import joshxviii.plantz.advancement.*
import net.minecraft.advancements.CriterionProgress
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries

object PazCriteria {

    @JvmField
    val SEND_MAIL = registerCriteria("send_mail", SimpleCriterionTrigger(SendMailCriteria.CODEC))

    @JvmField
    val RECEIVE_HERO_MAIL = registerCriteria("receive_hero_mail", SimpleCriterionTrigger(ReceiveHeroMailCriteria.CODEC))

    @JvmField
    val RELOCATION = registerCriteria("relocate", SimpleCriterionTrigger(RelocatePlantCriteria.CODEC))

    @JvmField
    val GROW_SEEDS = registerCriteria("grow_seeds", SimpleCriterionTrigger(GrowSeedsCriteria.CODEC))

    @JvmField
    val PLANT_POT_MINECRAFT = registerCriteria("plant_pot_minecart", SimpleCriterionTrigger(PlantPotMinecartCriteria.CODEC))

    @JvmField
    val DISCO_HYPNO = registerCriteria("disco_hypno", SimpleCriterionTrigger(DiscoHypnoCriteria.CODEC))

    @JvmField
    val WIN_ZOMBIE_RAID = registerCriteria("win_zombie_raid", SimpleCriterionTrigger(ZombieRaidCriteria.CODEC))

    @JvmField
    val START_HARDEST_RAID = registerCriteria("start_hardest_raid", SimpleCriterionTrigger(ZombieHardestRaidCriteria.CODEC))

    fun <T, E : SimpleCriterionCondition<T>> registerCriteria(
        name: String,
        trigger: SimpleCriterionTrigger<T, E>
    ): SimpleCriterionTrigger<T, E> {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, pazResource(name), trigger)
    }

    fun initialize() {}
}