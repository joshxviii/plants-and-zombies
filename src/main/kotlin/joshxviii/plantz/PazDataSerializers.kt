package joshxviii.plantz

import io.netty.buffer.ByteBuf
import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.ai.ZombieState
import joshxviii.plantz.entity.gnome.GnomeSoundVariant
import joshxviii.plantz.entity.gnome.GnomeVariant
import joshxviii.plantz.entity.zombie.BrownCoatVariant
import joshxviii.plantz.entity.zombie.GargantuarVariant
import joshxviii.plantz.entity.zombie.ImpVariant
import joshxviii.plantz.entity.zombie.SuperBrainzVariant
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityDataRegistry
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.syncher.EntityDataSerializer
import net.minecraft.world.item.DyeColor
import org.apache.logging.log4j.core.util.Integers

object PazDataSerializers {
    @JvmField val DATA_PAINT_COLORS = EntityDataSerializer.forValueType<Map<Int, Int>>(ByteBufCodecs.map(::HashMap, ByteBufCodecs.INT, ByteBufCodecs.INT))
    @JvmField val DATA_DYE_COLOR = EntityDataSerializer.forValueType<DyeColor>(DyeColor.STREAM_CODEC)
    @JvmField val DATA_PLANT_STATE = EntityDataSerializer.forValueType<PlantState>(PlantState.STREAM_CODEC)
    @JvmField val DATA_ZOMBIE_STATE = EntityDataSerializer.forValueType<ZombieState>(ZombieState.STREAM_CODEC)
    @JvmField val BROWN_COAT_VARIANT = EntityDataSerializer.forValueType<BrownCoatVariant>(BrownCoatVariant.STREAM_CODEC)
    @JvmField val IMP_VARIANT = EntityDataSerializer.forValueType<ImpVariant>(ImpVariant.STREAM_CODEC)
    @JvmField val SUPER_BRAINZ_VARIANT = EntityDataSerializer.forValueType<SuperBrainzVariant>(SuperBrainzVariant.STREAM_CODEC)
    @JvmField val GARGANTUAR_VARIANT = EntityDataSerializer.forValueType<GargantuarVariant>(GargantuarVariant.STREAM_CODEC)
    @JvmField val GNOME_VARIANT = EntityDataSerializer.forValueType<GnomeVariant>(GnomeVariant.STREAM_CODEC)
    @JvmField val GNOME_SOUND_VARIANT = EntityDataSerializer.forValueType<GnomeSoundVariant>(GnomeSoundVariant.STREAM_CODEC)

    fun initialize() {
        FabricEntityDataRegistry.register(pazResource("paint_colors"), DATA_PAINT_COLORS)
        FabricEntityDataRegistry.register(pazResource("dye_color"), DATA_DYE_COLOR)
        FabricEntityDataRegistry.register(pazResource("plant_state"), DATA_PLANT_STATE)
        FabricEntityDataRegistry.register(pazResource("zombie_state"), DATA_ZOMBIE_STATE)
        FabricEntityDataRegistry.register(pazResource("brown_coat_variant"), BROWN_COAT_VARIANT)
        FabricEntityDataRegistry.register(pazResource("imp_variant"), IMP_VARIANT)
        FabricEntityDataRegistry.register(pazResource("super_brainz_variant"), SUPER_BRAINZ_VARIANT)
        FabricEntityDataRegistry.register(pazResource("gargantuar_variant"), GARGANTUAR_VARIANT)
        FabricEntityDataRegistry.register(pazResource("gnome_variant"), GNOME_VARIANT)
        FabricEntityDataRegistry.register(pazResource("gnome_sound_variant"), GNOME_SOUND_VARIANT)
    }
}
