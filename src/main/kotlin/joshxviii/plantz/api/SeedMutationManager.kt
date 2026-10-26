package joshxviii.plantz.api

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.PazRegistries
import joshxviii.plantz.item.SeedPacketItem
import joshxviii.plantz.pazResource
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.RegistryCodecs
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

/**
 * Data driven seed mutation manager.
 */
object SeedMutationManager {
    fun get(level: ServerLevel, type: EntityType<*>): SeedMutationData? {
        val entityId = BuiltInRegistries.ENTITY_TYPE.getKey(type)
        val key = ResourceKey.create(PazRegistries.SEED_MUTATION, entityId)
        val t = level.registryAccess().lookupOrThrow(PazRegistries.SEED_MUTATION)
        val p = t.getValue(key)

        return p
    }

    fun resolve(entity: Entity): ItemStack {
        val default = SeedPacketItem.stackFor(entity.type)
        val level = entity.level() as? ServerLevel ?: return default
        val data = get(level, entity.type) ?: return default
        val pos = entity.blockPosition()
        val random = entity.random

        for (rule in data.mutations) {
            if (rule.matches(level, pos, random)) {
                return when (val result = rule.result) {
                    is SeedMutationResult.EntityTypeResult -> SeedPacketItem.stackFor(result.entityType.value())
                    is SeedMutationResult.ItemStackResult ->
                        ItemStack(result.item.value(), result.count.roll(random))
                }
            }
        }
        return default
    }
}

@JvmRecord
data class SeedMutationData(val mutations: List<SeedMutationRule>) {
    companion object {
        val CODEC: Codec<SeedMutationData> = RecordCodecBuilder.create { inst ->
            inst.group(
                SeedMutationRule.CODEC.listOf().fieldOf("mutations").forGetter { it.mutations }
            ).apply(inst, ::SeedMutationData)
        }
    }
}

@JvmRecord
data class SeedMutationRule(
    val chance: Float = 1f,
    val biomes: Optional<HolderSet<Biome>> = Optional.empty(),
    val weather: Optional<WeatherPredicate> = Optional.empty(),
    val time: Optional<TimePredicate> = Optional.empty(),
    val result: SeedMutationResult,
) {
    fun matches(level: ServerLevel, pos: BlockPos, random: RandomSource): Boolean {
        if (random.nextFloat() >= chance) return false

        weather.getOrNull()?.let {
            if (!it.matches(level)) return false
        }

        biomes.getOrNull()?.let {
            val biome = level.getBiome(pos)
            if (!it.contains(biome)) return false
        }

        time.getOrNull()?.let {
            if (!it.matches(level)) return false
        }

        return true
    }

    companion object {
        val CODEC: Codec<SeedMutationRule> = RecordCodecBuilder.create { inst ->
            inst.group(
                Codec.FLOAT.optionalFieldOf("chance", 1f).forGetter { it.chance },
                RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biome").forGetter { it.biomes },
                WeatherPredicate.CODEC.optionalFieldOf("weather").forGetter { it.weather },
                TimePredicate.CODEC.optionalFieldOf("time").forGetter { it.time },
                SeedMutationResult.CODEC.fieldOf("result").forGetter { it.result },
            ).apply(inst, ::SeedMutationRule)
        }
    }
}

sealed interface SeedMutationResult {
    data class EntityTypeResult(val entityType: Holder<EntityType<*>>) : SeedMutationResult
    data class ItemStackResult(val item: Holder<Item>, val count: CountRange) : SeedMutationResult {
        companion object {
            val CODEC: Codec<ItemStackResult> = RecordCodecBuilder.create { inst ->
                inst.group(
                    BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("item").forGetter(ItemStackResult::item),
                    CountRange.CODEC.optionalFieldOf("count", CountRange(1, 1)).forGetter(ItemStackResult::count),
                ).apply(inst, ::ItemStackResult)
            }
        }
    }

    companion object {
        val CODEC: Codec<SeedMutationResult> = Codec.either(
            BuiltInRegistries.ENTITY_TYPE.holderByNameCodec(),
            ItemStackResult.CODEC,
        ).xmap(
            { result ->
                result.map({ EntityTypeResult(it) }, { it })
            },
            { result ->
                when (result) {
                    is EntityTypeResult -> com.mojang.datafixers.util.Either.left(result.entityType)
                    is ItemStackResult -> com.mojang.datafixers.util.Either.right(result)
                }
            }
        )
    }
}

@JvmRecord
data class CountRange(val min: Int, val max: Int) {
    fun roll(random: RandomSource): Int = random.nextIntBetweenInclusive(min, max)

    companion object {
        private val RANGE_CODEC: Codec<CountRange> = RecordCodecBuilder.create { inst ->
            inst.group(
                Codec.intRange(1, Int.MAX_VALUE).fieldOf("min").forGetter(CountRange::min),
                Codec.intRange(1, Int.MAX_VALUE).fieldOf("max").forGetter(CountRange::max),
            ).apply(inst, ::CountRange)
        }.flatXmap(
            { range ->
                if (range.min <= range.max) DataResult.success(range)
                else DataResult.error { "Count range minimum must not exceed maximum" }
            },
            { DataResult.success(it) },
        )

        val CODEC: Codec<CountRange> = Codec.either(
            Codec.intRange(1, Int.MAX_VALUE),
            RANGE_CODEC,
        ).xmap(
            { count -> count.map({ CountRange(it, it) }, { it }) },
            { count ->
                if (count.min == count.max) com.mojang.datafixers.util.Either.left(count.min)
                else com.mojang.datafixers.util.Either.right(count)
            },
        )
    }
}

enum class WeatherPredicate {
    CLEAR, RAIN, THUNDER;

    fun matches(level: Level): Boolean = when (this) {
        CLEAR -> !level.isRaining
        RAIN -> level.isRaining && !level.isThundering
        THUNDER -> level.isThundering
    }

    companion object {
        val CODEC: Codec<WeatherPredicate> = Codec.STRING.xmap(
            { valueOf(it.uppercase()) },
            { it.name.lowercase() }
        )
    }
}

sealed interface TimePredicate {
    fun matches(level: Level): Boolean

    data class Named(val day: Boolean) : TimePredicate {
        override fun matches(level: Level) = if (day) level.isBrightOutside else level.isDarkOutside
    }

    data class Range(val min: Int, val max: Int) : TimePredicate {
        override fun matches(level: Level): Boolean {
            val t = (level.overworldClockTime % 24000L).toInt()
            return if (min <= max) t in min..max else t !in (max + 1)..<min
        }
    }

    companion object {
        val CODEC: Codec<TimePredicate> = Codec.either(
            Codec.STRING.xmap(
                { s -> Named(s.equals("day", true)) },
                { if (it.day) "day" else "night" }
            ),
            RecordCodecBuilder.create { inst ->
                inst.group(
                    Codec.INT.fieldOf("min").forGetter(Range::min),
                    Codec.INT.fieldOf("max").forGetter(Range::max),
                ).apply(inst, ::Range)
            }
        ).xmap(
            { it.map({ n -> n }, { r -> r }) },
            { p -> when (p) {
                is Named -> com.mojang.datafixers.util.Either.left(p)
                is Range -> com.mojang.datafixers.util.Either.right(p)
            }}
        )
    }
}