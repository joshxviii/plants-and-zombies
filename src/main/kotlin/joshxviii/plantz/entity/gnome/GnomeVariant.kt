package joshxviii.plantz.entity.gnome

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import joshxviii.plantz.pazResource
import net.minecraft.core.ClientAsset
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import java.util.function.IntFunction

/**
 * The Gnome Variants used for Textures.
 */
enum class GnomeVariant(val color: String, val id: Int) : StringRepresentable {
    BLUE("blue", 0),
    RED("red", 1),
    GREEN("green", 2),
    YELLOW("yellow", 3);

    override fun getSerializedName(): String = color
    fun getTexture(): Identifier = ClientAsset.ResourceTexture(pazResource("entity/gnome/${color}")).texturePath!!

    companion object {
        fun getDefault(): GnomeVariant = BLUE
        fun pickRandomVariant(): GnomeVariant = entries.random()

        val CODEC: Codec<GnomeVariant> = StringRepresentable.fromEnum(GnomeVariant::values)
        private val BY_ID: IntFunction<GnomeVariant> = ByIdMap.continuous(GnomeVariant::id, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO);
        val STREAM_CODEC: StreamCodec<ByteBuf, GnomeVariant> = ByteBufCodecs.idMapper<GnomeVariant>(BY_ID, GnomeVariant::id)
    }
}