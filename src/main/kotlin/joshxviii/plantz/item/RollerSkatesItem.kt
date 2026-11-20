package joshxviii.plantz.item

import joshxviii.plantz.PazItems
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.PazSounds
import joshxviii.plantz.PazTags
import joshxviii.plantz.pazResource
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.RailShape
import net.minecraft.world.phys.Vec3
import kotlin.math.min

class RollerSkatesItem(properties: Properties) : Item(properties) {
    companion object {
        fun addToTooltip(consumer: MutableList<Component>) {
            consumer.add(1, Component.translatable("item.plantz.roller_skates.description")
                .withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC))
        }
        val ROLLER_SKATES_ID = pazResource("roller_skates")
        private const val DAMAGE_INTERVAL = 35

        fun isSkating(entity: LivingEntity): Boolean {
            val hasSkates = entity.getItemBySlot(EquipmentSlot.FEET).`is`(PazItems.ROLLER_SKATES)
            if (!hasSkates) removeModifiers(entity)
            return !(entity is Player && (!entity.isSprinting || entity.isFallFlying || entity.isSwimming)) && hasSkates
        }

        fun isRailGrinding(entity: LivingEntity): Boolean {
           return RailGrinding.isGrinding(entity)
        }

        fun removeModifiers(entity: LivingEntity?) {
            entity?.getAttribute(Attributes.FRICTION_MODIFIER)?.removeModifier(ROLLER_SKATES_ID)
            entity?.getAttribute(Attributes.MOVEMENT_SPEED)?.removeModifier(ROLLER_SKATES_ID)
            entity?.getAttribute(Attributes.STEP_HEIGHT)?.removeModifier(ROLLER_SKATES_ID)
        }
    }
    override fun inventoryTick(itemStack: ItemStack, level: ServerLevel, owner: Entity, slot: EquipmentSlot?) {
        super.inventoryTick(itemStack, level, owner, slot)
        if (owner !is LivingEntity || level.isClientSide) return
        if (!owner.getItemBySlot(EquipmentSlot.FEET).`is`(PazItems.ROLLER_SKATES)) {
            removeModifiers(owner)
            return
        }
        if (!owner.isShiftKeyDown) {
            owner.getAttribute(Attributes.FRICTION_MODIFIER)?.let {
                if (!it.hasModifier(ROLLER_SKATES_ID)) it.addTransientModifier(
                    AttributeModifier(ROLLER_SKATES_ID, -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                )
            }
        }
        else owner.getAttribute(Attributes.FRICTION_MODIFIER)?.removeModifier(ROLLER_SKATES_ID)
        if (isSkating(owner)) {
            owner.getAttribute(Attributes.MOVEMENT_SPEED)?.let {
                if (!it.hasModifier(ROLLER_SKATES_ID)) it.addTransientModifier(
                    AttributeModifier(ROLLER_SKATES_ID, 0.7, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                )
            }
            owner.getAttribute(Attributes.STEP_HEIGHT)?.let {
                if (!it.hasModifier(ROLLER_SKATES_ID)) it.addTransientModifier(
                    AttributeModifier(ROLLER_SKATES_ID, 0.5, AttributeModifier.Operation.ADD_VALUE)
                )
            }
        }
        else  {
            owner.getAttribute(Attributes.MOVEMENT_SPEED)?.removeModifier(ROLLER_SKATES_ID)
            owner.getAttribute(Attributes.STEP_HEIGHT)?.removeModifier(ROLLER_SKATES_ID)
        }
        if ((isRailGrinding(owner) || isSkating(owner)) && owner.tickCount % DAMAGE_INTERVAL == 0 && owner.getRandom().nextFloat() > 0.5f) {
            if (slot != null) itemStack.hurtAndBreak(1, owner, slot)
        }
    }
}

interface RailGrindHolder {
    fun `plantz$getGrindState`(): RailGrindState?
    fun `plantz$setGrindState`(value: RailGrindState?)
}

class RailGrindState(var dir: Direction, val speed: Double, var pos: BlockPos, var passedCenter: Boolean) {
    var ended = false
    var cooldown = 0
}

object RailGrinding {
    private class Conn(val dir: Direction, val up: Boolean)

    private const val MIN_START_SPEED = 0.08
    private const val MIN_GRIND_SPEED = 0.2
    private const val JUMP_SPEED_BOOST = 1.75
    private const val JUMP_LIFT_BOOST = 0.15
    private const val REGRIND_COOLDOWN = 10
    private const val EPS = 1e-7

    private fun conns(state: BlockState): List<Conn> {
        fun c(vararg d: Direction) = d.map { Conn(it, false) }
        val n = Direction.NORTH
        val e = Direction.EAST
        val s = Direction.SOUTH
        val w = Direction.WEST
        val shape: RailShape? = when {
            state.hasProperty(BlockStateProperties.RAIL_SHAPE) -> state.getValue(BlockStateProperties.RAIL_SHAPE)
            state.hasProperty(BlockStateProperties.RAIL_SHAPE_STRAIGHT) -> state.getValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT)
            else -> null
        }
        if (shape != null) return when (shape) {
            RailShape.NORTH_SOUTH -> c(n, s)
            RailShape.EAST_WEST -> c(e, w)
            RailShape.ASCENDING_EAST -> listOf(Conn(e, true), Conn(w, false))
            RailShape.ASCENDING_WEST -> listOf(Conn(w, true), Conn(e, false))
            RailShape.ASCENDING_NORTH -> listOf(Conn(n, true), Conn(s, false))
            RailShape.ASCENDING_SOUTH -> listOf(Conn(s, true), Conn(n, false))
            RailShape.SOUTH_EAST -> c(s, e)
            RailShape.SOUTH_WEST -> c(s, w)
            RailShape.NORTH_WEST -> c(n, w)
            RailShape.NORTH_EAST -> c(n, e)
        }
        if (state.hasProperty(BlockStateProperties.AXIS)) return when (state.getValue(BlockStateProperties.AXIS)) {
            Direction.Axis.X -> c(e, w)
            Direction.Axis.Z -> c(n, s)
            else -> emptyList()
        }
        if (state.hasProperty(BlockStateProperties.NORTH)) {
            return listOf(
                n to BlockStateProperties.NORTH, e to BlockStateProperties.EAST,
                s to BlockStateProperties.SOUTH, w to BlockStateProperties.WEST
            ).filter { (_, p) -> state.getValue(p) }.map { Conn(it.first, false) }
        }
        return c(n, e, s, w)
    }

    private fun isRail(state: BlockState) = state.hasProperty(BlockStateProperties.RAIL_SHAPE) || state.hasProperty(BlockStateProperties.RAIL_SHAPE_STRAIGHT)

    private fun findNext(entity: LivingEntity, pos: BlockPos, state: BlockState, d: Direction): BlockPos? {
        val level = entity.level()
        val entry = conns(state).firstOrNull { it.dir == d } ?: return null
        val offsets = mutableListOf(0)
        if (entry.up) offsets.add(1)
        if (isRail(state)) offsets.add(-1)
        for (dy in offsets) {
            val np = pos.relative(d).above(dy)
            val ns = level.getBlockState(np)
            if (ns.`is`(PazTags.BlockTags.RAIL_GRINDABLE) && level.getBlockState(np.above()).getCollisionShape(level, np.above()).isEmpty && conns(ns).any { it.dir == d.opposite }) return np
        }
        return null
    }

    private fun surfaceY(entity: LivingEntity, pos: BlockPos, state: BlockState, x: Double, z: Double): Double {
        val up = conns(state).firstOrNull { it.up }
        if (up != null && isRail(state)) {
            val along = (x - (pos.x + 0.5)) * up.dir.stepX + (z - (pos.z + 0.5)) * up.dir.stepZ
            return pos.y + 0.0625 + (0.8 + along).coerceIn(0.0, 1.0)
        }
        val h = state.getShape(entity.level(), pos).max(Direction.Axis.Y)
        return pos.y + (if (h.isFinite() && h > 0.0) h else 0.125)
    }

    private fun findStartPos(entity: LivingEntity): BlockPos? {
        if (entity.isShiftKeyDown || !RollerSkatesItem.isSkating(entity)) return null
        val level = entity.level()
        for (dy in doubleArrayOf(-0.1, 0.01)) {
            val pos = BlockPos.containing(entity.x, entity.y + dy, entity.z)
            val state = level.getBlockState(pos)
            if (!state.`is`(PazTags.BlockTags.RAIL_GRINDABLE) || conns(state).isEmpty()) continue
            val diff = entity.y - surfaceY(entity, pos, state, entity.x, entity.z)
            if (diff in -0.2..0.5) return pos
        }
        return null
    }

    @JvmStatic
    fun isGrinding(entity: LivingEntity): Boolean {
        val current = (entity as RailGrindHolder).`plantz$getGrindState`()
        if (current != null) return !current.ended
        return entity.deltaMovement.horizontalDistance() >= MIN_START_SPEED * 0.5 && findStartPos(entity) != null
    }

    private fun release(entity: LivingEntity, st: RailGrindState, y: Double = 0.0, speedScale: Double = 1.0) {
        val v = st.speed * speedScale

        entity.deltaMovement = Vec3(entity.lookAngle.x * v, y, entity.lookAngle.z * v)
        st.ended = true
        st.cooldown = REGRIND_COOLDOWN
    }

    private fun finish(entity: LivingEntity, holder: RailGrindHolder, st: RailGrindState, x: Double, z: Double): Boolean {
        val state = entity.level().getBlockState(st.pos)
        entity.setPos(x, surfaceY(entity, st.pos, state, x, z), z)
        release(entity, st)
        return false
    }

    @JvmStatic
    fun tick(entity: LivingEntity): Boolean {
        val holder = entity as RailGrindHolder
        val level = entity.level()
        var st = holder.`plantz$getGrindState`()
        val dm = entity.deltaMovement

        if (st != null && st.ended) {
            if (--st.cooldown > 0) return false
            holder.`plantz$setGrindState`(null)
            st = null
        }

        if (st == null) {
            if (dm.y > 0.05 || entity.isPassenger) return false
            val speed = dm.horizontalDistance()
            if (speed < MIN_START_SPEED) return false
            val pos = findStartPos(entity) ?: return false
            val state = level.getBlockState(pos)
            val best = conns(state).maxByOrNull { it.dir.stepX * dm.x + it.dir.stepZ * dm.z } ?: return false
            if (best.dir.stepX * dm.x + best.dir.stepZ * dm.z < speed * 0.35) return false
            val along = (entity.x - (pos.x + 0.5)) * best.dir.stepX + (entity.z - (pos.z + 0.5)) * best.dir.stepZ
            st = RailGrindState(best.dir, maxOf(speed, MIN_GRIND_SPEED), pos, along >= 0.0)
            holder.`plantz$setGrindState`(st)
            entity.playSound(PazSounds.ROLLER_SKATE_GRIND_START, 1.0f, 1.0f)
        } else if (dm.y > 0.1) {
            // jumped off the rail boost
            release(entity, st, dm.y + JUMP_LIFT_BOOST, JUMP_SPEED_BOOST)
            return false
        } else if (entity.isShiftKeyDown || !entity.getItemBySlot(EquipmentSlot.FEET).`is`(PazItems.ROLLER_SKATES)) {
            release(entity, st, dm.y)
            return false
        }

        var x = entity.x
        var z = entity.z
        var r = st.speed * 2
        var iterations = 0
        while (r > EPS && iterations++ < 16) {
            val d = st.dir
            val cx = st.pos.x + 0.5
            val cz = st.pos.z + 0.5
            val state = level.getBlockState(st.pos)
            if (!state.`is`(PazTags.BlockTags.RAIL_GRINDABLE)) {
                release(entity, st)
                return false
            }
            val pa = (x - cx) * d.stepX + (z - cz) * d.stepZ
            if (!st.passedCenter && pa >= 0.0) st.passedCenter = true
            if (!st.passedCenter) {
                val dist = -pa
                val m = min(r, dist)
                x += d.stepX * m
                z += d.stepZ * m
                r -= m
                if (m < dist - EPS) continue
                x = cx
                z = cz
                st.passedCenter = true
                val next = conns(state).filter { it.dir != d.opposite }
                    .sortedBy { if (it.dir == d) 0 else 1 }
                    .firstOrNull { findNext(entity, st.pos, state, it.dir) != null }
                    ?: return finish(entity, holder, st, x, z)
                st.dir = next.dir
            } else {
                val dist = 0.5 - pa
                val m = min(r, dist)
                x += d.stepX * m
                z += d.stepZ * m
                r -= m
                if (m >= dist - EPS) {
                    val np = findNext(entity, st.pos, state, d) ?: return finish(entity, holder, st, x, z)
                    st.pos = np
                    st.passedCenter = false
                }
            }
        }

        val d = st.dir
        if (d.axis == Direction.Axis.X) z = st.pos.z + 0.5 else x = st.pos.x + 0.5
        val y = surfaceY(entity, st.pos, level.getBlockState(st.pos), x, z)
        entity.setPos(x, y, z)
        entity.setDeltaMovement(d.stepX * st.speed, 0.0, d.stepZ * st.speed)
        entity.setOnGround(true)
        entity.resetFallDistance()
        level.addParticle(PazServerParticles.GRIND_SPARK, entity.getRandomX(0.4), entity.y, entity.getRandomZ(0.4), 0.0, 0.0, 0.0)
        return true
    }
}