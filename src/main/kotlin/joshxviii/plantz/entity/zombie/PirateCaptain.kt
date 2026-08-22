package joshxviii.plantz.entity.zombie

import it.unimi.dsi.fastutil.ints.IntList
import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.NavigateToTargetGoal
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import net.minecraft.core.component.DataComponents
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.*
import net.minecraft.world.entity.projectile.FireworkRocketEntity
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.item.CrossbowItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ChargedProjectiles
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

class PirateCaptain(type: EntityType<out PirateCaptain>, level: Level) : PazZombie(type, level) {

    companion object {
        private fun getFirework(): ItemStack {
            val rocket = ItemStack(Items.FIREWORK_ROCKET).apply { set(
                DataComponents.FIREWORKS,
                Fireworks(
                    3,
                    listOf(
                        FireworkExplosion(FireworkExplosion.Shape.LARGE_BALL, IntList.of(0xFF8C00, 0xFFFD700), IntList.of(0x28232C, 0xB22222), false, false),
                        FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(0xFFFFFF), IntList.of(0x777777), false, false)
                    )
                )
            ) }
            return rocket
        }
        val SHOT_TIME_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId<Int>(PirateCaptain::class.java, EntityDataSerializers.INT)
        const val FIREWORK_COOLDOWN = 200
    }

    init {
        xpReward = 80
    }

    var shootTime: Int
        get() = this.entityData.get(SHOT_TIME_ID)
        set(value) = this.entityData.set(SHOT_TIME_ID, value)

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(SHOT_TIME_ID, 0)
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(2, ProjectileAttackGoal(
            usingEntity = this,
            projectileFactory =  {
                FireworkRocketEntity(level(), getFirework(), this, x, y+eyeHeight, z, true)
            },
            velocity = 0.5,
            actionDelay = 60,
            soundEvent = SoundEvents.CROSSBOW_SHOOT,
            usePredicate = { shootTime<=0 },
            actionStartEffect = {
                shootTime=random.nextIntBetweenInclusive(1, 40)
                startUsingItem(ProjectileUtil.getWeaponHoldingHand(this, weaponItem.item))
            },
            actionEndEffect = {
                val item = weaponItem
                if (item.item is CrossbowItem) {
                    weaponItem.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY)
                    stopUsingItem()
                }
            }
        ))
        goalSelector.addGoal(3, NavigateToTargetGoal(this, keepAwayDistance = 6.0, alwaysFaceTarget = true))
    }

    override fun addBehaviourGoals() {
        addBehaviourGoalsNoMelee()
    }

    override fun getProjectile(heldWeapon: ItemStack): ItemStack {
        return getFirework()
    }

    override fun handleAttributes(difficultyModifier: Float, spawnReason: EntitySpawnReason) {}

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun canPickUpLoot(): Boolean = false
    override fun isLeftHanded(): Boolean = false

    override fun tick() {
        super.tick()
        if (shootTime>0) {
            if (shootTime++>FIREWORK_COOLDOWN) {
                shootTime=0
            }
        }
    }

    override fun remove(reason: RemovalReason) {
        if (reason == RemovalReason.KILLED) {
            convertTo(PazEntities.PIRATE_CAPTAIN_GHOST, ConversionParams.single(this, true, true)) {
                it.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED)
                it.setItemSlot(EquipmentSlot.MAINHAND, Items.IRON_SWORD.defaultInstance)
                it.setDropChance(EquipmentSlot.MAINHAND, 0.0f)
            }
        }
        super.remove(reason)
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, ZombieGroupData(false, false))

        setItemSlot(EquipmentSlot.MAINHAND, Items.CROSSBOW.defaultInstance)
        setDropChance(EquipmentSlot.MAINHAND, 0.0f)

        return data
    }
}