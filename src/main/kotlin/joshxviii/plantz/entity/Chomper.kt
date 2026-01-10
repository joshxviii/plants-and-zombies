package joshxviii.plantz.entity

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.ai.goal.MeleePlantAttackGoal
import joshxviii.plantz.pazResource
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import java.util.UUID

class Chomper(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.CHOMPER, level) {

    companion object {
        private val CHOMP_ATTACK_MODIFIER = AttributeModifier(
            pazResource("chomp_attack"), 100.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        )
    }

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, ChompAttackGoal(this))
    }

    class ChompAttackGoal(
        plantEntity: Plant,
    ) : MeleePlantAttackGoal(
        plantEntity = plantEntity,
        attackReach = 1.85,
        cooldownTime = 60,
        actionDelay = 10,
        damageType = PazDamageTypes.CHOMPED,
        actionStartEffect = {
            plantEntity.playSound(SoundEvents.EVOKER_FANGS_ATTACK, 0.7f, 0.9f)
        }
    ) {
        override fun doAction() : Boolean {
            val target = plantEntity.target?: return false
            if(!target.`is`(PazEntities.TAG_CANNOT_CHOMP)) {
                //Add modifier to increase damage for insta kills
                plantEntity.getAttribute(Attributes.ATTACK_DAMAGE)?.addOrUpdateTransientModifier(CHOMP_ATTACK_MODIFIER)
            }

            !super.doAction()

            //remove modifier if it was added
            plantEntity.getAttribute(Attributes.ATTACK_DAMAGE)?.removeModifier(CHOMP_ATTACK_MODIFIER)
            if (!target.isAlive) {
                (plantEntity.level() as ServerLevel).sendParticles(
                    PazServerParticles.SPORE_HIT, target.x,target.y+target.eyeHeight,target.z,
                    30,
                    0.2, 0.2, 0.2,
                    0.32
                )
                target.discard()
            }
            return true
        }
    }
}