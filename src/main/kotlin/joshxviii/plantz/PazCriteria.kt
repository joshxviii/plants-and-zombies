package joshxviii.plantz

import joshxviii.plantz.advancement.DiscoHypnoCriteria
import joshxviii.plantz.advancement.GrowSeedsCriteria
import joshxviii.plantz.advancement.PlantPotMinecartCriteria
import joshxviii.plantz.advancement.SendMailCriteria
import joshxviii.plantz.advancement.SimpleCriterionCondition
import joshxviii.plantz.advancement.SimpleCriterionTrigger
import joshxviii.plantz.advancement.ZombieRaidCriteria
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries

object PazCriteria {

    @JvmField
    val SEND_MAIL = registerCriteria("send_mail", SimpleCriterionTrigger(SendMailCriteria.CODEC))

    @JvmField
    val GROW_SEEDS = registerCriteria("grow_seeds", SimpleCriterionTrigger(GrowSeedsCriteria.CODEC))

    @JvmField
    val PLANT_POT_MINECRAFT = registerCriteria("plant_pot_minecart", SimpleCriterionTrigger(PlantPotMinecartCriteria.CODEC))

    @JvmField
    val DISCO_HYPNO = registerCriteria("disco_hypno", SimpleCriterionTrigger(DiscoHypnoCriteria.CODEC))

    @JvmField
    val WIN_ZOMBIE_RAID = registerCriteria("win_zombie_raid", SimpleCriterionTrigger(ZombieRaidCriteria.CODEC))

    fun <T, E : SimpleCriterionCondition<T>> registerCriteria(
        name: String,
        trigger: SimpleCriterionTrigger<T, E>
    ): SimpleCriterionTrigger<T, E> {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, pazResource(name), trigger)
    }

    fun initialize() {}
}