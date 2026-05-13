package joshxviii.plantz

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.fabricmc.loader.impl.lib.sat4j.core.Vec
import net.minecraft.core.Registry
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.ScalableParticleOptionsBase
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ARGB
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.joml.Vector3f
import java.util.function.Function

object PazServerParticles {
    @JvmField val BUTTER_DRIP:SimpleParticleType = registerSimple("butter_drip")
    @JvmField val PEA_HIT: SimpleParticleType = registerSimple("pea")
    @JvmField val ICE_PEA_HIT: SimpleParticleType = registerSimple("pea_ice")
    @JvmField val FIRE_PEA_HIT: SimpleParticleType = registerSimple("pea_fire")
    @JvmField val HYPNO_SPORE: SimpleParticleType = registerSimple("hypno_spore")
    @JvmField val SPORE: SimpleParticleType = registerSimple("spore")
    @JvmField val SPORE_HIT: SimpleParticleType = registerSimple("spore_hit")
    @JvmField val FUME_BUBBLE: SimpleParticleType = registerSimple("fume_bubble")
    @JvmField val EMBER: SimpleParticleType = registerSimple("ember")
    @JvmField val ELECTRIFIED: SimpleParticleType = registerSimple("electrified")
    @JvmField val ENERGIZED: SimpleParticleType = registerSimple("energized")
    @JvmField val SLEEP: SimpleParticleType = registerSimple("sleep")
    @JvmField val NOTIFY: SimpleParticleType = registerSimple("notify")
    @JvmField val NEEDS_WATER: SimpleParticleType = registerSimple("needs_water")
    @JvmField val NEEDS_SUN: SimpleParticleType = registerSimple("needs_sun")
    @JvmField val NEEDS_TIME: SimpleParticleType = registerSimple("needs_time")
    @JvmField val ZOMBIE_OMEN: SimpleParticleType = registerSimple("zombie_omen")
    @JvmField val NUKE_WAVE: ParticleType<NukeWaveParticleOptions> = register("nuke_wave", { NukeWaveParticleOptions.CODEC}, {NukeWaveParticleOptions.STREAM_CODEC})
    @JvmField val NUKE_BLAST: ParticleType<NukeBlastParticleOptions> = register("nuke_blast", {NukeBlastParticleOptions.CODEC}, {NukeBlastParticleOptions.STREAM_CODEC})
    @JvmField val NUKE_SMOKE: ParticleType<NukeSmokeParticleOptions> = register("nuke_smoke", {NukeSmokeParticleOptions.CODEC}, {NukeSmokeParticleOptions.STREAM_CODEC})
    @JvmField val PAINT_BALL: ParticleType<PaintParticleOptions> = register("paint_ball", {PaintParticleOptions.CODEC}, {PaintParticleOptions.STREAM_CODEC})
    @JvmField val ELECTRIC_ARC: ParticleType<ElectricArcParticleOptions> = register("electric_arc", {ElectricArcParticleOptions.CODEC}, {ElectricArcParticleOptions.STREAM_CODEC})

    fun registerSimple(name: String): SimpleParticleType {
        val particleType = FabricParticleTypes.simple()
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, pazResource(name), particleType)
        return particleType
    }

    private fun <T : ParticleOptions> register(
        name: String,
        codec: Function<ParticleType<T>, MapCodec<T>>,
        streamCodec: Function<ParticleType<T>, StreamCodec<in RegistryFriendlyByteBuf, T>>
    ): ParticleType<T> {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, pazResource(name), object : ParticleType<T>(false) {
            override fun codec(): MapCodec<T> = codec.apply(this)

            override fun streamCodec(): StreamCodec<in RegistryFriendlyByteBuf, T> = streamCodec.apply(this)
        })
    }

    fun initialize() {}
}

class PaintParticleOptions(private val color: Int, scale: Float = 1f) : ScalableParticleOptionsBase(scale) {
    override fun getType(): ParticleType<PaintParticleOptions> {
        return PazServerParticles.PAINT_BALL
    }

    fun getColor(): Vector3f = ARGB.vector3fFromRGB24(this.color)

    companion object {
        val CODEC: MapCodec<PaintParticleOptions> = RecordCodecBuilder.mapCodec { i ->
                i.group(
                    ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter { it.color },
                    SCALE.fieldOf("scale").forGetter { it.scale }
                ).apply(i, ::PaintParticleOptions) }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, PaintParticleOptions> = StreamCodec.composite(
            ByteBufCodecs.INT, { it.color },
            ByteBufCodecs.FLOAT, { it.scale },
            ::PaintParticleOptions)
    }
}

