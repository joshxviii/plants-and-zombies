package joshxviii.plantz

import com.mojang.blaze3d.vertex.PoseStack
import joshxviii.plantz.PazModels.ARMOR_LAYER_LOCATION
import joshxviii.plantz.entity.gnome.Gnome
import joshxviii.plantz.entity.gnome.GnomeVariant
import joshxviii.plantz.model.GnomeArmorModel
import joshxviii.plantz.model.GnomeModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer
import net.minecraft.client.renderer.entity.layers.RenderLayer
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState
import net.minecraft.client.renderer.state.CameraRenderState
import net.minecraft.client.resources.model.EquipmentClientInfo.LayerType
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.AnimationState
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

class GnomeRenderer(
    context: EntityRendererProvider.Context,
    defaultModel: GnomeModel<GnomeRenderState>,
) : MobRenderer<Gnome, GnomeRenderState, GnomeModel<GnomeRenderState>>(
    context,
    defaultModel,
    0.2f
) {
    companion object {
        val CUSTOM_HEAD_TRANSFORMS: CustomHeadLayer.Transforms = CustomHeadLayer.Transforms(-0.1171875F, -0.07421875F, 1.0F);
    }

    init {
        val armorModels = GnomeArmorSet.bakeDefault(ARMOR_LAYER_LOCATION, context.modelSet)
        addLayer(GnomeArmorLayer(this, armorModels, context.equipmentRenderer))
        addLayer(CustomHeadLayer(this, context.modelSet, context.playerSkinRenderCache, CUSTOM_HEAD_TRANSFORMS))
        addLayer(ItemInHandLayer(this))
    }

    override fun submit(
        state: GnomeRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        super.submit(state, poseStack, submitNodeCollector, camera)
    }

    override fun getTextureLocation(state: GnomeRenderState): Identifier = state.variant.getTexture()

    override fun createRenderState(): GnomeRenderState {
        return GnomeRenderState()
    }

    override fun extractRenderState(entity: Gnome, state: GnomeRenderState, partialTicks: Float) {
        super.extractRenderState(entity, state, partialTicks)
        ArmedEntityRenderState.extractArmedEntityRenderState(entity, state, this.itemModelResolver, partialTicks)
        state.variant = entity.variant
        state.headEquipment = getEquipmentIfRenderable(entity, EquipmentSlot.HEAD)
        state.chestEquipment = getEquipmentIfRenderable(entity, EquipmentSlot.CHEST)
        state.legsEquipment = getEquipmentIfRenderable(entity, EquipmentSlot.LEGS)
        state.feetEquipment = getEquipmentIfRenderable(entity, EquipmentSlot.FEET)
    }

    private fun getEquipmentIfRenderable(entity: LivingEntity, slot: EquipmentSlot): ItemStack {
        val itemStack = entity.getItemBySlot(slot)
        return if (GnomeArmorLayer.shouldRender(itemStack, slot)) itemStack.copy() else ItemStack.EMPTY
    }
}

class GnomeArmorLayer(
    renderer: RenderLayerParent<GnomeRenderState, GnomeModel<GnomeRenderState>>,
    private val armorModels: GnomeArmorSet<GnomeArmorModel<GnomeRenderState>>,
    private val equipmentRenderer: EquipmentLayerRenderer
) : RenderLayer<GnomeRenderState, GnomeModel<GnomeRenderState>>(renderer) {

    override fun submit(
        poseStack: PoseStack, submitNodeCollector: SubmitNodeCollector, lightCoords: Int, state: GnomeRenderState, yRot: Float, xRot: Float
    ) {
        this.renderArmorPiece(poseStack, submitNodeCollector, state.chestEquipment, EquipmentSlot.CHEST, lightCoords, state)
        this.renderArmorPiece(poseStack, submitNodeCollector, state.legsEquipment, EquipmentSlot.LEGS, lightCoords, state)
        this.renderArmorPiece(poseStack, submitNodeCollector, state.feetEquipment, EquipmentSlot.FEET, lightCoords, state)
        this.renderArmorPiece(poseStack, submitNodeCollector, state.headEquipment, EquipmentSlot.HEAD, lightCoords, state)
    }

    private fun renderArmorPiece(
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        itemStack: ItemStack,
        slot: EquipmentSlot,
        lightCoords: Int,
        state: GnomeRenderState,
    ) {
        val equippable = itemStack.get(DataComponents.EQUIPPABLE) ?: return
        if (!equippable.slot().equals(slot)) return
        val assetId = equippable.assetId().orElse(null) ?: return

        val layerType = if (this.usesInnerModel(slot)) LayerType.HUMANOID_LEGGINGS
        else LayerType.HUMANOID

        val armorModel = armorModels[slot]

        poseStack.pushPose()
        this.equipmentRenderer
            .renderLayers<GnomeRenderState>(
                layerType,
                assetId,
                armorModel,
                state,
                itemStack,
                poseStack,
                submitNodeCollector,
                lightCoords,
                state.outlineColor
            )
        poseStack.popPose()
    }

    private fun usesInnerModel(slot: EquipmentSlot?): Boolean {
        return slot == EquipmentSlot.LEGS
    }

    companion object {
        fun shouldRender(stack: ItemStack, slot: EquipmentSlot): Boolean {
            val equippable = stack.get(DataComponents.EQUIPPABLE) ?: return false
            return equippable.slot() == slot && equippable.assetId().isPresent
        }
    }
}

class GnomeRenderState : ArmedEntityRenderState() {
    var variant: GnomeVariant = GnomeVariant.BLUE
    var headEquipment: ItemStack = ItemStack.EMPTY
    var chestEquipment: ItemStack = ItemStack.EMPTY
    var legsEquipment: ItemStack = ItemStack.EMPTY
    var feetEquipment: ItemStack = ItemStack.EMPTY
    val idleAnimationState: AnimationState = AnimationState()
    val actionAnimationState: AnimationState = AnimationState()
}