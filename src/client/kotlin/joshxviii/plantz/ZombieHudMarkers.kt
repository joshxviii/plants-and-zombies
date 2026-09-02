package joshxviii.plantz

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import joshxviii.plantz.renderer.entity.PazZombieRenderState
import joshxviii.plantz.renderer.entity.PazZombieRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.renderer.state.gui.GuiElementRenderState
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.Identifier
import net.minecraft.util.LightCoordsUtil
import net.minecraft.util.Mth
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.tan

object ZombieHudMarkers {

    private const val ICON_SIZE = 8

    private const val MIN_DISTANCE = 10.0
    private const val MAX_DISTANCE = 96.0

    fun renderIcons(graphics: GuiGraphicsExtractor, delta: Float) {
        val mc = Minecraft.getInstance()
        if (mc.level == null || mc.player == null) return
        if (!PazConfig.ALWAYS_SHOW_HEAD_MARKERS) PazNetwork.ZombieRaidClientCache.get()
            //?.let { if (it.waveTimer > 1200) return }
            ?: return

        for ((worldPos, zombie) in collect(mc)) {
            val screen = project(worldPos) ?: continue

            val x = Mth.floor(screen.x - ICON_SIZE / 2f)
            val y = Mth.floor(screen.y - ICON_SIZE / 2f)

            headIcon(graphics, zombie, x, y)
        }
    }

    fun headIcon(graphics: GuiGraphicsExtractor, zombie: Zombie, x: Int, y: Int, use3DIcon: Boolean = false) {
        val mc = Minecraft.getInstance()
        val partial = mc.deltaTracker.getGameTimeDeltaPartialTick(false)
        val state = (mc.entityRenderDispatcher.extractEntity(zombie, partial) as? PazZombieRenderState)?.also {// state to render model as
            it.walkAnimationPos = 0f
            it.walkAnimationSpeed = 0f
            it.deathTime = 0f
            it.isAngry = true
            it.isBaby = zombie.isBaby
            it.headOnly = true
            it.mainHandItemState.clear()
            it.rightHandItemState.clear()
            it.leftHandItemState.clear()
            it.yRot = 0f
            it.xRot = 0f
            it.bodyRot = 0f
            it.movementDirection = Vec3.ZERO
        }?: return

        val renderer = mc.entityRenderDispatcher.getRenderer(state) as? PazZombieRenderer ?: return
        val model = if(state.isBaby) renderer.babyModel else renderer.defaultModel
        val texture = renderer.getTextureLocation(state)

        val cx = x + ICON_SIZE * 0.5
        val cy = y + ICON_SIZE * 0.5
        val baseScale = ICON_SIZE * 2.0f

        val w = mc.window.guiScaledWidth
        val h = mc.window.guiScaledHeight

        // outline
        submitHead(graphics, zombie, model, texture, zombie.getItemBySlot(EquipmentSlot.HEAD), cx, cy, baseScale * state.scale, outlinePad = 1.15f)

        // head icon
        if (use3DIcon) graphics.guiRenderState.addPicturesInPictureState(GuiEntityRenderState(state, Vector3f((cx.toFloat() - w * 0.5f) / baseScale, zombie.eyeHeight + (cy.toFloat() - h * 0.5f ) / baseScale, 0f), Quaternionf().rotateX(Mth.PI), null, 0, 0, w, h, baseScale, null))
        else submitHead(graphics, zombie, model, texture, zombie.getItemBySlot(EquipmentSlot.HEAD), cx, cy, baseScale * state.scale)


        if (zombie.mainHandItem.`is`(PazBlocks.BRAINZ_FLAG.asItem()) || zombie.offhandItem.`is`(PazBlocks.BRAINZ_FLAG.asItem())) {
            graphics.pose().pushMatrix()
            graphics.pose().translate(cx.toFloat(), cy.toFloat())
            graphics.pose().scale(baseScale/24f)
            graphics.item(PazBlocks.BRAINZ_FLAG.asItem().defaultInstance, -4, -4)
            graphics.pose().popMatrix()
        }
    }

