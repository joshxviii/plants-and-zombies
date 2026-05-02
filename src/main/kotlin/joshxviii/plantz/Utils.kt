package joshxviii.plantz

import joshxviii.plantz.PazMain.MODID
import joshxviii.plantz.entity.plant.Chomper
import joshxviii.plantz.entity.plant.Plant
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerEntityGetter
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
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
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potions
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

fun Entity.canWearPlant(): Boolean {
    return this is LivingEntity && this.getItemBySlot(EquipmentSlot.HEAD).`is`(PazItems.PLANT_POT_HELMET)
            && this.isAlive && !this.isDeadOrDying && !this.isRemoved
            && !(this is ServerPlayer && (this.isSpectator))
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
    val scale = (this as? LivingEntity)?.scale ?: 1.0f
    val pitch = this.xRot * Mth.DEG_TO_RAD
    val yaw = this.yRot * Mth.DEG_TO_RAD

    val rotated = Vec3(0.0, 0.55 * scale, 0.0).xRot(-pitch).yRot(-yaw)
    val position = this.position().add(
        rotated.x,
        this.eyeHeight + rotated.y - 0.25 * scale,
        rotated.z
    )
    val offset = plant.getVehicleAttachmentPoint(this)
    moveFunction.accept(plant,
        position.x - offset.x,
        position.y - offset.y,
        position.z - offset.z
    )
    plant.yBodyRot = this.yHeadRot
}

fun Player.hasSpaceForSun(item: ItemStack): Boolean {
    val inv = this.inventory
    val hasFreeSlot = inv.freeSlot != -1
    val hasSlotWithSpace = inv.getSlotWithRemainingSpace(item) != -1

    val hasSunStorageItemWithSpace = inv.hasAnyMatching { it.get(PazComponents.STORED_SUN)?.hasRoomForSun(1) == true }

    return ((hasFreeSlot || hasSlotWithSpace) && !this.hasInfiniteMaterials()) || hasSunStorageItemWithSpace
}

fun Player.tryAddSunToStorage(amount:Int = 1): Boolean {
    return inventory.hasAnyMatching { itemStack ->
        val storedSun = itemStack.get(PazComponents.STORED_SUN) ?: return@hasAnyMatching false
        if (!storedSun.hasRoomForSun(amount))return@hasAnyMatching false

        itemStack.set(PazComponents.STORED_SUN, storedSun.addSun(amount))
        true
    }
}

fun Player.removeSunFromStorageAndInventory(amount:Int = 1): Boolean {
    var remainder = amount

    val removedEnoughFromStorage = inventory.hasAnyMatching { itemStack ->
        val storedSun = itemStack.get(PazComponents.STORED_SUN) ?: return@hasAnyMatching false
        val sunToRemove = storedSun.storedSun.coerceAtMost(remainder)

        itemStack.set(PazComponents.STORED_SUN, storedSun.removeSun(sunToRemove))
        remainder -= sunToRemove

        remainder <= 0
    }

    if (!removedEnoughFromStorage) {
        val removedFromInventory = inventory.clearOrCountMatchingItems(
            { it.`is`(PazItems.SUN) },
            remainder,
            inventoryMenu.getCraftSlots()
        )
        remainder -= removedFromInventory
    }

    return remainder <= 0
}

fun Player.getTotalSun(): Int {
    var count: Int = 0
    count += inventory.countItem(PazItems.SUN)
    inventory.forEach { itemStack ->
        count += itemStack.get(PazComponents.STORED_SUN)?.storedSun ?: 0
    }
    return count
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


// AI/PATHFINDING
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

// MODEL RENDERING
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