class NukeWaveParticleOptions(private val color: Int = 0xFFFFFF, scale: Float = 1f) : ScalableParticleOptionsBase(scale) {
    override fun getType(): ParticleType<NukeWaveParticleOptions> = PazServerParticles.NUKE_WAVE
    fun getColor(): Vector3f = ARGB.vector3fFromRGB24(this.color)

    companion object {
        val CODEC: MapCodec<NukeWaveParticleOptions> = RecordCodecBuilder.mapCodec { i ->
            i.group(
                ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter { it.color },
                SCALE.fieldOf("scale").forGetter { it.scale }
            ).apply(i, ::NukeWaveParticleOptions) }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, NukeWaveParticleOptions> = StreamCodec.composite(
            ByteBufCodecs.INT, { it.color }, ByteBufCodecs.FLOAT, { it.scale }, ::NukeWaveParticleOptions)
    }
}

class NukeBlastParticleOptions(private val color: Int = 0xFFFFFF, scale: Float = 1f) : ScalableParticleOptionsBase(scale) {
    override fun getType(): ParticleType<NukeBlastParticleOptions> = PazServerParticles.NUKE_BLAST
    fun getColor(): Vector3f = ARGB.vector3fFromRGB24(this.color)

    companion object {
        val CODEC: MapCodec<NukeBlastParticleOptions> = RecordCodecBuilder.mapCodec { i ->
            i.group(
                ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter { it.color },
                SCALE.fieldOf("scale").forGetter { it.scale }
            ).apply(i, ::NukeBlastParticleOptions) }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, NukeBlastParticleOptions> = StreamCodec.composite(
            ByteBufCodecs.INT, { it.color }, ByteBufCodecs.FLOAT, { it.scale }, ::NukeBlastParticleOptions)
    }
}

class NukeSmokeParticleOptions(private val color: Int = 0xFFFFFF, scale: Float = 1f) : ScalableParticleOptionsBase(scale) {
    override fun getType(): ParticleType<NukeSmokeParticleOptions> = PazServerParticles.NUKE_SMOKE
    fun getColor(): Vector3f = ARGB.vector3fFromRGB24(this.color)

    companion object {
        val CODEC: MapCodec<NukeSmokeParticleOptions> = RecordCodecBuilder.mapCodec { i ->
            i.group(
                ExtraCodecs.RGB_COLOR_CODEC.fieldOf("color").forGetter { it.color },
                SCALE.fieldOf("scale").forGetter { it.scale }
            ).apply(i, ::NukeSmokeParticleOptions) }
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, NukeSmokeParticleOptions> = StreamCodec.composite(
            ByteBufCodecs.INT, { it.color }, ByteBufCodecs.FLOAT, { it.scale }, ::NukeSmokeParticleOptions)
    }
}

class ElectricArcParticleOptions(
    val targetPos: Vec3 = Vec3.ZERO,
    val color: Int = 0xAACCFF,
    val thickness: Float = 0.075f
) : ParticleOptions {

    override fun getType(): ParticleType<ElectricArcParticleOptions> = PazServerParticles.ELECTRIC_ARC

    companion object {
        val CODEC: MapCodec<ElectricArcParticleOptions> = RecordCodecBuilder.mapCodec { builder ->
            builder.group(
                Codec.DOUBLE.optionalFieldOf("tx", 0.0).forGetter { it.targetPos.x },
                Codec.DOUBLE.optionalFieldOf("ty", 0.0).forGetter { it.targetPos.y },
                Codec.DOUBLE.optionalFieldOf("tz", 0.0).forGetter { it.targetPos.z },
                ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("color", 0xAACCFF).forGetter { it.color },
                Codec.FLOAT.optionalFieldOf("thickness", 0.12f).forGetter { it.thickness }
            ).apply(builder
            ) { x, y, z, color, thickness -> ElectricArcParticleOptions(Vec3(x, y, z), color, thickness) }
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ElectricArcParticleOptions> =
            StreamCodec.composite(
                ByteBufCodecs.DOUBLE, { it.targetPos.x },
                ByteBufCodecs.DOUBLE, { it.targetPos.y },
                ByteBufCodecs.DOUBLE, { it.targetPos.z },
                ByteBufCodecs.INT, { it.color },
                ByteBufCodecs.FLOAT, { it.thickness },
                { x, y, z, color, thickness -> ElectricArcParticleOptions(Vec3(x,y,z), color, thickness) }
            )
    }
}