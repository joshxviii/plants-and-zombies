package joshxviii.plantz

import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.monster.zombie.ZombieModel
import net.minecraft.client.renderer.entity.AbstractZombieRenderer
import net.minecraft.client.renderer.entity.ArmorModelSet
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.ZombieRenderState
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.monster.zombie.Zombie

class PazZombieRenderer(
    context: EntityRendererProvider.Context,
    body: ModelLayerLocation = ModelLayers.ZOMBIE,
    babyBody: ModelLayerLocation = ModelLayers.ZOMBIE_BABY,
    armorSet: ArmorModelSet<ModelLayerLocation> = ModelLayers.ZOMBIE_ARMOR,
    babyArmorSet: ArmorModelSet<ModelLayerLocation> = ModelLayers.ZOMBIE_BABY_ARMOR
) : AbstractZombieRenderer<Zombie, ZombieRenderState, ZombieModel<ZombieRenderState>>(
    context,
    ZombieModel<ZombieRenderState>(context.bakeLayer(body)),
    ZombieModel<ZombieRenderState>(context.bakeLayer(babyBody)),
    ArmorModelSet.bake<ZombieModel<ZombieRenderState>>(armorSet, context.modelSet) { root: ModelPart -> ZombieModel(root) },
    ArmorModelSet.bake<ZombieModel<ZombieRenderState>>(
        babyArmorSet,
        context.modelSet
    ) { root: ModelPart -> ZombieModel(root) }
) {
    override fun createRenderState(): PazZombieRenderState {
        return PazZombieRenderState()
    }

    override fun extractRenderState(entity: Zombie, state: ZombieRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
        (state as PazZombieRenderState)
        state.texturePath = BuiltInRegistries.ENTITY_TYPE.getKey(entity.type).path
    }

    override fun getTextureLocation(state: ZombieRenderState): Identifier {
        (state as PazZombieRenderState)
        val baseTexture = "textures/entity/zombie/${state.texturePath}/${state.texturePath}"
        return pazResource("${baseTexture}.png")
    }
}

class PazZombieRenderState : ZombieRenderState() {
    var texturePath: String = "default"
}