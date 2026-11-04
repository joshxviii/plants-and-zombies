package joshxviii.plantz.ai.goal

import joshxviii.plantz.PazEffects
import joshxviii.plantz.entity.isActiveInRaid
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.entity.zombie.PazZombieGroupData
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.Vec3
import java.util.UUID
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class SummonMinionsGoal(
    private val summoner: PazZombie,
    private val minionType: EntityType<out PazZombie>,
    private val trackedMinionIds: MutableSet<UUID>?,
    private val isSummoning: () -> Boolean,
    private val beginSummoning: () -> Unit,
    private val configureMinion: (PazZombie) -> Unit = {},
    private val canSummon: () -> Boolean = { true },
    private val shouldAddToRaid: Boolean = true
) : Goal() {
    companion object {
        const val DEFAULT_AMOUNT = 3
        const val MAX_TRACKED_MINIONS = 5
        const val MAX_NEARBY_MINIONS = 8
        const val SUMMON_DISTANCE = 2
        const val SUMMON_DELAY_TIME = 255
    }

    private var summonTime = summoner.random.nextInt(20, 60)

    override fun canUse(): Boolean {
        if (isSummoning()) return true
        if (!canSummon() || !summoner.isAggressive || summoner.isDeadOrDying || summoner.target?.isAlive != true) return false

        val level = summoner.level() as? ServerLevel ?: return false
        updateTrackedMinions(level)
        return (trackedMinionIds?.size ?: 0) < MAX_TRACKED_MINIONS && countNearbyMinions(level) < MAX_NEARBY_MINIONS
    }

    override fun tick() {
        if (--summonTime == 0 && !isSummoning()) beginSummoning()
        if (isSummoning() && summonTime < -8) {
            summonMinions()
        }
    }

    private fun updateTrackedMinions(level: ServerLevel) {
        trackedMinionIds?.removeIf { uuid -> level.getEntity(uuid)?.let { !it.isAlive } == true }
    }

    private fun countNearbyMinions(level: ServerLevel): Int =
        level.getEntitiesOfClass(
            minionType.baseClass,
            summoner.boundingBox.inflate(16.0)
        ) { it.`is`(minionType) }.size

    private fun getSummonAmount(level: ServerLevel): Int {
        var amount = DEFAULT_AMOUNT
        val difficultyMultiplier = level.getCurrentDifficultyAt(summoner.blockPosition()).specialMultiplier
        if (difficultyMultiplier > 0.0f) {
            repeat(3) {
                if (summoner.random.nextFloat() < 0.25f * difficultyMultiplier) amount++
            }
        }
        return amount
    }

    private fun summonMinions() {
        summonTime = SUMMON_DELAY_TIME + summoner.random.nextInt(50)
        val target = summoner.target ?: return
        val level = summoner.level() as? ServerLevel ?: return
        updateTrackedMinions(level)

        val nearbyCount = countNearbyMinions(level)
        val remainingTracked = MAX_TRACKED_MINIONS - (trackedMinionIds?.size ?: 0)
        val remainingNearby = MAX_NEARBY_MINIONS - nearbyCount
        val amount = min(getSummonAmount(level), min(remainingTracked, remainingNearby))
        if (amount <= 0) return

        val angleToTarget = summoner.yRot * Mth.DEG_TO_RAD
        val minY = (min(target.y, summoner.y) - 2.0).coerceIn(summoner.y - 3.0, summoner.y + 2.0)
        val maxY = (max(target.y, summoner.y) + 2.0).coerceIn(summoner.y - 3.0, summoner.y + 2.0)
        val angleBetween = 2.0 * Math.PI / amount

        for (index in 1..amount) {
            val angleOffset = angleBetween * index - angleBetween * (amount + 1) * 0.5
            val x = summoner.x + cos(angleOffset + angleToTarget) * SUMMON_DISTANCE
            val z = summoner.z + sin(angleOffset + angleToTarget) * SUMMON_DISTANCE
            tryCreateMinion(level, x, z, minY, maxY, angleToTarget)
        }
    }

    private fun tryCreateMinion(level: ServerLevel, x: Double, z: Double, minY: Double, maxY: Double, angle: Float) {
        var pos = BlockPos.containing(x, maxY, z)
        var topOffset = 0.0
        var foundSpawnPosition = false

        do {
            val belowPos = pos.below()
            val belowState = level.getBlockState(belowPos)
            val state = level.getBlockState(pos)
            if (belowState.isFaceSturdy(level, belowPos, Direction.UP)) {
                if (!level.isEmptyBlock(pos)) {
                    val shape = state.getCollisionShape(level, pos)
                    if (!shape.isEmpty) topOffset = shape.max(Direction.Axis.Y)
                }
                foundSpawnPosition = true
                break
            }
            if (state.fluidState.`is`(Fluids.WATER)) {
                topOffset = state.fluidState.getHeight(level, pos).toDouble()
                foundSpawnPosition = true
                break
            }
            pos = pos.below()
        } while (pos.y >= Mth.floor(minY) - 1)

        if (!foundSpawnPosition) return

        val minion = minionType.create(level, EntitySpawnReason.MOB_SUMMONED) ?: return
        minion.snapTo(
            BlockPos.containing(x, pos.y + topOffset, z),
            angle * (180.0f / Math.PI.toFloat()),
            0.0f
        )
        minion.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.REINFORCEMENT, PazZombieGroupData(
            isRaider = summoner.isActiveInRaid() && shouldAddToRaid,
            ignoreFlag = true
        ))
        minion.setPersistenceRequired()
        summoner.activeEffects.find { it.effect.`is`(PazEffects.HYPNOTIZE.unwrapKey().orElseThrow()) }
            ?.let(minion::addEffect)
        configureMinion(minion)

        if (!level.addFreshEntity(minion)) return
        trackedMinionIds?.add(minion.uuid)
        level.gameEvent(GameEvent.ENTITY_PLACE, Vec3(x, pos.y + topOffset, z), GameEvent.Context.of(summoner))
    }
}
