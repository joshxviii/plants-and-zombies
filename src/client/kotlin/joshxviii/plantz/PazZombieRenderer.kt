package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.entity.zombie.DiscoZombie
import joshxviii.plantz.entity.zombie.NewspaperZombie
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.model.zombies.PazZombieModel
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.monster.zombie.ZombieModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.AbstractZombieRenderer
import net.minecraft.client.renderer.entity.ArmorModelSet
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.ZombieRenderState
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.monster.zombie.Zombie
import org.joml.Vector3f

class PazZombieRenderer(
    context: EntityRendererProvider.Context,
    private val defaultModel: PazZombieModel = PazZombieModel(context.bakeLayer(PazZombieModel.LAYER_LOCATION)),
    private val babyModel: PazZombieModel = PazZombieModel(context.bakeLayer(ModelLayers.ZOMBIE_BABY)),
    armorSet: ArmorModelSet<ModelLayerLocation> = ModelLayers.ZOMBIE_ARMOR,
    babyArmorSet: ArmorModelSet<ModelLayerLocation> = ModelLayers.ZOMBIE_BABY_ARMOR
) : AbstractZombieRenderer<Zombie, ZombieRenderState, ZombieModel<ZombieRenderState>>(
    context,
    defaultModel,
    babyModel,
    ArmorModelSet.bake<ZombieModel<ZombieRenderState>>(armorSet, context.modelSet) { root: ModelPart -> ZombieModel(root) },
    ArmorModelSet.bake<ZombieModel<ZombieRenderState>>(babyArmorSet, context.modelSet) { root: ModelPart -> ZombieModel(root) }
) {
    override fun createRenderState(): PazZombieRenderState {
        return PazZombieRenderState()
    }

    override fun extractRenderState(entity: Zombie, state: ZombieRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
        (entity as PazZombie)
        (state as PazZombieRenderState)
        state.texturePath = BuiltInRegistries.ENTITY_TYPE.getKey(entity.type).path
        state.initAnimationState.startIfStopped(0)
        if (entity is DiscoZombie) state.actionAnimationState.copyFrom(entity.summonAnimation)
        if (entity is NewspaperZombie) state.isAngry = entity.isAngry()
    }

    override fun getTextureLocation(state: ZombieRenderState): Identifier {
        (state as PazZombieRenderState)

        val base = "textures/entity/zombie/${state.texturePath}/${state.texturePath}"
        val rm = Minecraft.getInstance().resourceManager

        val suffixes = buildList {
            if (state.isAngry) add("angry")
        }

        return resolveTextureLocation(base, suffixes, rm) ?: pazResource("$base.png")
    }
}

class PazZombieRenderState : ZombieRenderState() {
    var texturePath: String = "default"
    var actionTime: Int = 0
    var isAngry: Boolean = false
    val initAnimationState: AnimationState = AnimationState()
    val actionAnimationState: AnimationState = AnimationState()
}