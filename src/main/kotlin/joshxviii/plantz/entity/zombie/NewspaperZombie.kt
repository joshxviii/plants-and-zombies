package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazItems
import joshxviii.plantz.PazSounds
import joshxviii.plantz.PazTags
import joshxviii.plantz.entity.plant.WallNut.Companion.ROLLING_ID
import joshxviii.plantz.pazResource
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.TagKey
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

class NewspaperZombie(type: EntityType<out NewspaperZombie>, level: Level) : PazZombie(type, level) {

    companion object {
        val ANGRY_BONUS_ID: Identifier = pazResource("angry_zombie_bonus")
    }

    override fun getAmbientSound(): SoundEvent {// TODO: custom sounds
        return if (isAngry()) PazSounds.NEWSPAPER_ZOMBIE_AMBIENT else PazSounds.NEWSPAPER_ZOMBIE_AMBIENT
    }
    override fun getHurtSound(source: DamageSource): SoundEvent {
        return if (isAngry()) PazSounds.NEWSPAPER_ZOMBIE_HURT else PazSounds.NEWSPAPER_ZOMBIE_HURT
    }
    override fun getDeathSound(): SoundEvent {
        return if (isAngry()) PazSounds.NEWSPAPER_ZOMBIE_DEATH else PazSounds.NEWSPAPER_ZOMBIE_DEATH
    }
    override fun getStepSound(): SoundEvent {
        return SoundEvents.ZOMBIE_STEP
    }

    fun isAngry() : Boolean {
        return !mainHandItem.`is`(PazItems.NEWSPAPER)
    }

    override fun equipmentHasChanged(previous: ItemStack, current: ItemStack): Boolean {
        val hasChanged = super.equipmentHasChanged(previous, current)
        updateNewspaper()
        return hasChanged
    }

    fun updateNewspaper() {
        if(mainHandItem.`is`(PazItems.NEWSPAPER)) {
            this.startUsingItem(usedItemHand)
            this.setLivingEntityFlag(LIVING_ENTITY_FLAG_IS_USING, true)
            removeAngerBoost()
        }
        else {
            this.setLivingEntityFlag(LIVING_ENTITY_FLAG_IS_USING, false)
            applyAngerBoost()
        }
    }

    fun removeAngerBoost() {
        getAttribute(Attributes.MOVEMENT_SPEED)!!.let {
            if (it.hasModifier(ANGRY_BONUS_ID)) it.removeModifier(ANGRY_BONUS_ID)
        }
        getAttribute(Attributes.ATTACK_DAMAGE)!!.let {
            if (it.hasModifier(ANGRY_BONUS_ID)) it.removeModifier(ANGRY_BONUS_ID)
        }
        setCanBreakDoors(false)
    }

    fun applyAngerBoost() {
        getAttribute(Attributes.MOVEMENT_SPEED)!!.let {
            if (!it.hasModifier(ANGRY_BONUS_ID)) it.addTransientModifier(AttributeModifier(ANGRY_BONUS_ID, 0.11, AttributeModifier.Operation.ADD_VALUE))
        }
        getAttribute(Attributes.ATTACK_DAMAGE)!!.let {
            if (!it.hasModifier(ANGRY_BONUS_ID)) it.addTransientModifier(AttributeModifier(ANGRY_BONUS_ID, 2.0, AttributeModifier.Operation.ADD_VALUE))
        }
        setCanBreakDoors(true)
    }

    override fun getPreferredWeaponType(): TagKey<Item> = PazTags.ItemTags.NEWSPAPER_ZOMBIE_PREFERRED_WEAPONS
    override fun canPickUpLoot(): Boolean = true

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, groupData)

        setItemSlot(EquipmentSlot.MAINHAND, PazItems.NEWSPAPER.defaultInstance)
        setDropChance(EquipmentSlot.MAINHAND, 0.0f)
        return data
    }
}