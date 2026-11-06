package joshxviii.plantz.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import joshxviii.plantz.PazPoiTypes.PLANTZ_FLAG
import joshxviii.plantz.effect.ZombieOmenMobEffect
import joshxviii.plantz.raid.BossType
import joshxviii.plantz.raid.WaveType
import joshxviii.plantz.raid.ZombieRaid
import joshxviii.plantz.raid.getZombieRaids
import joshxviii.plantz.networking.OpenOptionsScreenPayload
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.world.entity.ai.village.poi.PoiManager
import java.net.URI

object PazCommands {
    const val HELP_URL = "https://discord.gg/xxUrJuFJUz"

    private val NO_FLAG = SimpleCommandExceptionType(Component.translatable("commands.plantz.raid.start.no_flag"))
    private val RAID_NOT_STARTED = SimpleCommandExceptionType(Component.translatable("commands.plantz.raid.start.failed"))
    private val RAID_ALREADY_ACTIVE = SimpleCommandExceptionType(Component.translatable("commands.plantz.raid.start.already_active"))
    private val NO_RAID = SimpleCommandExceptionType(Component.translatable("commands.plantz.raid.no_raid_nearby"))
    private val RAID_INACTIVE = SimpleCommandExceptionType(Component.translatable("commands.plantz.raid.wave_start.not_ongoing"))
    private val FINAL_WAVE = SimpleCommandExceptionType(Component.translatable("commands.plantz.raid.wave_start.final_wave"))
    private val WAVE_FAILED = SimpleCommandExceptionType(Component.translatable("commands.plantz.raid.wave_start.failed"))
    private val UNKNOWN_WAVE_TYPE = DynamicCommandExceptionType { Component.translatable("commands.plantz.raid.wave_start.unknown_wave_type", it) }
    private val UNKNOWN_BOSS_TYPE = DynamicCommandExceptionType { Component.translatable("commands.plantz.raid.wave_start.unknown_boss_type", it) }

    fun initialize() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ -> register(dispatcher) }
    }

    private fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root: LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("pz")
            .then(Commands.literal("help").executes(::help))
            .then(Commands.literal("options").executes(::options))
            .then(
                Commands.literal("raid")
                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                    .then(
                        Commands.literal("start")
                            .executes { startRaid(it, 0) }
                            .then(
                                Commands.argument("wave_count", IntegerArgumentType.integer(1, ZombieRaid.MAXIMUM_WAVE_COUNT))
                                    .executes { startRaid(it, IntegerArgumentType.getInteger(it, "wave_count")) }
                            )
                    )
                    .then(Commands.literal("stop").executes(::stopRaid))
                    .then(
                        Commands.literal("wave_start")
                            .executes { startWave(it, null, null) }
                            .then(
                                Commands.argument("wave_type", StringArgumentType.word())
                                    .suggests { _, builder -> SharedSuggestionProvider.suggest(WaveType.entries.map { it.serializedName }, builder) }
                                    .executes { startWave(it, parseWaveType(it), null) }
                                    .then(
                                        Commands.argument("boss_type", StringArgumentType.word())
                                            .suggests { _, builder -> SharedSuggestionProvider.suggest(BossType.entries.map { it.serializedName }, builder) }
                                            .executes { startWave(it, parseWaveType(it), parseBossType(it)) }
                                    )
                            )
                    )
            )
        dispatcher.register(root)
    }

    private fun help(ctx: CommandContext<CommandSourceStack>): Int {
        val link = Component.literal(HELP_URL).withStyle(
            Style.EMPTY
                .withColor(ChatFormatting.AQUA)
                .withUnderlined(true)
                .withClickEvent(ClickEvent.OpenUrl(URI.create(HELP_URL)))
                .withHoverEvent(HoverEvent.ShowText(Component.translatable("commands.plantz.help.hover")))
        )
        ctx.source.sendSuccess({ Component.translatable("commands.plantz.help", link) }, false)
        return 1
    }

    private fun options(ctx: CommandContext<CommandSourceStack>): Int {
        ServerPlayNetworking.send(ctx.source.playerOrException, OpenOptionsScreenPayload)
        return 1
    }

    private fun startRaid(ctx: CommandContext<CommandSourceStack>, waveCount: Int): Int {
        val player = ctx.source.playerOrException
        val level = player.level()
        val raids = level.getZombieRaids()
        if (raids.getNearbyRaid(player.blockPosition(), ZombieRaid.RAID_DISTANCE) != null) throw RAID_ALREADY_ACTIVE.create()

        val raid = raids.createOrExtendZombieRaid(player, waveCount) ?: run {
            val flagNearby = level.poiManager.findClosest(
                { it.`is`(PLANTZ_FLAG) },
                player.blockPosition(),
                ZombieOmenMobEffect.MAX_FLAG_DISTANCE,
                PoiManager.Occupancy.HAS_SPACE
            ).isPresent
            throw if (flagNearby) RAID_NOT_STARTED.create() else NO_FLAG.create()
        }
        ctx.source.sendSuccess({ Component.translatable("commands.plantz.raid.start.success", raid.center.x, raid.center.y, raid.center.z) }, true)
        return 1
    }

    private fun stopRaid(ctx: CommandContext<CommandSourceStack>): Int {
        val player = ctx.source.playerOrException
        val level = player.level()
        val raid = level.getZombieRaids().getNearbyRaid(player.blockPosition(), ZombieRaid.RAID_DISTANCE) ?: throw NO_RAID.create()
        raid.stop(level)
        level.getZombieRaids().setDirty()
        ctx.source.sendSuccess({ Component.translatable("commands.plantz.raid.stop.success", raid.center.x, raid.center.y, raid.center.z) }, true)
        return 1
    }

    private fun startWave(ctx: CommandContext<CommandSourceStack>, waveType: WaveType?, bossType: BossType?): Int {
        val player = ctx.source.playerOrException
        val level = player.level()
        val raids = level.getZombieRaids()
        val raid = raids.getNearbyRaid(player.blockPosition(), ZombieRaid.RAID_DISTANCE) ?: throw NO_RAID.create()
        if (!raid.isActive()) throw RAID_INACTIVE.create()
        if (raid.isFinalWave()) throw FINAL_WAVE.create()

        // spawnNextWave only spawns while ONGOING, so skip the pre-wave cooldown
        raid.status = ZombieRaid.ZombieRaidStatus.ONGOING
        raid.raidCooldownTicks = ZombieRaid.PRE_RAID_TICKS
        val before = raid.currentWaveNumber()
        raid.spawnNextWave(level, waveType, bossType)
        if (raid.currentWaveNumber() == before) throw WAVE_FAILED.create()
        raids.setDirty()

        return raid.currentWaveNumber()
    }

    private fun parseWaveType(ctx: CommandContext<CommandSourceStack>): WaveType {
        val name = StringArgumentType.getString(ctx, "wave_type")
        return WaveType.entries.firstOrNull { it.serializedName.equals(name, true) } ?: throw UNKNOWN_WAVE_TYPE.create(name)
    }

    private fun parseBossType(ctx: CommandContext<CommandSourceStack>): BossType {
        val name = StringArgumentType.getString(ctx, "boss_type")
        return BossType.entries.firstOrNull { it.serializedName.equals(name, true) } ?: throw UNKNOWN_BOSS_TYPE.create(name)
    }
}
