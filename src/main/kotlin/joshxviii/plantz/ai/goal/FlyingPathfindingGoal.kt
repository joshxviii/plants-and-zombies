package joshxviii.plantz.ai.goal

import joshxviii.plantz.ai.ZombieState
import joshxviii.plantz.entity.zombie.PazZombie
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.Vec3
import java.util.EnumSet

open class FlyingPathfindingGoal (
    private val entity: PathfinderMob,
    val hoverGroundHeight: Double = 0.0
): Goal() {

    companion object {
        const val FORCE_RANDOM_MOVE_DELAY = 100
    }

    private val flyingSpeed: Double = entity.getAttribute(Attributes.FLYING_SPEED)?.value ?: 0.0
    private var flySpeedMultiplier: Double = 1.0
    private var moveDirection: Vec3 = Vec3.ZERO
    private var randomMoveDelay = 0

    override fun canUse(): Boolean {
        if (entity is PazZombie && entity.state == ZombieState.FLYING) return true
        if (hoverGroundHeight > 0) {
            val distanceAboveGround = entity.y - entity.level().getHeight(Heightmap.Types.WORLD_SURFACE, entity.blockPosition()).toDouble()
            if (distanceAboveGround < hoverGroundHeight) return true
        }
        val target = entity.target?: return false
        return !(target.distanceTo(entity) < 1 || !entity.hasLineOfSight(target)) && (!entity.onGround() && entity !is PazZombie)
    }

    override fun canContinueToUse(): Boolean {
        return entity.getMoveControl().hasWanted()
    }

    open fun move() {
        val dir = moveDirection
        val speed = flySpeedMultiplier * flyingSpeed
        var moveVector = Vec3((dir.x) * speed, (dir.y) * speed * 2, (dir.z) * speed).add(entity.deltaMovement.scale(0.75))
        if (hoverGroundHeight > 0) {
            val distanceAboveGround = entity.y - entity.level().getHeight(Heightmap.Types.WORLD_SURFACE, entity.blockPosition()).toDouble()
            val diff = Mth.clamp((hoverGroundHeight - distanceAboveGround) * .2, -0.3, 0.5)
            if (diff>0 || entity.target==null) moveVector = moveVector.add(0.0, diff, 0.0)
        }

        entity.deltaMovement = moveVector.scale(0.5)
    }

    override fun tick() {
        if (!moveTowardsTarget()) moveRandomly()
        move()
    }

    fun moveTowardsTarget(): Boolean {
        val target = entity.target?: return false
        val targetPosition = target.eyePosition.subtract(entity.position())

        val distance = targetPosition.length()

        flySpeedMultiplier = if (distance <= 0.75) .5
        else 1.0

        moveDirection = moveDirection.lerp(targetPosition.normalize(), 0.4)

        entity.lookAt(target, 30.0f, 30.0f)
        entity.lookControl.setLookAt(targetPosition)
        entity.moveControl.setWantedPosition(targetPosition.x, targetPosition.y, targetPosition.z, 1.5)
        entity.moveControl.setWait()
        return true
    }

    fun moveRandomly() {
        randomMoveDelay++
        var testPos: Vec3 = Vec3.ZERO
        if ((!entity.getMoveControl().hasWanted() && entity.random.nextInt(reducedTickDelay(43)) == 0) || randomMoveDelay>=FORCE_RANDOM_MOVE_DELAY) {
            for (i in 0..2) {
                testPos = entity.eyePosition.add(
                    entity.random.nextDouble() * entity.random.nextInt(2),
                    entity.random.nextDouble() * entity.random.nextInt(1) - .5,
                    entity.random.nextDouble() * entity.random.nextInt(2)
                )
                if (!entity.level().isEmptyBlock(BlockPos.containing(testPos))) continue
                entity.moveControl.setWantedPosition(testPos.x, testPos.y, testPos.z, 0.25)
                break
            }
            randomMoveDelay = 0
        }
        if (entity.getMoveControl().hasWanted()) {
            val targetPos = Vec3(entity.moveControl.wantedX, entity.moveControl.wantedY, entity.moveControl.wantedZ)
            val vector = targetPos.subtract(entity.eyePosition)
            if (vector.length() < 0.2) entity.moveControl.setWait()
            entity.lookControl.setLookAt(targetPos)
            moveDirection = moveDirection.lerp(vector.normalize(), 0.1)
        }
    }

}