package joshxviii.plantz.entity.zombie

import PazOwnableZombie
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.item.BlueprintItem
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.control.BodyRotationControl
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.phys.Vec3
import org.spongepowered.asm.mixin.injection.selectors.TargetSelector
import java.util.*

abstract class ZombieRobot(type: EntityType<out ZombieRobot>, level: Level) : PazOwnableZombie(type, level) {

    companion object {
        val DATA_OWNERUUID_ID: EntityDataAccessor<Optional<EntityReference<LivingEntity>>> = SynchedEntityData.defineId(ZombieRobot::class.java, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE)
    }

    init {
        xpReward = 2
        this.lookControl = object : LookControl(this) { override fun clampHeadRotationToBody() {} }
    }

    override fun setPos(x: Double, y: Double, z: Double) {
        if (this.isPassenger || !onGround()) super.setPos(x, y, z)
        else super.setPos(Mth.floor(x) + 0.5, y, Mth.floor(z) + 0.5)
    }

    override fun createBodyControl(): BodyRotationControl = object : BodyRotationControl(this) { override fun clientTick() {} }

    override fun getDeltaMovement(): Vec3 = Vec3(0.0, super.deltaMovement.y, 0.0)
    override fun setDeltaMovement(deltaMovement: Vec3) {
        if (!onGround() || isInWater) return super.setDeltaMovement(deltaMovement)
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(DATA_OWNERUUID_ID, Optional.empty())
    }

    override fun getOwnerReference(): EntityReference<LivingEntity>? {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    override fun doPush(entity: Entity) {}


    fun setOwner(owner: LivingEntity?) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable<LivingEntity>(owner).map { EntityReference.of(it) })
    }

    fun setOwnerReference(owner: EntityReference<LivingEntity>?) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable<EntityReference<LivingEntity>>(owner))
    }

    override fun getAmbientSound(): SoundEvent = SoundEvents.EMPTY
    override fun getHurtSound(source: DamageSource): SoundEvent = SoundEvents.EMPTY
    override fun getDeathSound(): SoundEvent = SoundEvents.EMPTY
    override fun getStepSound(): SoundEvent = SoundEvents.EMPTY

    override fun getPickResult(): ItemStack = BlueprintItem.stackFor(this.type)

    override fun canEquipDuckyInWater(): Boolean = false
    override fun canPickUpLoot(): Boolean = false
    override fun canBreakDoors(): Boolean = false
    override fun canHoldItem(itemStack: ItemStack): Boolean = false
    override fun isBaby() = false

    override fun registerGoals() {
        this.goalSelector.addGoal(3, RandomLookAroundGoal(this))
        this.goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        // attack owner's target goal
        this.targetSelector.addGoal(2, NearestAttackableTargetGoal(this, LivingEntity::class.java, true
        ) { entity, level ->
            if (this.owner is Player) (
                entity !is Plant &&
                entity !is Creeper &&
                (entity is Zombie || (entity is Enemy))
            )
            else (
                entity is Player || entity is Plant
            )
        })
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, ZombieGroupData(false, false))



        return data
    }

}