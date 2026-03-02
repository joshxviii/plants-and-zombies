package joshxviii.plantz

import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.ColorTargetState
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.RenderPipelines

object PazRenderPipelines {
    val PLANT_PROJECTILE = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(pazResource("pipeline/plant_projectile"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1f)
            .withShaderDefine("APPLY_TEXTURE_MATRIX")
            .withShaderDefine("NO_OVERLAY")
            .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build()
    )

    val EMISSIVE_PROJECTILE = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(pazResource("pipeline/emissive_projectile"))
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("APPLY_TEXTURE_MATRIX")
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .build()
    )

    val EMISSIVE_SUN: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.MATRICES_FOG_LIGHT_DIR_SNIPPET)
            .withLocation(pazResource("pipeline/emissive_sun"))
            .withVertexShader("core/item")
            .withFragmentShader("core/item")
            .withSampler("Sampler0")
            .withSampler("Sampler2")
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withColorTargetState(ColorTargetState(BlendFunction.TRANSLUCENT))
            .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
            .withCull(false)
            .build()
    )

    val FLAG = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(pazResource("pipeline/flag"))
            .withShaderDefine("APPLY_TEXTURE_MATRIX")
            .withSampler("Sampler0")
            .build()
    )

    fun initialize() {}
}