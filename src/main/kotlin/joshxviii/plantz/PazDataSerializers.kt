package joshxviii.plantz

import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.entity.gnome.GnomeSoundVariant
import joshxviii.plantz.entity.gnome.GnomeVariant
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityDataRegistry
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.syncher.EntityDataSerializer

object PazDataSerializers {
    @JvmField val DATA_PLANT_STATE = EntityDataSerializer.forValueType<PlantState>(PlantState.STREAM_CODEC)
    @JvmField val DATA_COOLDOWN = EntityDataSerializer.forValueType<Int>(ByteBufCodecs.VAR_INT)
    @JvmField val DATA_SLEEPING = EntityDataSerializer.forValueType<Boolean>(ByteBufCodecs.BOOL)
    @JvmField val GNOME_VARIANT = EntityDataSerializer.forValueType<GnomeVariant>(GnomeVariant.STREAM_CODEC)
    @JvmField val GNOME_SOUND_VARIANT = EntityDataSerializer.forValueType<GnomeSoundVariant>(GnomeSoundVariant.STREAM_CODEC)

    fun initialize() {
        FabricEntityDataRegistry.register(pazResource("plant_state"), DATA_PLANT_STATE)
        FabricEntityDataRegistry.register(pazResource("cooldown"), DATA_COOLDOWN)
        FabricEntityDataRegistry.register(pazResource("sleeping"), DATA_SLEEPING)
        FabricEntityDataRegistry.register(pazResource("gnome_variant"), GNOME_VARIANT)
        FabricEntityDataRegistry.register(pazResource("gnome_sound_variant"), GNOME_SOUND_VARIANT)
    }
}