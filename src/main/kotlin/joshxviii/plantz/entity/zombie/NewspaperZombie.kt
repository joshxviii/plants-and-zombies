package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazItems
import joshxviii.plantz.PazSounds
import joshxviii.plantz.PazTags
import joshxviii.plantz.pazResource
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.TagKey
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

class NewspaperZombie(type: EntityType<out NewspaperZombie>, level: Level) : PazZombie(type, level) {

    companion object {
        val ANGRY_BONUS_ID: Identifier = pazResource("angry_zombie_bonus")
    }

    override fun getAmbientSound(): SoundEvent {
        return if (isAngry()) PazSounds.BROWNCOAT_AMBIENT else PazSounds.BROWNCOAT_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return if (isAngry()) PazSounds.BROWNCOAT_HURT else PazSounds.BROWNCOAT_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return if (isAngry()) PazSounds.BROWNCOAT_DEATH else PazSounds.BROWNCOAT_DEATH
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.ZOMBIE_STEP
    }

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    fun isAngry() : Boolean {
        return !mainHandItem.`is`(PazItems.NEWSPAPER)
    }

    override fun tick() {
        super.tick()
    }

    override fun onEquipItem(slot: EquipmentSlot, oldStack: ItemStack, stack: ItemStack) {
        super.onEquipItem(slot, oldStack, stack)
    }

    override fun equipmentHasChanged(previous: ItemStack, current: ItemStack): Boolean {
        val hasChanged = super.equipmentHasChanged(previous, current)
        if(mainHandItem.`is`(PazItems.NEWSPAPER)) {
            this.setLivingEntityFlag(LIVING_ENTITY_FLAG_IS_USING, true)
        }
        else {
            this.setLivingEntityFlag(LIVING_ENTITY_FLAG_IS_USING, false)
        }
        if (!hasChanged) return false
        if(previous.`is`(PazItems.NEWSPAPER)) applyAngerBoost()
        if(current.`is`(PazItems.NEWSPAPER)) removeAngerBoost()
        return true
    }

    fun removeAngerBoost() {
        getAttribute(Attributes.MOVEMENT_SPEED)!!.removeModifier(ANGRY_BONUS_ID)
        getAttribute(Attributes.ATTACK_DAMAGE)!!.removeModifier(ANGRY_BONUS_ID)
    }

    fun applyAngerBoost() {
        getAttribute(Attributes.MOVEMENT_SPEED)!!
            .addTransientModifier(
                AttributeModifier(
                    ANGRY_BONUS_ID,
                    0.1,
                    AttributeModifier.Operation.ADD_VALUE
                )
            )
        getAttribute(Attributes.ATTACK_DAMAGE)!!
            .addTransientModifier(
                AttributeModifier(
                    ANGRY_BONUS_ID,
                    1.5,
                    AttributeModifier.Operation.ADD_VALUE
                )
            )
        setCanBreakDoors(true)
    }

    override fun isBaby(): Boolean = false
    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {}
    override fun getPreferredWeaponType(): TagKey<Item> = PazTags.ItemTags.NEWSPAPER_ZOMBIE_PREFERRED_WEAPONS
    override fun canPickUpLoot(): Boolean = true
    override fun isSunSensitive(): Boolean = false
    override fun convertsInWater(): Boolean = false

    override fun dropEquipment(level: ServerLevel) {
        super.dropEquipment(level)
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, ZombieGroupData(false, false))
        setItemSlot(EquipmentSlot.MAINHAND, PazItems.NEWSPAPER.defaultInstance)
        setDropChance(EquipmentSlot.MAINHAND, 0.0f)
        return data
    }
}