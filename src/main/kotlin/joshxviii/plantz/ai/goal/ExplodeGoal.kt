package joshxviii.plantz.ai.goal

import joshxviii.plantz.entity.plant.Plant
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.level.gameevent.GameEvent
import java.util.*

class ExplodeGoal(
    private val plantEntity: Plant
) : Goal() {
    private var target: LivingEntity? = null
    private var oldSwell = 0
    private var swell = 0
    val maxSwell = 30
    var swellDir = 0

    init {
        this.setFlags(EnumSet.of<Flag>(Flag.MOVE))
    }

    override fun canUse(): Boolean {
        val target = this.plantEntity.target
        return target != null && !target.isDeadOrDying && this.plantEntity.distanceToSqr(
            target
        ) < 9.0
    }

    override fun start() {
        this.plantEntity.getNavigation().stop()
        this.target = this.plantEntity.target
    }

    override fun stop() {
        this.target = null
    }

    override fun requiresUpdateEveryTick(): Boolean {
        return true
    }

    override fun tick() {
        val target = this.target ?: return
        swellDir = if (!target.isDeadOrDying) {
            if (this.plantEntity.distanceToSqr(target) > 49.0) {
                -1
            } else if (!this.plantEntity.sensing.hasLineOfSight(target)) {
                -1
            } else {
                1
            }
        } else {
            -1
        }

        if (swellDir > 0 && this.swell == 0) {
            plantEntity.playSound(SoundEvents.CREEPER_PRIMED, 1.0f, 0.5f)
            plantEntity.gameEvent(GameEvent.PRIME_FUSE)
        }

        this.swell += swellDir
        if (this.swell < 0) {
            this.swell = 0
        }

        if (this.swell >= this.maxSwell) {
            this.swell = this.maxSwell
            plantEntity.discard()
        }
    }
}