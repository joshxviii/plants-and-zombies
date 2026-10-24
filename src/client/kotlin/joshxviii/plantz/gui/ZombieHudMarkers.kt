package joshxviii.plantz.gui

import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazConfig
import joshxviii.plantz.PazNetwork
import joshxviii.plantz.entity.isActiveInRaid
import joshxviii.plantz.entity.zombie.PazZombie
import joshxviii.plantz.getHeldBalloons
import joshxviii.plantz.renderer.entity.zombie.PazZombieRenderState
import joshxviii.plantz.renderer.entity.zombie.PazZombieRenderer
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.model.HumanoidModel
import net.minecraft.resources.Identifier
import net.minecraft.util.Mth
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import kotlin.math.tan

object ZombieHudMarkers {

    const val ICON_SIZE = 8

    private const val MIN_DISTANCE = 10.0
    private const val MAX_DISTANCE = 96.0

    fun renderIcons(graphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker) {
        val mc = Minecraft.getInstance()
        if (mc.level == null || mc.player == null) return
        if (!PazConfig.ALWAYS_SHOW_HEAD_MARKERS) PazNetwork.ZombieRaidClientCache.get()
            ?: return

        for ((worldPos, zombie) in collect(mc)) {
            val screen = project(worldPos) ?: continue

            val x = Mth.floor(screen.x - ICON_SIZE / 2f)
            val y = Mth.floor(screen.y - ICON_SIZE / 2f)

            headIcon(graphics, zombie, x, y)
        }
    }

    fun headIcon(graphics: GuiGraphicsExtractor, zombie: Zombie, x: Int, y: Int) {
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
        //submitHead(graphics, zombie, model, texture, zombie.getItemBySlot(EquipmentSlot.HEAD), cx, cy, baseScale * state.scale, outlinePad = 1.15f)

        // head icon
        // TODO [use3dIcon] uses a copied render of the entity model with everything except the head removed.
        //  it definitely has an impact on performance, but I can't find a better way to render worn head items like the cones/football helmets.

        // TODO I should probably take a screen shot when resources are loaded of each entity in the zombie raider tag list and save the head marker image to reuse here
        submitHead(graphics, zombie, model, texture, zombie.getItemBySlot(EquipmentSlot.HEAD), cx, cy, baseScale * state.scale)

        if (zombie.mainHandItem.`is`(PazBlocks.BRAINZ_FLAG.asItem()) || zombie.offhandItem.`is`(PazBlocks.BRAINZ_FLAG.asItem())) submitItem(graphics,
            PazBlocks.BRAINZ_FLAG.asItem().defaultInstance, cx, cy, baseScale)

        val balloon = (zombie as? PazZombie)?.getHeldBalloons()?.firstOrNull()
        if (balloon != null) submitItem(graphics, balloon.pickResult, cx, cy-8, baseScale)
    }

    private fun submitItem(
        graphics: GuiGraphicsExtractor,
        item: ItemStack,
        cx: Double,
        cy: Double,
        scale: Float
    ) {
        graphics.pose().pushMatrix()
        graphics.pose().translate(cx.toFloat(), cy.toFloat())
        graphics.pose().scale(scale/24f)
        graphics.item(item, -4, -4)
        graphics.pose().popMatrix()
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
        graphics.guiRenderState.addGuiElement(
            HeadHudMarker(zombie, model, texture, itemStack, cx, cy, scale, outlinePad)
        )
    }

    fun collect(client: Minecraft): List<Marker> {
        val level = client.level ?: return emptyList()
        val cam = client.gameRenderer.mainCamera().position()

        return buildList {
            for (entity in level.entitiesForRendering()) {
                if (entity !is Zombie) continue

                if (!PazConfig.ALWAYS_SHOW_HEAD_MARKERS) {
                    if (!entity.isActiveInRaid()) continue
                }
                if (!isViewObscured(cam, entity)) continue

                val pos = entity.getPosition(client.deltaTracker.getGameTimeDeltaPartialTick(false)).add(0.0, entity.eyeHeight.toDouble(), 0.0)
                val dist = pos.distanceToSqr(cam)
                if (dist !in (MIN_DISTANCE*MIN_DISTANCE)..(MAX_DISTANCE*MAX_DISTANCE)) continue
                add(Marker(pos, entity))
            }
        }.sortedByDescending { it.zombie.distanceToSqr(cam) }
    }

    fun isViewObscured(camPos: Vec3, target: LivingEntity): Boolean {
        return target.level().clip(ClipContext(target.eyePosition, camPos, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, target)).type != HitResult.Type.MISS
    }

    fun project(worldPos: Vec3): Vec2? {
        val mc = Minecraft.getInstance()
        val cam = mc.gameRenderer.mainCamera()
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