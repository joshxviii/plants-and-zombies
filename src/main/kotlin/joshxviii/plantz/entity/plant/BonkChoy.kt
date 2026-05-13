package joshxviii.plantz.entity.plant

import joshxviii.plantz.*
import joshxviii.plantz.PazTags.EntityTypes.CANNOT_CHOMP
import joshxviii.plantz.ai.goal.MeleeAttackActionGoal
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
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
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class BonkChoy(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.BONK_CHOY, level) {

    companion object {

    }

    override fun registerGoals() {
        super.registerGoals()
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, LivingEntity::class.java, 5, true, false) { target, level ->
            target !is Plant
                    && (target is Zombie
                    || (target is Enemy && isTame)
                    || (target is Player && !isTame))
        })
    }

    override fun tick() {
        super.tick()
    }
}