package joshxviii.plantz.entity.plant

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazLootTables
import joshxviii.plantz.ai.goal.ActionGoal
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams

class GraveBuster(type: EntityType<out Plant>, level: Level) : Plant(PazEntities.GRAVE_BUSTER, level) {

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, BustGraveGoal(this))
    }
    override fun attackGoals() {}

    override fun canPlaceOn(block: BlockState): Boolean {
        return block.`is`(PazBlocks.ZEN_PLANT_POT) || block.`is`(PazBlocks.GRAVESTONE)
    }

    override fun tick() {
        super.tick()

        if (cooldown > -1) {
            val level = level() as? ServerLevel ?: return
            if (tickCount % 9 == 0) playSound(SoundEvents.TUFF_BRICKS_HIT)
            if (tickCount % 2 == 0) level.sendParticles(
                BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(blockPosition().below())),
                x, y, z, 10, 0.2, 0.0, 0.2, 0.4
            )
        }
    }

    class BustGraveGoal(
        private val graveBuster: GraveBuster,
    ) : ActionGoal(
        graveBuster, cooldownTime = 20, actionDelay = 37,
    ) {

        override fun canUse(): Boolean {
            return (usingEntity.tickCount > cooldownTime
                    && usingEntity.isAlive
                    && graveBuster.isTame
                    && !(usingEntity is Plant && (usingEntity.isAsleep || usingEntity.isGrowingSeeds)))
        }

        override fun canDoAction(): Boolean {
            return graveBuster.getBlockBelow().`is`(PazBlocks.GRAVESTONE)
        }

        override fun doAction(): Boolean {
            graveBuster.discard()
            val posBelow = graveBuster.blockPosition().below()
            val level = graveBuster.level() as? ServerLevel?: return false
            if (level.getBlockState(posBelow).`is`(PazBlocks.GRAVESTONE)) {
                level.destroyBlock(posBelow, false, graveBuster)
                // spawn loot
                val params: LootParams = LootParams.Builder(level)
                    .withParameter(LootContextParams.ORIGIN, graveBuster.position())
                    .create(LootContextParamSets.CHEST)
                val lootTable: LootTable =
                    level.server.reloadableRegistries().getLootTable(PazLootTables.GRAVESTONE_TREASURE)
                val items = lootTable.getRandomItems(params)
                items.forEach { item ->
                    val entity = ItemEntity(level, graveBuster.x, graveBuster.y-1f, graveBuster.z, item)
                    level.addFreshEntity(entity)
                }
                return true
            }
            return false
        }
    }
}