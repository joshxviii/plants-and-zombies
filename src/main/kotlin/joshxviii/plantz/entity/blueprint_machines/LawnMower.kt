package joshxviii.plantz.entity.blueprint_machines

import joshxviii.plantz.item.BlueprintItem
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class LawnMower(type: EntityType<out LawnMower>, level: Level) : LivingEntity(type, level) {
    companion object {

    }

    override fun getPickResult(): ItemStack = BlueprintItem.stackFor(this.type)

    override fun hurtServer(
        level: ServerLevel,
        source: DamageSource,
        damage: Float
    ): Boolean {
        setRemoved(RemovalReason.KILLED)
        return true
    }

    override fun getMainArm(): HumanoidArm = HumanoidArm.RIGHT
}