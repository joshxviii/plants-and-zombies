package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazEntities
import joshxviii.plantz.ai.goal.ExplodeGoal
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.SpawnGroupData
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

class PotatoMine(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.POTATO_MINE, level) {
    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, ExplodeGoal(
            plantEntity = this,
            radius = 0.75f,
            detectRange = 2.0,
            actionPredicate = { cooldown < 0 },
        ))
        this.targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Mob::class.java, 5, true, false) { target, level ->
            target is Enemy
            && target !is Plant
        })
    }

    init {
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnReason: EntitySpawnReason,
        groupData: SpawnGroupData?
    ): SpawnGroupData? {
        cooldown = 160 + random.nextInt(-60, 30)
        return super.finalizeSpawn(level, difficulty, spawnReason, groupData)
    }

    override fun tick() {
        super.tick()
    }

    override fun getMaxSwell() = 4
    override fun doPush(entity: Entity) {}
}