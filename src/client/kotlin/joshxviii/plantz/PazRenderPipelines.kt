package joshxviii.plantz

import com.mojang.renderpearl.api.pipeline.BlendFunction
import com.mojang.renderpearl.api.pipeline.ColorTargetState
import com.mojang.renderpearl.api.pipeline.DepthStencilState
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology
import net.minecraft.client.renderer.BindGroupLayouts
import net.fabricmc.loader.api.FabricLoader
import net.irisshaders.iris.api.v0.IrisApi
import net.irisshaders.iris.api.v0.IrisProgram
import net.minecraft.client.renderer.RenderPipelines

object PazRenderPipelines {

    @JvmField
    val ELECTRIC_ARC = RenderPipelines.register(
        RenderPipeline.builder(*arrayOf(RenderPipelines.MATRICES_FOG_SNIPPET))
            .withLocation(pazResource("pipeline/electric_arc"))
            .withVertexShader("core/entity")
            .withFragmentShader("core/entity")
            .withShaderDefine("ALPHA_CUTOUT", 1.0f)
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withShaderDefine("APPLY_TEXTURE_MATRIX")
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
            .withColorTargetState(ColorTargetState(BlendFunction.ADDITIVE))
            .withCull(false).withVertexBinding(0, DefaultVertexFormat.ENTITY).withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .build())

    @JvmField
    val PAINT_OVERLAY: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(pazResource("pipeline/paint_overlay"))
            .withFragmentShader(pazResource("core/paint_overlay"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1f)
            .withShaderDefine("NO_OVERLAY")
            .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
            .withBindGroupLayout(BindGroupLayouts.SAMPLER1)
            .withCull(false)
            .build()
    )

    //TODO Use this for taking a screenshot of the zombie models
    @JvmField
    val HEAD_MARKER_BAKE: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(pazResource("pipeline/head_marker_bake"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
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
            }

        } catch (t: Throwable) {
            PazMain.LOGGER.warn("Iris present but assignPipeline failed", t)
        }
    }
}
