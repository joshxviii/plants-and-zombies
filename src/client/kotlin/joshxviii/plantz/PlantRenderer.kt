package joshxviii.plantz

import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry
import joshxviii.plantz.entity.Plant
import joshxviii.plantz.model.PeaShooterModel
import joshxviii.plantz.model.SunflowerModel
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.EntityType

object PlantRenderers {
    val PEASHOOTER = ModelLayerLocation(pazResource("peashooter"), "main")
    val SUNFLOWER = ModelLayerLocation(pazResource("sunflower"), "main")

    fun registerAll() {
        ModelLayerRegistry.registerModelLayer(PEASHOOTER) { PeaShooterModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(SUNFLOWER) { SunflowerModel.createBodyLayer() }

        EntityRenderers.register(PazEntities.PEA_SHOOTER) { context -> PlantRenderer(PeaShooterModel(context.bakeLayer(PEASHOOTER)), context) }
        EntityRenderers.register(PazEntities.SUNFLOWER) { context -> PlantRenderer(SunflowerModel(context.bakeLayer(SUNFLOWER)), context) }
    }

    private fun <M : EntityModel<PlantRenderState>> registerPlant(
        type: EntityType<out Plant>,
        layer: ModelLayerLocation,
        modelFactory: (ModelPart) -> M,
        layerFactory: () -> LayerDefinition
    ) {
        ModelLayerRegistry.registerModelLayer(layer, layerFactory)
        EntityRenderers.register(type) { context ->
            val model = modelFactory(context.bakeLayer(layer))
            PlantRenderer(model, context)
        }
    }

}

class PlantRenderState : LivingEntityRenderState() {
    var isPotted: Boolean = false
    var texturePath: String = "default"
    val idleAnimationState: AnimationState = AnimationState()
    val attackAnimationState: AnimationState = AnimationState()
}

class PlantRenderer(
    model: EntityModel<PlantRenderState>,
    context: EntityRendererProvider.Context
) : MobRenderer<Plant, PlantRenderState, EntityModel<PlantRenderState>>(
    context,
    model,
    0.5f
) {
    override fun createRenderState(): PlantRenderState {
        return PlantRenderState()
    }

    override fun extractRenderState(entity: Plant, state: PlantRenderState, partialTick: Float) {
        super.extractRenderState(entity, state, partialTick)

        state.texturePath = BuiltInRegistries.ENTITY_TYPE.getKey(entity.type).path
    }

    override fun getTextureLocation(state: PlantRenderState): Identifier {
        return pazResource("textures/entity/${state.texturePath}/${state.texturePath}.png")
    }

}


