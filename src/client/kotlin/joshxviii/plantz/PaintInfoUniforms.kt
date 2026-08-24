package joshxviii.plantz

import com.mojang.blaze3d.buffers.GpuBuffer
import com.mojang.blaze3d.buffers.GpuBufferSlice
import com.mojang.blaze3d.buffers.Std140Builder
import com.mojang.blaze3d.systems.RenderSystem
import java.nio.ByteBuffer
import java.nio.ByteOrder

object PaintInfoUniforms {
    private const val SIZE = 16

    private val buffer: GpuBuffer = RenderSystem.getDevice()
        .createBuffer(
            { "plantz_paint_info" },
            GpuBuffer.USAGE_COPY_DST,
            SIZE.toLong()
        )

    fun write(scale: Float, strength: Float) {
        val data = ByteBuffer.allocateDirect(SIZE)
            .order(ByteOrder.nativeOrder())
            .putFloat(scale)
            .putFloat(strength)
            .putFloat(0f)
            .putFloat(0f)
            .rewind()

        RenderSystem.getDevice()
            .createCommandEncoder()
            .writeToBuffer(slice(), data)
    }

    fun slice(): GpuBufferSlice = buffer.slice(0, SIZE.toLong())

    fun amplifierToNoise(amplifier: Int): Pair<Float, Float> {
        val t = (amplifier / 20f).coerceIn(0f, 1f)
        val scale = 8f + t * 24f
        val strength = t * 2f
        return scale to strength
    }
}