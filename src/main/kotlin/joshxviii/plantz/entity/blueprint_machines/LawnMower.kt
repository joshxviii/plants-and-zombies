package joshxviii.plantz.entity.blueprint_machines

import joshxviii.plantz.entity.zombie.ZombieRobot
import joshxviii.plantz.item.BlueprintItem
import joshxviii.plantz.item.SeedPacketItem
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class LawnMower(type: EntityType<out LawnMower>, level: Level) : Entity(type, level) {
    companion object {

    }


    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        //TODO
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        //TODO
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        //TODO
    }

    override fun getPickResult(): ItemStack = BlueprintItem.stackFor(this.type)

    override fun hurtServer(
        level: ServerLevel,
        source: DamageSource,
        damage: Float
    ): Boolean {
        return true
    }


}