package joshxviii.plantz

import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.RenderPipelines

object PazRenderPipelines {
    val PLANT_PROJECTILE = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation("pipeline/plant_projectile")
            .withShaderDefine("ALPHA_CUTOUT", 0.1f)
            .withShaderDefine("APPLY_TEXTURE_MATRIX")
            .withShaderDefine("NO_OVERLAY")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .build()
    )

    val EMISSIVE_PROJECTILE = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation("pipeline/emissive_projectile")
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withShaderDefine("APPLY_TEXTURE_MATRIX")
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .build()
    )

    val EMISSIVE_SUN: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.MATRICES_FOG_LIGHT_DIR_SNIPPET)
            .withLocation("pipeline/emissive_sun")
            .withVertexShader("core/rendertype_item_entity_translucent_cull")
            .withFragmentShader("core/rendertype_item_entity_translucent_cull")
            .withSampler("Sampler0")
            .withSampler("Sampler2")
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withVertexFormat(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS)
            .withCull(false)
            .build()
    )

    val FLAG = RenderPipelines.register(
        RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation("pipeline/flag")
            .withShaderDefine("APPLY_TEXTURE_MATRIX")
            .withSampler("Sampler0")
            .build()
    )

    fun initialize() {}
}