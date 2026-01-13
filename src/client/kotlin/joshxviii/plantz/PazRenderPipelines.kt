package joshxviii.plantz

import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.RenderPipeline
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
            .withShaderDefine("EMISSIVE")
            .withShaderDefine("NO_OVERLAY")
            .withShaderDefine("NO_CARDINAL_LIGHTING")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .build()
    )

    fun initialize() {}
}