package joshxviii.plantz.gui

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.QuadInstance
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import joshxviii.plantz.PazRenderPipelines
import joshxviii.plantz.gui.ZombieHudMarkers.ICON_SIZE
import joshxviii.plantz.raid.isBoss
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.renderer.state.gui.GuiElementRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.geometry.BakedQuad
import net.minecraft.client.resources.model.geometry.ItemQuads
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.util.LightCoordsUtil
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.ItemDisplayContext
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import java.lang.reflect.Proxy

private val LIGHT = LightCoordsUtil.FULL_BRIGHT
private val NO_OVERLAY = OverlayTexture.NO_OVERLAY

class HeadHudMarker private constructor(
    private val draws: List<MarkerDraw>,
    private val texture: Identifier,
    private val tint: Int,
    private val outline: Boolean,
) : GuiElementRenderState {
    companion object {
        const val OUTLINE_COLOR = 0x000000
        const val BOSS_OUTLINE_COLOR = 0xFF0000

        /**
         * TODO: Hash the head model part for each entity model to reuse
         */
        fun EntityModel<*>.findHead(): ModelPart? = this.root().createPartLookup().apply("head")

        /**
         * Returns the head as GUI segments, ordered back-to-front by depth so worn items and head parts overlap
         */
        fun build(
            zombie: Zombie,
            model: EntityModel<*>,
            texture: Identifier,
            cx: Double,
            cy: Double,
            scale: Float,
            outlinePad: Float = 0f
        ): List<HeadHudMarker> {
            val outline = outlinePad > 0f
            val tint = ARGB.opaque(if (zombie.isBoss()) (if (outline) BOSS_OUTLINE_COLOR else ARGB.addRgb(BOSS_OUTLINE_COLOR, 0xBBBBBB)) else (if (outline) OUTLINE_COLOR else 0xFFFFFF))

            model.resetPose()
            model.findHead()?.setPos(0f, 0f, 0f)
            val draws = ArrayList<MarkerDraw>()
            collectHead(model, headPose(zombie, cx, cy, scale, outlinePad), texture, draws)

            val itemPose = headPose(zombie, cx, cy, scale, outlinePad)
            collectWornItem(zombie, itemPose, draws)

            draws.sortByDescending { it.depth }
            return batch(draws, tint, outline)
        }

        private fun headPose(zombie: Zombie, cx: Double, cy: Double, scale: Float, outlinePad: Float): PoseStack {
            val outline = outlinePad > 0f
            val pose = PoseStack()
            pose.translate(cx, cy, if (outline) 149.0 else 150.0)
            pose.translate(0.0, (ICON_SIZE + outlinePad) * 0.5, 0.0)
            if (zombie.isBoss()) pose.scale(1.25f, 1.25f, 1.25f)
            pose.scale(scale, scale, scale)
            if (outline) pose.scale(outlinePad, outlinePad, outlinePad)
            return pose
        }

        private fun collectHead(model: EntityModel<*>, pose: PoseStack, texture: Identifier, out: MutableList<MarkerDraw>) {
            val head = model.findHead() ?: return
            head.visit(pose) { cubePose, _, _, cube ->
                val matrix = cubePose.pose()
                val center = Vector3f()
                matrix.transformPosition(
                    (cube.minX + cube.maxX) * 0.5f,
                    (cube.minY + cube.maxY) * 0.5f,
                    (cube.minZ + cube.maxZ) * 0.5f,
                    center
                )
                out.add(CubeDraw(cube, center.z, Matrix4f(matrix), Matrix3f(cubePose.normal()), texture))
            }
        }

        private fun collectWornItem(zombie: Zombie, pose: PoseStack, out: MutableList<MarkerDraw>) {
            val stack = zombie.getItemBySlot(EquipmentSlot.HEAD)
            if (stack.isEmpty) return

            val mc = Minecraft.getInstance()
            val itemState = ItemStackRenderState()
            mc.itemModelResolver.updateForTopItem(itemState, stack, ItemDisplayContext.HEAD, mc.level, null, 0)
            pose.scale(0.6f, 0.6f, 0.6f)
            pose.rotate(Axis.ZP.rotationDegrees(180f))
            pose.translate(0f, 0.4f, 0f)

            val submissions = ArrayList<ItemSubmission>()
            itemState.submit(pose, captureCollector(submissions), LIGHT, NO_OVERLAY, 0)

            val corner = Vector3f()
            for (submission in submissions) for (quad in submission.quads.all()) {
                var depth = 0f
                for (i in 0..3) {
                    submission.pose.transformPosition(quad.position(i), corner)
                    depth += corner.z
                }
                out.add(
                    ItemQuadDraw(
                        quad,
                        depth * 0.25f,
                        submission.pose,
                        submission.normal,
                        layerColor(submission.tints, quad),
                        quad.materialInfo().sprite().atlasLocation()
                    )
                )
            }
        }

        private fun layerColor(tints: IntArray, quad: BakedQuad): Int {
            val info = quad.materialInfo()
            return if (info.isTinted && info.tintIndex() in tints.indices) tints[info.tintIndex()] else -1
        }

        private class ItemSubmission(val pose: Matrix4f, val normal: Matrix3f, val tints: IntArray, val quads: ItemQuads)

        private fun captureCollector(out: MutableList<ItemSubmission>): SubmitNodeCollector {
            lateinit var collector: SubmitNodeCollector
            collector = Proxy.newProxyInstance(
                SubmitNodeCollector::class.java.classLoader,
                arrayOf(SubmitNodeCollector::class.java)
            ) { _, method, args ->
                when (method.name) {
                    "submitItem" -> {
                        val pose = args!![0] as PoseStack
                        out.add(
                            ItemSubmission(
                                Matrix4f(pose.last().pose()),
                                Matrix3f(pose.last().normal()),
                                (args[5] as IntArray).copyOf(),
                                args[6] as ItemQuads
                            )
                        )
                        null
                    }
                    "order" -> collector
                    "hashCode" -> System.identityHashCode(collector)
                    "equals" -> args!![0] === collector
                    "toString" -> "HeadItemCollector"
                    else -> null
                }
            } as SubmitNodeCollector
            return collector
        }

        private fun batch(sorted: List<MarkerDraw>, tint: Int, outline: Boolean): List<HeadHudMarker> {
            val result = ArrayList<HeadHudMarker>()
            var run = ArrayList<MarkerDraw>()
            for (draw in sorted) {
                if (run.isNotEmpty() && run[0].texture != draw.texture) {
                    result.add(HeadHudMarker(run, run[0].texture, tint, outline))
                    run = ArrayList()
                }
                run.add(draw)
            }
            if (run.isNotEmpty()) result.add(HeadHudMarker(run, run[0].texture, tint, outline))
            return result
        }
    }

    val mc = Minecraft.getInstance()

    override fun buildVertices(vertexConsumer: VertexConsumer) {
        val scratch = PoseStack()
        for (draw in draws) draw.emit(vertexConsumer, scratch, tint)
    }

    override fun pipeline(): RenderPipeline = if (outline) PazRenderPipelines.HEAD_MARKER_OUTLINE else PazRenderPipelines.HEAD_MARKER

    override fun textureSetup(): TextureSetup {
        val t = mc.textureManager.getTexture(texture)
        return TextureSetup.singleTexture(t.textureView, t.sampler)
    }

    override fun scissorArea(): ScreenRectangle? = null
    override fun bounds(): ScreenRectangle = ScreenRectangle(0, 0, mc.window.guiScaledWidth, mc.window.guiScaledHeight)
}