    private fun submitHead(
        graphics: GuiGraphicsExtractor,
        zombie: Zombie,
        model: HumanoidModel<*>,
        texture: Identifier,
        itemStack: ItemStack,
        cx: Double,
        cy: Double,
        scale: Float,
        outlinePad: Float = 0f
    ) {
        val mc = Minecraft.getInstance()

        graphics.guiRenderState.addGuiElement(object : GuiElementRenderState {
            override fun buildVertices(vertexConsumer: VertexConsumer) {
                model.resetPose()
                val pose = PoseStack()
                pose.pushPose()
                pose.translate(cx, cy, if (outlinePad>0f) 149.0 else 150.0)
                pose.translate(0.0, (ICON_SIZE + outlinePad)*0.5, 0.0)
                pose.scale(scale, scale, scale)
                if (outlinePad>0f) pose.scale(outlinePad, outlinePad, outlinePad)
                model.head.setPos(0f, 0f, 0f)
                model.head.render(pose, vertexConsumer, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY)
                mc.itemModelResolver.updateForTopItem(ItemStackRenderState(), itemStack, ItemDisplayContext.HEAD, mc.level, zombie, 0)

                pose.popPose()
            }

            override fun pipeline(): RenderPipeline = if (outlinePad>0f) PazRenderPipelines.HEAD_MARKER_OUTLINE else PazRenderPipelines.HEAD_MARKER

            override fun textureSetup(): TextureSetup {
                val t = mc.textureManager.getTexture(texture)
                return TextureSetup.singleTexture(t.textureView, t.sampler)
            }

            override fun scissorArea(): ScreenRectangle? = null
            override fun bounds(): ScreenRectangle = ScreenRectangle(0, 0, mc.window.guiScaledWidth, mc.window.guiScaledHeight)
        })
    }

    fun collect(client: Minecraft): List<Marker> {
        val level = client.level ?: return emptyList()
        val cam = client.gameRenderer.mainCamera.position()

        return buildList {
            for (entity in level.entitiesForRendering()) {
                if (entity !is Zombie) continue

                if (!PazConfig.ALWAYS_SHOW_HEAD_MARKERS) {
                    val isFromRaid = (entity as? ZombieRaider)?.`plantz$getIsFromRaid`()?: false
                    if (!isFromRaid) continue
                }
                if (!isViewObscured(cam, entity)) continue

                val pos = entity.getPosition(client.deltaTracker.getGameTimeDeltaPartialTick(false)).add(0.0, entity.eyeHeight.toDouble(), 0.0)
                val dist = pos.distanceToSqr(cam)
                if (dist !in (MIN_DISTANCE*MIN_DISTANCE)..(MAX_DISTANCE*MAX_DISTANCE)) continue
                add(Marker(pos, entity))
            }
        }
    }

    fun isViewObscured(camPos: Vec3, target: LivingEntity): Boolean {
        return target.level().clip(ClipContext(target.eyePosition, camPos, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, target)).type != HitResult.Type.MISS
    }

    fun project(worldPos: Vec3): Vec2? {
        val mc = Minecraft.getInstance()
        val cam = mc.gameRenderer.mainCamera
        val w = mc.window.guiScaledWidth.toDouble()
        val h = mc.window.guiScaledHeight.toDouble()

        val rel = worldPos.subtract(cam.position()).toVector3f()
        val z = rel.dot(cam.forwardVector())
        if (z <= 0.05) return null

        val x = -rel.dot(cam.leftVector())
        val y = rel.dot(cam.upVector())

        val tanHalf = tan(cam.fov * Mth.DEG_TO_RAD * 0.5)
        val ndcX = x / (z * tanHalf * (w / h))
        val ndcY = y / (z * tanHalf)

        val sx = (ndcX * 0.5 + 0.5) * w
        val sy = (1.0 - (ndcY * 0.5 + 0.5)) * h

        if (sx !in -32.0..w + 32.0 || sy !in -32.0..h + 32.0) return null
        return Vec2(sx.toFloat(), sy.toFloat())
    }

    data class Marker(val worldPos: Vec3, val zombie: Zombie)
}