package joshxviii.plantz.entity.gnome

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import joshxviii.plantz.PazSounds
import net.minecraft.core.Holder
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.sounds.SoundEvent
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import java.util.function.IntFunction

/**
 * Each voice variant maps to a pre-determined set of sounds.
 */
enum class GnomeSoundVariant(val variant: String, val id: Int, ) : StringRepresentable {
    VOICE_1("voice_1", 0),
    VOICE_2("voice_2", 1),
    VOICE_3("voice_3", 2),
    VOICE_4("voice_4", 3),
    VOICE_5("voice_5", 4),
    VOICE_6("voice_6", 5),
    VOICE_7("voice_7", 6);

    override fun getSerializedName(): String = variant

    fun getSoundSet(): GnomeSoundSet = PazSounds.GNOME_SOUNDS[this]!!

    companion object {

        fun pickRandomVariant(): GnomeSoundVariant = entries.random()

        val CODEC: Codec<GnomeSoundVariant> = StringRepresentable.fromEnum(::values)
        private val BY_ID: IntFunction<GnomeSoundVariant> = ByIdMap.continuous(GnomeSoundVariant::id, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO)
        val STREAM_CODEC: StreamCodec<ByteBuf, GnomeSoundVariant> = ByteBufCodecs.idMapper(BY_ID, GnomeSoundVariant::id)
    }

    /**
     * Sound definitions. Assigned in: [PazSounds.registerGnomeSoundVariants]
     */
    data class GnomeSoundSet(
        val ambientSound: Holder<SoundEvent>,
        val deathSound: Holder<SoundEvent>,
        val hurtSound: Holder<SoundEvent>,
        val jumpSound: Holder<SoundEvent>,
        val stepSound: Holder<SoundEvent>
    )
}