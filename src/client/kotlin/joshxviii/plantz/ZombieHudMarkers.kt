package joshxviii.plantz

import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.renderer.entity.PazZombieRenderState
import joshxviii.plantz.renderer.entity.PazZombieRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.state.gui.GuiElementRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.LightCoordsUtil
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import kotlin.math.tan

object ZombieHudMarkers {

    private const val ICON_SIZE = 8
    private const val MAX_DISTANCE = 64.0

    fun collect(client: Minecraft): List<Marker> {
        val level = client.level ?: return emptyList()
        val cam = client.gameRenderer.mainCamera.position()

        return buildList {
            for (entity in level.entitiesForRendering()) {
                if (entity !is PazZombie) continue
                val pos = entity.getPosition(client.deltaTracker.getGameTimeDeltaPartialTick(false))
                    .add(0.0, entity.eyeHeight + 0.5, 0.0)
                if (pos.distanceToSqr(cam) > MAX_DISTANCE * MAX_DISTANCE) continue
                add(Marker(pos, entity))
            }
        }
    }

    fun renderIcons(graphics: GuiGraphicsExtractor, delta: Float) {
        val mc = Minecraft.getInstance()
        if (mc.level == null || mc.player == null) return

        for ((worldPos, zombie) in collect(mc)) {
            val screen = project(worldPos) ?: continue

            val x = Mth.floor(screen.x - ICON_SIZE / 2f)
            val y = Mth.floor(screen.y - ICON_SIZE / 2f)

            headIcon(graphics, zombie, x, y)
        }
    }

    fun project(worldPos: Vec3): Vec2? {
        val mc = Minecraft.getInstance()
        val camera = mc.gameRenderer.mainCamera

        val rel = worldPos.subtract(camera.position())

        val look = Vec3.directionFromRotation(camera.xRot(), camera.yRot())

        var right = look.cross(Vec3.Y_AXIS)
        if (right.lengthSqr() < 1.0e-6) {
            right = look.cross(Vec3(0.0, 0.0, 1.0))
            if (right.lengthSqr() < 1.0e-6) {
                right = look.cross(Vec3(1.0, 0.0, 0.0))
            }
        }
        right = right.normalize()
        val up = right.cross(look).normalize()

        val x = rel.dot(right)
        val y = rel.dot(up)
        val z = rel.dot(look)
        if (z <= 0.05) return null

        val aspect = mc.window.guiScaledWidth.toDouble() / mc.window.guiScaledHeight
        val tanHalf = tan(camera.fov * Mth.DEG_TO_RAD * 0.5)

        val ndcX = x / (z * tanHalf * aspect)
        val ndcY = y / (z * tanHalf)

        val sx = (ndcX * 0.5 + 0.5) * mc.window.guiScaledWidth
        val sy = (1.0 - (ndcY * 0.5 + 0.5)) * mc.window.guiScaledHeight

        if (sx !in -32.0..(mc.window.guiScaledWidth + 32.0)) return null
        if (sy !in -32.0..(mc.window.guiScaledHeight + 32.0)) return null

        return Vec2(sx.toFloat(), sy.toFloat())
    }

    fun headIcon(graphics: GuiGraphicsExtractor, zombie: PazZombie, x: Int, y: Int) {

        val mc = Minecraft.getInstance()
        val state = mc.entityRenderDispatcher.extractEntity(zombie, mc.deltaTracker.getGameTimeDeltaPartialTick(false)) as? PazZombieRenderState ?: return
        val renderer = mc.entityRenderDispatcher.getRenderer(state) as? PazZombieRenderer ?: return

        val model = renderer.model
        val texture = renderer.getTextureLocation(state)

        graphics.guiRenderState.addGuiElement(object : GuiElementRenderState {
            override fun buildVertices(vertexConsumer: VertexConsumer) {
                model.resetPose()
                val pose = PoseStack()
                pose.pushPose()

                pose.translate(x + ICON_SIZE * 0.5, y + ICON_SIZE * 0.5, 150.0)

                val s = ICON_SIZE * 2.0f
                pose.scale(s, s, s)

                model.head.setPos(0f,0f,0f)
                model.head.render(
                    pose,
                    vertexConsumer,
                    LightCoordsUtil.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY
                )

                pose.popPose()
            }

            override fun pipeline(): RenderPipeline = RenderPipelines.GUI_TEXTURED

            override fun textureSetup(): TextureSetup {
                val t = mc.textureManager.getTexture(texture)
                return TextureSetup.singleTexture(t.textureView, t.sampler)
            }

            override fun scissorArea(): ScreenRectangle? = null

            override fun bounds(): ScreenRectangle = ScreenRectangle(0, 0, mc.window.guiScaledWidth, mc.window.guiScaledHeight)
        })
    }

    data class Marker(val worldPos: Vec3, val zombie: PazZombie)
}