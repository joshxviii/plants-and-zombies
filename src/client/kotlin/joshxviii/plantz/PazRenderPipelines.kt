package joshxviii.plantz

import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.ColorTargetState
import com.mojang.blaze3d.pipeline.DepthStencilState
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.shaders.UniformType
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.fabricmc.loader.api.FabricLoader
import net.irisshaders.iris.api.v0.IrisApi
import net.irisshaders.iris.api.v0.IrisProgram
import net.minecraft.client.renderer.RenderPipelines

object PazRenderPipelines {

    @JvmField
    val ELECTRIC_ARC = RenderPipelines.register(
        RenderPipeline.builder(*arrayOf(RenderPipelines.MATRICES_FOG_SNIPPET))
            .withLocation("pipeline/energy_swirl")
            .withVertexShader("core/entity")
            .withFragmentShader("core/entity")
            .withShaderDefine("ALPHA_CUTOUT", 1.0f)
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withShaderDefine("APPLY_TEXTURE_MATRIX")
            .withSampler("Sampler0")
            .withColorTargetState(ColorTargetState(BlendFunction.ADDITIVE))
            .withCull(false).withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .build())

    @JvmField
    val TIME_PORTAL: RenderPipeline = RenderPipelines.register(RenderPipeline.builder(*arrayOf(
            RenderPipelines.MATRICES_PROJECTION_SNIPPET,
            RenderPipelines.FOG_SNIPPET,
            RenderPipelines.GLOBALS_SNIPPET))
        .withLocation(pazResource("pipeline/time_portal"))
        .withVertexShader(pazResource("core/time_portal"))
        .withFragmentShader(pazResource("core/time_portal"))
        .withSampler("Sampler0").withSampler("Sampler1")
        .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
        .withDepthStencilState(DepthStencilState.DEFAULT)
        .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
        .build()
    )

    @JvmField
    val PAINT_OVERLAY: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withUniform("PaintInfo", UniformType.UNIFORM_BUFFER)
            .withLocation(pazResource("pipeline/paint_overlay"))
            .withFragmentShader(pazResource("core/paint_overlay"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1f)
            .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
            .withSampler("Sampler0")
            .withCull(false)
            .build()
    )

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