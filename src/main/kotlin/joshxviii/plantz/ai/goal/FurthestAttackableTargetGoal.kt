package joshxviii.plantz.ai.goal

import joshxviii.plantz.getFurthestEntities
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.target.TargetGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import java.util.*
import java.util.function.Predicate

open class FurthestAttackableTargetGoal<T : LivingEntity>(
    usingEntity: PathfinderMob,
    protected val targetType: Class<T>,
    randomInterval: Int,
    mustSee: Boolean,
    mustReach: Boolean,
    selector: TargetingConditions.Selector?
) : TargetGoal(usingEntity, mustSee, mustReach) {
    protected val randomInterval: Int = reducedTickDelay(randomInterval)
    protected var target: LivingEntity? = null
    protected val targetConditions: TargetingConditions

    init {
        this.setFlags(EnumSet.of<Flag>(Flag.TARGET))
        this.targetConditions = TargetingConditions.forCombat().range(this.followDistance).selector(selector)
    }

    override fun canUse(): Boolean {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false
        } else {
            this.findTarget()
            return this.target != null
        }
    }

    protected open fun getTargetSearchArea(followDistance: Double): AABB {
        return this.mob.boundingBox.inflate(followDistance, followDistance, followDistance)
    }

    protected fun findTarget() {
        val level = getServerLevel(this.mob)
        if (this.targetType != Player::class.java && this.targetType != ServerPlayer::class.java) {
            this.target = level.getFurthestEntities(
                this.mob.level().getEntitiesOfClass<T>(
                    this.targetType,
                    this.getTargetSearchArea(this.followDistance),
                    Predicate { entity: T? -> true }),
                this.targetConditions,
                this.mob,
                this.mob.x,
                this.mob.eyeY,
                this.mob.z
            )
        } else {
            this.target = level.getNearestPlayer(
                this.targetConditions,
                this.mob,
                this.mob.x,
                this.mob.eyeY,
                this.mob.z
            )
        }
    }

    override fun start() {
        this.mob.target = this.target
        super.start()
    }
}