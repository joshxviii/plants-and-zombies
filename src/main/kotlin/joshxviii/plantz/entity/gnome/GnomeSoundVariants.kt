package joshxviii.plantz.entity.gnome

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.PazEntities
import joshxviii.plantz.PazSounds
import joshxviii.plantz.pazResource
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.RegistryFixedCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.sounds.SoundEvent
import net.minecraft.util.RandomSource

object GnomeSoundVariants {
    enum class SoundSet(val id: String) {
        VOICE_1("gnome_voice_1"),
        VOICE_2("gnome_voice_2"),
        VOICE_3("gnome_voice_3"),
        VOICE_4("gnome_voice_4"),
        VOICE_5("gnome_voice_5"),
        VOICE_6("gnome_voice_6"),
        VOICE_7("gnome_voice_7");

        val resourceKey: ResourceKey<GnomeSoundVariant> = ResourceKey.create(PazEntities.GNOME_SOUND_VARIANT, pazResource(id))
    }

    fun bootstrap(context: BootstrapContext<GnomeSoundVariant>) {
        SoundSet.entries.forEach { soundSet ->
            val key = soundSet.resourceKey
            val variant = PazSounds.GNOME_SOUNDS[soundSet] ?: error("Missing sound variant for $soundSet")

            context.register(key, variant)
        }
    }

    fun pickRandomSoundVariant(registryAccess: RegistryAccess, random: RandomSource): Holder<GnomeSoundVariant> {
        return registryAccess.lookupOrThrow<GnomeSoundVariant>(PazEntities.GNOME_SOUND_VARIANT).getRandom(random).orElseThrow() as Holder<GnomeSoundVariant>
    }
}

@JvmRecord
data class GnomeSoundVariant(val sounds: GnomeSoundSet) {
    @JvmRecord
    data class GnomeSoundSet(
        val ambientSound: Holder<SoundEvent>,
        val deathSound: Holder<SoundEvent>,
        val hurtSound: Holder<SoundEvent>,
        val stepSound: Holder<SoundEvent>
    ) {
        companion object {
            val CODEC: Codec<GnomeSoundSet> = RecordCodecBuilder.create<GnomeSoundSet> {
                it.group(
                    SoundEvent.CODEC.fieldOf("ambient_sound").forGetter<GnomeSoundSet>(GnomeSoundSet::ambientSound),
                    SoundEvent.CODEC.fieldOf("death_sound").forGetter<GnomeSoundSet>(GnomeSoundSet::deathSound),
                    SoundEvent.CODEC.fieldOf("hurt_sound").forGetter<GnomeSoundSet>(GnomeSoundSet::hurtSound),
                    SoundEvent.CODEC.fieldOf("step_sound").forGetter<GnomeSoundSet>(GnomeSoundSet::stepSound)
                ).apply(it, ::GnomeSoundSet)
            }
        }
    }

    companion object {
        val DIRECT_CODEC: Codec<GnomeSoundVariant> = GnomeSoundVariantCodec
        val NETWORK_CODEC: Codec<GnomeSoundVariant> = GnomeSoundVariantCodec
        val CODEC: Codec<Holder<GnomeSoundVariant>> = RegistryFixedCodec.create<GnomeSoundVariant>(PazEntities.GNOME_SOUND_VARIANT)
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Holder<GnomeSoundVariant>> = ByteBufCodecs.holderRegistry<GnomeSoundVariant>(PazEntities.GNOME_SOUND_VARIANT)

        private val GnomeSoundVariantCodec: Codec<GnomeSoundVariant>
            get() = RecordCodecBuilder.create<GnomeSoundVariant> {
                it.group(
                    GnomeSoundSet.CODEC.fieldOf("sounds").forGetter<GnomeSoundVariant>(GnomeSoundVariant::sounds)
                ).apply(it, ::GnomeSoundVariant)
            }
    }
}