private sealed interface MarkerDraw {
    val depth: Float
    val texture: Identifier
    fun emit(consumer: VertexConsumer, scratch: PoseStack, tint: Int)
}

private class CubeDraw(
    val cube: ModelPart.Cube,
    override val depth: Float,
    val pose: Matrix4f,
    val normal: Matrix3f,
    override val texture: Identifier,
) : MarkerDraw {
    override fun emit(consumer: VertexConsumer, scratch: PoseStack, tint: Int) {
        scratch.last().pose().set(pose)
        scratch.last().normal().set(normal)
        cube.compile(scratch.last(), consumer, LIGHT, NO_OVERLAY, tint)
    }
}

private class ItemQuadDraw(
    val quad: BakedQuad,
    override val depth: Float,
    val pose: Matrix4f,
    val normal: Matrix3f,
    val itemColor: Int,
    override val texture: Identifier,
) : MarkerDraw {
    override fun emit(consumer: VertexConsumer, scratch: PoseStack, tint: Int) {
        scratch.last().pose().set(pose)
        scratch.last().normal().set(normal)
        val instance = QuadInstance()
        instance.setColor(ARGB.multiply(tint, itemColor))
        instance.setLightCoords(LIGHT)
        instance.setOverlayCoords(NO_OVERLAY)
        consumer.putBakedQuad(scratch.last(), quad, instance)
    }
}
