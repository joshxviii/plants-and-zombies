package joshxviii.plantz.block.entity

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazWorldGen
import joshxviii.plantz.block.TimeMachineBlock
import joshxviii.plantz.block.TimeMachineState
import joshxviii.plantz.block.TimePortalBlock
import joshxviii.plantz.pazResource
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStack
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import java.util.function.Function

fun ServerLevel.getTimeMachineManager(): TimeMachineManager =
    server.overworld().dataStorage.computeIfAbsent(TimeMachineManager.TYPE)

class TimeMachineManager private constructor(
    private val links: MutableMap<BlockPos, TimeMachineLink> = mutableMapOf()
) : SavedData() {

    companion object {
        private val LINK_CODEC: Codec<TimeMachineLink> = RecordCodecBuilder.create(Function { instance ->
            instance.group(
                BlockPos.CODEC.fieldOf("pos").forGetter { it.pos },
                ItemStack.OPTIONAL_CODEC.fieldOf("battery").forGetter { it.battery },
                Direction.CODEC.fieldOf("facing").forGetter { it.facing },
                Codec.unboundedMap(Level.RESOURCE_KEY_CODEC, Codec.BOOL).fieldOf("members").forGetter { it.powered },
                Codec.INT.optionalFieldOf("use_fuel_ticks", 0).forGetter { it.useFuelTicks }
            ).apply(instance) { pos, battery, facing, members, ticks -> TimeMachineLink(pos, battery, facing, members.toMutableMap(), ticks) }
        })
        val CODEC: Codec<TimeMachineManager> = LINK_CODEC.listOf().fieldOf("machines").codec().xmap(
            { entries -> TimeMachineManager(entries.associateBy { it.pos }.toMutableMap()) },
            { manager -> manager.links.values.toList() }
        )
        val TYPE = SavedDataType(pazResource("time_machines"), ::TimeMachineManager, CODEC, DataFixTypes.LEVEL)

        const val BURN_FUEL_TICK = 38
    }

    private val removing = mutableSetOf<BlockPos>()

    internal class TimeMachineLink(
        val pos: BlockPos,
        var battery: ItemStack,
        val facing: Direction,
        val powered: MutableMap<ResourceKey<Level>, Boolean>,
        var useFuelTicks: Int = 0
    ) {
        var lastBattery: ItemStack = battery.copy()
    }

    internal fun register(machine: TimeMachineBlockEntity): TimeMachineLink {
        val level = machine.level as ServerLevel
        val link = links.getOrPut(machine.blockPos) {
            setDirty()
            TimeMachineLink(machine.blockPos, machine.legacyItem, machine.blockState.getValue(TimeMachineBlock.FACING), mutableMapOf())
        }
        if (!machine.legacyItem.isEmpty) {
            machine.legacyItem = ItemStack.EMPTY
            machine.setChanged()
        }
        if (level.dimension() !in link.powered) {
            link.powered[level.dimension()] = level.hasNeighborSignal(machine.blockPos)
            setDirty()
        }
        return link
    }

    fun changed() = setDirty()

    fun tick(server: MinecraftServer) {
        for (link in links.values.toList()) {
            val loaded = loadedMachines(server, link)
            if (loaded.isEmpty()) continue
            for (machine in loaded) {
                val level = machine.level as ServerLevel
                val powered = level.hasNeighborSignal(link.pos)
                if (link.powered.put(level.dimension(), powered) != powered) setDirty()
            }
            val sun = link.battery.get(PazComponents.STORED_SUN)
            if (sun?.hasSun() == true && link.powered.containsValue(true)) {
                link.useFuelTicks++
                if (link.useFuelTicks >= BURN_FUEL_TICK) {
                    link.useFuelTicks = 0
                    link.battery.set(PazComponents.STORED_SUN, sun.removeSun())
                }
                setDirty()
            } else if (link.useFuelTicks != 0) {
                link.useFuelTicks = 0
                setDirty()
            }
            if (!ItemStack.matches(link.battery, link.lastBattery)) {
                link.lastBattery = link.battery.copy()
                setDirty()
            }
            loaded.forEach { applyState(it, link) }
        }
    }

    private fun loadedMachines(server: MinecraftServer, link: TimeMachineLink): List<TimeMachineBlockEntity> =
        link.powered.keys.toList().mapNotNull { dimension ->
            val level = server.getLevel(dimension) ?: return@mapNotNull null
            if (!level.hasChunkAt(link.pos)) return@mapNotNull null
            (level.getBlockEntity(link.pos) as? TimeMachineBlockEntity)?.takeUnless { it.isRemoved }
        }

    private fun applyState(machine: TimeMachineBlockEntity, link: TimeMachineLink) {
        val sun = link.battery.get(PazComponents.STORED_SUN)
        val state = when {
            sun == null -> TimeMachineState.INACTIVE
            sun.hasSun() && link.powered.containsValue(true) -> TimeMachineState.ACTIVE
            else -> TimeMachineState.BATTERY
        }
        machine.applySharedState(state, sun?.getLevel() ?: 0, link.facing)
    }

    fun createDestination(source: TimeMachineBlockEntity, destination: ServerLevel): Boolean {
        val link = register(source)
        val pos = link.pos
        val portalPos = pos.above(2)
        if (!destination.isInWorldBounds(pos) || !destination.isInWorldBounds(portalPos) ||
            !destination.worldBorder.isWithinBounds(pos)) return false
        destination.getChunkAt(pos)
        val existing = destination.getBlockState(pos)
        val portal = destination.getBlockState(portalPos)
        if (!existing.`is`(PazBlocks.TIME_MACHINE) && !existing.canBeReplaced()) return false
        if (!portal.`is`(PazBlocks.TIME_PORTAL) && !portal.canBeReplaced()) return false

        if (!existing.`is`(PazBlocks.TIME_MACHINE)) {
            if (!destination.setBlockAndUpdate(pos, source.blockState)) return false
        }
        val machine = destination.getBlockEntity(pos) as? TimeMachineBlockEntity ?: return false
        register(machine)
        applyState(machine, link)
        val sourceLevel = source.level ?: return false
        val sourcePortal = sourceLevel.getBlockState(portalPos)
        if (!sourcePortal.`is`(PazBlocks.TIME_PORTAL)) return false
        destination.setBlockAndUpdate(portalPos, sourcePortal.setValue(TimePortalBlock.FACING, link.facing))
        return destination.getBlockState(portalPos).`is`(PazBlocks.TIME_PORTAL)
    }

    fun remove(machine: TimeMachineBlockEntity): ItemStack {
        val level = machine.level as ServerLevel
        val pos = machine.blockPos
        if (pos in removing) return ItemStack.EMPTY
        val link = register(machine)
        removing.add(pos)
        try {
            removePortal(level, pos)
            link.powered.remove(level.dimension())
            val drop = if (PazWorldGen.isTimeDimension(level.dimension())) ItemStack.EMPTY else link.battery
            if (!PazWorldGen.isTimeDimension(level.dimension())) link.battery = ItemStack.EMPTY
            if (level.dimension() == Level.OVERWORLD) {
                for (dimension in link.powered.keys.toList().filter(PazWorldGen::isTimeDimension)) {
                    val target = level.server.getLevel(dimension) ?: continue
                    target.getChunkAt(pos)
                    if (target.getBlockState(pos).`is`(PazBlocks.TIME_MACHINE)) {
                        target.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
                    }
                    removePortal(target, pos)
                    link.powered.remove(dimension)
                }
            }
            if (link.powered.isEmpty()) links.remove(pos)
            setDirty()
            return drop
        } finally {
            removing.remove(pos)
        }
    }

    private fun removePortal(level: ServerLevel, pos: BlockPos) {
        if (level.getBlockState(pos.above(2)).`is`(PazBlocks.TIME_PORTAL)) {
            level.setBlockAndUpdate(pos.above(2), Blocks.AIR.defaultBlockState())
        }
    }
}
