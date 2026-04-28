package joshxviii.plantz

import joshxviii.plantz.PazMain.MODID
import joshxviii.plantz.entity.plant.Chomper
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerEntityGetter
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.Entity.MoveFunction
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.OwnableEntity
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.pathfinder.Path
import net.minecraft.world.phys.Vec3

fun pazResource(path: String): Identifier = Identifier.fromNamespaceAndPath(MODID, path)

interface PlantHeadAttachment {
    fun `plantz$hasPlantOnHead`(): Boolean
    fun `plantz$getPlant`(): Plant?
    fun `plantz$setPlant`(value: Plant?)
    fun `plantz$getPlantData`(): CompoundTag
    fun `plantz$setPlantData`(value: CompoundTag)
}

fun Entity.headAttachmentPoint(): Vec3 {
    val scale = (this as? LivingEntity)?.scale ?: 1.0f
    val pitch = Math.toRadians(this.xRot.toDouble())
    val yaw = Math.toRadians(this.yRot.toDouble())

    val local = Vec3(0.0, 0.575 * scale, 0.0)

    val pitched = local.xRot(-pitch.toFloat())
    val rotated = pitched.yRot(-yaw.toFloat())

    return this.position().add(
        rotated.x,
        this.eyeHeight + rotated.y - 0.25 * scale,
        rotated.z
    )
//    return Vec3(
//        this.x + rotated.x,
//        this.eyeY + rotated.y - 0.25 * scale,
//        this.z + rotated.z
//    )
}

fun Entity.canWearPlant(): Boolean {
    return this is LivingEntity && this.getItemBySlot(EquipmentSlot.HEAD).`is`(PazItems.PLANT_POT_HELMET)
            && this.isAlive && !this.isDeadOrDying
            && !(this is ServerPlayer && (this.isSpectator || this.hasDisconnected()))
}
fun Entity.tryToSetPlantOnHead(entityTag: CompoundTag): Boolean {
    if (this.canWearPlant() && !(this as PlantHeadAttachment).`plantz$hasPlantOnHead`()) {
        this.`plantz$setPlantData`(entityTag)
        return true
    }
    return false
}
fun Entity.positionPlant(plant: Plant) {
    val moveFunction: MoveFunction = { entity: Entity, x: Double, y: Double, z: Double -> entity.setPos(x, y, z) }
    val position = this.headAttachmentPoint()
    val offset = plant.getVehicleAttachmentPoint(this)
    moveFunction.accept(plant,
        position.x - offset.x,
        position.y - offset.y,
        position.z - offset.z
    )
    plant.yBodyRot = this.yHeadRot
}

fun Player.hasSpaceForItem(item: ItemStack): Boolean {
    val inv = this.inventory
    val hasFreeSlot = inv.freeSlot != -1
    val hasSlotWithSpace = inv.getSlotWithRemainingSpace(item) != -1
    return hasFreeSlot || hasSlotWithSpace
}

fun Int.tickTimeFormat(): String = "%02d:%02d".format(
    (this / 20 / 60) % 60,
    (this / 20) % 60,
)

fun Entity.hasSameOwner(target: Entity?): Boolean {
    return if ((this is OwnableEntity && target is OwnableEntity))
        owner.let { it!=null && target.owner?.`is`(it) == true }
    else
        false
}

fun Entity.applyImpulse(xd: Double, yd: Double, zd: Double, pow: Float, uncertainty: Float) {
    val impulse = Vec3(xd, yd, zd)
        .normalize()
        .add(
            this.random.triangle(0.0, 0.0172275 * uncertainty),
            this.random.triangle(0.0, 0.0172275 * uncertainty),
            this.random.triangle(0.0, 0.0172275 * uncertainty)
        )
        .scale(pow.toDouble())
    this.deltaMovement = impulse
    this.needsSync = true
}

fun <T : LivingEntity?> ServerEntityGetter.getFurthestEntities(
    entities: MutableList<out T>,
    targetConditions: TargetingConditions,
    source: LivingEntity?,
    x: Double,
    y: Double,
    z: Double
): T? {
    var best = -1.0
    var result: T? = null

    for (entity in entities) {
        if (targetConditions.test(this.level, source, entity!!)) {
            val dist = entity.distanceToSqr(x, y, z)
            if (best == -1.0 || dist > best) {
                best = dist
                result = entity
            }
        }
    }

    return result
}

fun LookControl.lookAtBlockPos(pos: BlockPos) {
    this.setLookAt(pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, 10.0f, 10.0f)
}

fun Path?.canReachTarget(target: BlockPos?): Boolean {
    if (target==null) return false
    return this?.endNode?.let {
        target.distSqr(Vec3i(it.x, it.y, it.z)) <= 2.25
    }?: false
}

fun Path?.getEndPos(): BlockPos? = this?.endNode?.let { BlockPos(it.x, it.y, it.z) }

fun PathNavigation.moveToBlockPos(blockPos: BlockPos, speedModifier: Double) = this.moveTo(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), speedModifier)

fun List<String>.permutationsDescending(): List<String> = buildList {
    add(this@permutationsDescending.joinToString("_"))
    for (i in size - 1 downTo 1) {
        add(this@permutationsDescending.subList(0, i).joinToString(""))
    }
}

fun LivingEntity.getMagicName(): String {
    val name = this.customName?.string ?: return ""
    val hasMagicName: Boolean = when (this) {
        is Chomper -> name == "Chester"
        else -> false
    }
    return if (hasMagicName) name.lowercase() else ""
}

fun resolveTextureLocation(base: String, suffixes: List<String>, rm: ResourceManager): Identifier? {
    for (suffix in suffixes.permutationsDescending()) {
        if (suffix.isEmpty()) break
        val candidate = pazResource("${base}_${suffix}.png")
        if (rm.getResource(candidate).isPresent) return candidate
    }
    return null
}