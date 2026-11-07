package joshxviii.plantz

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.renderpearl.api.pipeline.BlendFunction
import com.mojang.renderpearl.api.pipeline.ColorTargetState
import com.mojang.renderpearl.api.pipeline.DepthStencilState
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import net.fabricmc.loader.api.FabricLoader
import net.irisshaders.iris.api.v0.IrisApi
import net.irisshaders.iris.api.v0.IrisProgram
import net.minecraft.client.renderer.BindGroupLayouts
import net.minecraft.client.renderer.RenderPipelines

object PazRenderPipelines {

    @JvmField
    val PAINT_OVERLAY: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(pazResource("pipeline/paint_overlay"))
            .withFragmentShader(pazResource("core/paint_overlay"))
            .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
            .withBindGroupLayout(BindGroupLayouts.SAMPLER1)
            .withShaderDefine("ALPHA_CUTOUT", 0.1f)
            .withCull(false)
            .build()
    )

    //TODO use these two for rendering the image on the HUD
    @JvmField
    val HEAD_MARKER: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(pazResource("pipeline/head_marker"))
            .build()
    )

    @JvmField
    val HEAD_MARKER_OUTLINE: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(pazResource("pipeline/head_marker_outline"))
            .withFragmentShader(pazResource("core/head_marker_outline"))
            .build()
    )

    fun initialize() {
        if (FabricLoader.getInstance().isModLoaded("iris")) {
            registerIrisCompat()
        }
    }

    private fun registerIrisCompat() {
        try {
            IrisApi.getInstance().let{
                it.assignPipeline(
                    PAINT_OVERLAY,
                    IrisProgram.ENTITIES
                )
                it.assignPipeline(
                    TIME_PORTAL,
                    IrisProgram.PARTICLES_TRANSLUCENT
                )
            }

        } catch (t: Throwable) {
            PazMain.LOGGER.warn("Iris present but assignPipeline failed", t)
        }
    }
}