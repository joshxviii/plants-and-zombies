package joshxviii.plantz.entity.gnome

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.PazEntities
import joshxviii.plantz.pazResource
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.RegistryFixedCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.util.RandomSource
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.variant.*


object GnomeVariants {

    fun bootstrap(context: BootstrapContext<GnomeVariant>) {
        GnomeVariant.ModelType.entries.forEach {
            val key = it.resourceKey
            val texture = pazResource("entity/gnome/${it.color}")
            context.register(
                key, GnomeVariant(ModelAndTexture(it, texture), /*ADD BIOME SELECTORS HERE (SEE [CowVariant])*/)
            )
        }
    }

    fun pickRandomVariant(registryAccess: RegistryAccess, random: RandomSource): Holder<GnomeVariant> {
        return registryAccess.lookupOrThrow<GnomeVariant>(PazEntities.GNOME_VARIANT).getRandom(random).orElseThrow() as Holder<GnomeVariant>
    }

}

@JvmRecord
data class GnomeVariant(
    val modelAndTexture: ModelAndTexture<ModelType>,
    val spawnConditions: SpawnPrioritySelectors = SpawnPrioritySelectors.EMPTY
) : PriorityProvider<SpawnContext, SpawnCondition> {

    override fun selectors(): MutableList<PriorityProvider.Selector<SpawnContext, SpawnCondition>> {
        return this.spawnConditions.selectors()
    }

    enum class ModelType(val color: String) : StringRepresentable {
        BLUE("blue"),
        RED("red"),
        GREEN("green"),
        YELLOW("yellow"),
        GOLDEN("golden");

        override fun getSerializedName(): String = this.color
        val resourceKey: ResourceKey<GnomeVariant> = ResourceKey.create(PazEntities.GNOME_VARIANT, pazResource(color))

        companion object {
            val CODEC: Codec<ModelType> = StringRepresentable.fromEnum<ModelType> { ModelType.entries.toTypedArray() }
        }
    }

    companion object {
        val DIRECT_CODEC: Codec<GnomeVariant> = RecordCodecBuilder.create<GnomeVariant> {
            it.group<ModelAndTexture<ModelType>, SpawnPrioritySelectors>(
                ModelAndTexture.codec<ModelType>(ModelType.CODEC, ModelType.BLUE).forGetter<GnomeVariant>(GnomeVariant::modelAndTexture),
                SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter<GnomeVariant>(GnomeVariant::spawnConditions)
            ).apply<GnomeVariant>(it) {
                modelAndTexture, spawnConditions -> GnomeVariant(modelAndTexture, spawnConditions)
            }
        }
        val NETWORK_CODEC: Codec<GnomeVariant> = RecordCodecBuilder.create<GnomeVariant> {
            it.group<ModelAndTexture<ModelType>>(
                ModelAndTexture.codec<ModelType>(ModelType.CODEC, ModelType.BLUE).forGetter<GnomeVariant>(GnomeVariant::modelAndTexture),
            ).apply<GnomeVariant>(it) {
                modelAndTexture -> GnomeVariant(modelAndTexture)
            }
        }
        val CODEC: Codec<Holder<GnomeVariant>> = RegistryFixedCodec.create<GnomeVariant>(PazEntities.GNOME_VARIANT)
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Holder<GnomeVariant>> = ByteBufCodecs.holderRegistry<GnomeVariant>(PazEntities.GNOME_VARIANT)
    }
}