package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazDamageTypes
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.PazTags.EntityTypes.CANNOT_CHOMP
import joshxviii.plantz.ai.goal.MeleeAttackPlantGoal
import joshxviii.plantz.pazResource
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.chicken.Chicken
import net.minecraft.world.entity.animal.fish.AbstractFish
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class Chomper(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.CHOMPER, level) {

    companion object {
        private val CHOMP_ATTACK_MODIFIER = AttributeModifier(
            pazResource("chomp_attack"), 100.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        )
    }

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, ChompAttackGoal(this))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && (target is Enemy
                    || target is AbstractFish
                    || target is Chicken
                    || (target is Player && !isTame))
        })
    }

    class ChompAttackGoal(
        plantEntity: Plant,
    ) : MeleeAttackPlantGoal(
        usingEntity = plantEntity,
        attackReach = 1.85,
        cooldownTime = 60,
        actionDelay = 10,
        damageType = PazDamageTypes.CHOMP,
        actionStartEffect = {
            plantEntity.playSound(SoundEvents.EVOKER_FANGS_ATTACK, 0.7f, 0.9f)
        }
    ) {
        override fun doAction() : Boolean {
            val target = usingEntity.target?: return false
            if(!target.`is`(CANNOT_CHOMP)) {
                //Add modifier to increase damage for insta kills
                usingEntity.getAttribute(Attributes.ATTACK_DAMAGE)?.addOrUpdateTransientModifier(CHOMP_ATTACK_MODIFIER)
            }

            !super.doAction()

            //remove modifier if it was added
            usingEntity.getAttribute(Attributes.ATTACK_DAMAGE)?.removeModifier(CHOMP_ATTACK_MODIFIER)
            if (!target.isAlive) {
                (usingEntity.level() as ServerLevel).sendParticles(
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