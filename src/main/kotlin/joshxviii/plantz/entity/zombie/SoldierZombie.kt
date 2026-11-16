package joshxviii.plantz.entity.zombie

import joshxviii.plantz.PazDataSerializers.DATA_DYE_COLOR
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazSounds
import joshxviii.plantz.ai.goal.NavigateToTargetGoal
import joshxviii.plantz.ai.goal.ProjectileAttackGoal
import joshxviii.plantz.entity.projectile.PaintBall
import net.minecraft.core.component.DataComponents
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.*
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStackTemplate
import net.minecraft.world.item.component.ChargedProjectiles
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class SoldierZombie(type: EntityType<out SoldierZombie>, level: Level) : PazZombie(type, level) {

    companion object {
        val potentialDyes = listOf(
            DyeColor.GREEN,
            DyeColor.PURPLE,
            DyeColor.ORANGE,
            DyeColor.BLUE,
        )

        val DYE_COLOR: EntityDataAccessor<DyeColor> = SynchedEntityData.defineId<DyeColor>(SoldierZombie::class.java, DATA_DYE_COLOR)
    }

    init {
        xpReward = 10
    }

    var dyeColor: DyeColor
        get() = this.entityData.get(DYE_COLOR)
        set(value) = this.entityData.set(DYE_COLOR, value)

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(DYE_COLOR, DyeColor.WHITE)
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        output.store("dyeColor", DyeColor.CODEC, dyeColor)
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        input.read("dyeColor", DyeColor.CODEC).ifPresent { dyeColor -> this.dyeColor = dyeColor }
    }

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(3, NavigateToTargetGoal(this, keepAwayDistance = 7.0, alwaysFaceTarget = true))
        this.goalSelector.addGoal(4, ProjectileAttackGoal(
            usingEntity = this,
            projectileFactory =  { PaintBall(level(), this, color = dyeColor, damage = 1f) },
            velocity = 1.1,
            actionDelay = 40,
            soundEvent = PazSounds.DYE_BLASTER,
            actionEndEffect = {

            }))
    }

    override fun addBehaviourGoals() {
        addBehaviourGoalsNoMelee()
    }

    override fun doHurtTarget(level: ServerLevel, target: Entity): Boolean {
        val result = super.doHurtTarget(level, target)
        return result
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        val data = super.finalizeSpawn(level, difficulty, spawnReason, groupData)

        dyeColor = potentialDyes.random()
        val blaster = PazItems.DYE_BLASTER.defaultInstance
        blaster.set(DataComponents.DYED_COLOR, DyedItemColor(dyeColor.fireworkColor))
        setItemSlot(EquipmentSlot.MAINHAND, blaster)
        setDropChance(EquipmentSlot.MAINHAND, 0.0f)
        if (spawnReason != EntitySpawnReason.CONVERSION) {
            setCanBreakDoors(true)
        }

        return data
    }
}