package joshxviii.plantz

import com.mojang.serialization.Codec
import joshxviii.plantz.item.component.BrainzAlloyCost
import joshxviii.plantz.item.component.LetterDataComponent
import joshxviii.plantz.item.component.ProtectsHeadDataComponent
import joshxviii.plantz.item.component.StoredSun
import joshxviii.plantz.item.component.StoredWater
import joshxviii.plantz.item.component.SunCost
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import java.util.function.UnaryOperator

object PazComponents {

    @JvmField
    val SUN_COST: DataComponentType<SunCost> = register(
        "sun_cost"
    ) { it.persistent(SunCost.CODEC)
        .networkSynchronized(SunCost.STREAM_CODEC)
        .cacheEncoding()
    }

    @JvmField
    val BRAINZ_ALLOY_COST: DataComponentType<BrainzAlloyCost> = register(
        "brainz_alloy_cost"
    ) { it.persistent(BrainzAlloyCost.CODEC)
        .networkSynchronized(BrainzAlloyCost.STREAM_CODEC)
        .cacheEncoding()
    }

    @JvmField
    val STORED_WATER: DataComponentType<StoredWater> = register(
        "stored_water"
    ) { it.persistent(StoredWater.CODEC)
        .networkSynchronized(StoredWater.STREAM_CODEC)
        .cacheEncoding()
    }

    @JvmField
    val STORED_SUN: DataComponentType<StoredSun> = register(
        "stored_sun"
    ) { it.persistent(StoredSun.CODEC)
        .networkSynchronized(StoredSun.STREAM_CODEC)
        .cacheEncoding()
    }

    @JvmField
    val PROTECTS_HEAD: DataComponentType<ProtectsHeadDataComponent> = register(
        "protects_head"
    ) { it.persistent(ProtectsHeadDataComponent.CODEC)
        .networkSynchronized(ProtectsHeadDataComponent.STREAM_CODEC)
        .cacheEncoding()
    }

    @JvmField
    val BLOCKING_ITEM_BREAK_CHANCE: DataComponentType<Float> = register(
        "blocking_item_break_chance"
    ) { it.persistent(Codec.floatRange(0.0f, 1.0f))
        .networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.FLOAT)
        .cacheEncoding()
    }

    @JvmField
    val LETTER_DATA: DataComponentType<LetterDataComponent> = register(
        "letter_data"
    ) { it.persistent(LetterDataComponent.CODEC)
        .networkSynchronized(LetterDataComponent.STREAM_CODEC)
        .cacheEncoding()
    }

    private fun <T : Any> register(name: String, builder: UnaryOperator<DataComponentType.Builder<T>>): DataComponentType<T> {
        val key = ResourceKey.create(Registries.DATA_COMPONENT_TYPE, pazResource(name))
        return Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            key,
            builder.apply(DataComponentType.builder()).build()
        )
    }

    fun initialize() {}
}