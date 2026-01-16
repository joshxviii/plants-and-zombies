import joshxviii.plantz.ai.PlantState
import joshxviii.plantz.pazResource
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityDataRegistry
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.syncher.EntityDataSerializer

object PazDataSerializers {
    val DATA_PLANT_STATE = EntityDataSerializer.forValueType<PlantState>(PlantState.STREAM_CODEC)
    val DATA_COOLDOWN = EntityDataSerializer.forValueType<Int>(ByteBufCodecs.VAR_INT)
    val DATA_SLEEPING = EntityDataSerializer.forValueType<Boolean>(ByteBufCodecs.BOOL)

    fun initialize() {
        FabricEntityDataRegistry.register(pazResource("plant_state"), DATA_PLANT_STATE)
        FabricEntityDataRegistry.register(pazResource("cooldown"), DATA_COOLDOWN)
        FabricEntityDataRegistry.register(pazResource("sleeping"), DATA_SLEEPING)
    }
}