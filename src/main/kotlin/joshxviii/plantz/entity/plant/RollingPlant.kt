package joshxviii.plantz.entity.plant

import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import org.joml.Quaternionf

interface RollingPlant {
    companion object {
        val ROLLING: EntityDataAccessor<Boolean> = SynchedEntityData.defineId<Boolean>(Plant::class.java, EntityDataSerializers.BOOLEAN)
    }

    var rollRotation: Quaternionf
    fun isRolling(): Boolean
}