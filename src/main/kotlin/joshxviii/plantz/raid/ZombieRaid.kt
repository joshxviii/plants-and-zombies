package joshxviii.plantz.raid

import com.mojang.serialization.Codec
import joshxviii.plantz.PazTags.EntityTypes.ZOMBIE_RAIDERS
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.HolderGetter
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import net.minecraft.world.Difficulty
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.raid.Raid
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.level.block.entity.BannerPattern
import net.minecraft.world.level.block.entity.BannerPatternLayers
import net.minecraft.world.level.block.entity.BannerPatterns
import net.minecraft.world.level.levelgen.Heightmap

class ZombieRaid(
    center : BlockPos,
    difficulty: Difficulty
) : Raid(
    center,
    difficulty
) {
    companion object {
        public fun getBrainzBannerInstance(patternGetter: HolderGetter<BannerPattern>): ItemStack {
            val banner = ItemStack(Items.RED_BANNER)
            val patterns = BannerPatternLayers.Builder()
                .add(patternGetter.getOrThrow(BannerPatterns.MOJANG), DyeColor.WHITE)
                .add(patternGetter.getOrThrow(BannerPatterns.BRICKS), DyeColor.PINK)
                .add(patternGetter.getOrThrow(BannerPatterns.GLOBE), DyeColor.PINK)
                .add(patternGetter.getOrThrow(BannerPatterns.STRIPE_TOP), DyeColor.RED)
                .add(patternGetter.getOrThrow(BannerPatterns.STRIPE_BOTTOM), DyeColor.RED)
                .add(patternGetter.getOrThrow(BannerPatterns.CURLY_BORDER), DyeColor.RED)
                .build()
            banner.set<BannerPatternLayers>(DataComponents.BANNER_PATTERNS, patterns)
            banner.set<TooltipDisplay>(
                DataComponents.TOOLTIP_DISPLAY,
                TooltipDisplay.DEFAULT.withHidden(DataComponents.BANNER_PATTERNS, true)
            )
            banner.set<Component>(DataComponents.ITEM_NAME, Component.translatable("item.plantz.zombie_banner"))
            banner.set<Rarity>(DataComponents.RARITY, Rarity.UNCOMMON)
            return banner
        }
    }

    private val random = RandomSource.create()
    private var raidCooldownTicks = 0
    private val numGroups = 0
    var waveSpawnPos : BlockPos? = null
    var started : Boolean = false
    private var raidStatus: ZombieRaidStatus? = null

    enum class ZombieRaidStatus(private val state: String) : StringRepresentable {
        ONGOING("ongoing"),
        VICTORY("victory"),
        LOSS("loss"),
        STOPPED("stopped");

        override fun getSerializedName(): String = this.state

        companion object {
            val CODEC: Codec<ZombieRaidStatus> =
                StringRepresentable.fromEnum<ZombieRaidStatus> { entries.toTypedArray() }
        }
    }

    override fun tick(level: ServerLevel) {
        if (!this.isStopped) {

            when (raidStatus) {
                ZombieRaidStatus.ONGOING -> {
                    var attempt = 0
                    while (this.shouldSpawnGroup()) {
                        val spawnPos = waveSpawnPos ?: this.findRandomSpawnPos(level, 20)
                        if (spawnPos != null) {
                            started = true
                            this.spawnGroup(level, spawnPos)
                        } else attempt++

                        if (attempt > 5) {
                            this.stop()
                            break
                        }
                    }
                }
                ZombieRaidStatus.VICTORY -> {}
                ZombieRaidStatus.LOSS -> {}
                ZombieRaidStatus.STOPPED -> {}
                null -> TODO()
            }
        }
    }

    private fun shouldSpawnGroup(): Boolean {
        return this.raidCooldownTicks == 0 && (this.groupsSpawned < this.numGroups || this.totalRaidersAlive == 0)
    }

    private fun spawnGroup(level: ServerLevel, pos: BlockPos){
        val tag = BuiltInRegistries.ENTITY_TYPE.get(ZOMBIE_RAIDERS)
        val raiderType = if (!tag.isEmpty) tag.get().getRandomElement(level.random).get().value() else return

        raiderType.create(level, EntitySpawnReason.EVENT)
    }


    private fun findRandomSpawnPos(level: ServerLevel, maxTries: Int): BlockPos? {
        val secondsRemaining = this.raidCooldownTicks / 20
        val howFar = 0.22f * secondsRemaining - 0.24f
        val spawnPos = MutableBlockPos()
        val startAngle = this.random.nextFloat() * (Math.PI * 2).toFloat()

        for (i in 0..<maxTries) {
            val angle = startAngle + Math.PI.toFloat() * i / 8.0f
            val spawnX =
                this.center.x + Mth.floor(Mth.cos(angle.toDouble()) * 32.0f * howFar) + this.random.nextInt(3) * Mth.floor(
                    howFar
                )
            val spawnZ =
                this.center.z + Mth.floor(Mth.sin(angle.toDouble()) * 32.0f * howFar) + this.random.nextInt(3) * Mth.floor(
                    howFar
                )
            val spawnY = level.getHeight(Heightmap.Types.WORLD_SURFACE, spawnX, spawnZ)
            if (Mth.abs(spawnY - this.center.y) <= 96) {
                spawnPos.set(spawnX, spawnY, spawnZ)
                if (secondsRemaining <= 7) {
                    val delta = 10
                    if (level.hasChunksAt(
                            spawnPos.x - 10,
                            spawnPos.z - 10,
                            spawnPos.x + 10,
                            spawnPos.z + 10
                        )
                        && level.isPositionEntityTicking(spawnPos)
                    )
                        return spawnPos
                }
            }
        }
        return null
    }
}