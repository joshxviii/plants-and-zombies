package joshxviii.plantz

import joshxviii.plantz.entity.Plant
import joshxviii.plantz.model.CherryBombModel
import joshxviii.plantz.model.ChomperModel
import joshxviii.plantz.model.IcePeaShooterModel
import joshxviii.plantz.model.PeaShooterModel
import joshxviii.plantz.model.PotatoMineModel
import joshxviii.plantz.model.RepeaterModel
import joshxviii.plantz.model.SunflowerModel
import joshxviii.plantz.model.WallNutModel
import joshxviii.plantz.model.projectiles.PeaModel
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.renderer.entity.EntityRenderers
import net.minecraft.world.entity.EntityType

object PazModels {

    fun registerAll() {
        // REGISTER MODELS
        ModelLayerRegistry.registerModelLayer(PeaShooterModel.LAYER_LOCATION) { PeaShooterModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(SunflowerModel.LAYER_LOCATION) { SunflowerModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(WallNutModel.LAYER_LOCATION) { WallNutModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(ChomperModel.LAYER_LOCATION) { ChomperModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(CherryBombModel.LAYER_LOCATION) { CherryBombModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(PotatoMineModel.LAYER_LOCATION) { PotatoMineModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(IcePeaShooterModel.LAYER_LOCATION) { IcePeaShooterModel.createBodyLayer() }
        ModelLayerRegistry.registerModelLayer(RepeaterModel.LAYER_LOCATION) { RepeaterModel.createBodyLayer() }

        ModelLayerRegistry.registerModelLayer(PeaModel.LAYER_LOCATION) { PeaModel.createBodyLayer() }



        // REGISTER ENTITY RENDERERS
        EntityRenderers.register(PazEntities.PEA_SHOOTER) { ctx -> PlantRenderer(PeaShooterModel(ctx.bakeLayer(PeaShooterModel.LAYER_LOCATION)), ctx) }
        EntityRenderers.register(PazEntities.SUNFLOWER) { ctx -> PlantRenderer(SunflowerModel(ctx.bakeLayer(SunflowerModel.LAYER_LOCATION)), ctx) }
        EntityRenderers.register(PazEntities.WALL_NUT) { ctx -> PlantRenderer(WallNutModel(ctx.bakeLayer(WallNutModel.LAYER_LOCATION)), ctx) }
        EntityRenderers.register(PazEntities.CHOMPER) { ctx -> PlantRenderer(ChomperModel(ctx.bakeLayer(ChomperModel.LAYER_LOCATION)), ctx) }
        EntityRenderers.register(PazEntities.CHERRY_BOMB) { ctx -> PlantRenderer(CherryBombModel(ctx.bakeLayer(CherryBombModel.LAYER_LOCATION)), ctx) }
        EntityRenderers.register(PazEntities.POTATO_MINE) { ctx -> PlantRenderer(PotatoMineModel(ctx.bakeLayer(PotatoMineModel.LAYER_LOCATION)), ctx) }
        EntityRenderers.register(PazEntities.ICE_PEA_SHOOTER) { ctx -> PlantRenderer(IcePeaShooterModel(ctx.bakeLayer(IcePeaShooterModel.LAYER_LOCATION)), ctx) }
        EntityRenderers.register(PazEntities.REPEATER) { ctx -> PlantRenderer(RepeaterModel(ctx.bakeLayer(RepeaterModel.LAYER_LOCATION)), ctx) }
        EntityRenderers.register(PazEntities.FIRE_PEA_SHOOTER) { ctx -> PlantRenderer(IcePeaShooterModel(ctx.bakeLayer(IcePeaShooterModel.LAYER_LOCATION)), ctx) }
        EntityRenderers.register(PazEntities.PEA) { ctx -> ProjectileRenderer(PeaModel(ctx.bakeLayer(PeaModel.LAYER_LOCATION)), ctx) }
        EntityRenderers.register(PazEntities.PEA_ICE) { ctx -> ProjectileRenderer(PeaModel(ctx.bakeLayer(PeaModel.LAYER_LOCATION)), ctx) }
        EntityRenderers.register(PazEntities.PEA_FIRE) { ctx -> ProjectileRenderer(PeaModel(ctx.bakeLayer(PeaModel.LAYER_LOCATION)), ctx) }
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