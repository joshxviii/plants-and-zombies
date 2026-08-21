package joshxviii.plantz

import joshxviii.plantz.PazMain.MODID
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.raid.WaveType
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerEntityGetter
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.*
import net.minecraft.world.entity.Entity.MoveFunction
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.pathfinder.Path
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.sqrt

fun pazResource(path: String): Identifier = Identifier.fromNamespaceAndPath(MODID, path)

interface GardenHeroRewards {
    fun `plantz$getWaveList`(): MutableList<WaveType>
    fun `plantz$setWaveList`(value: MutableList<WaveType>)
    companion object {
        fun collectRewards(player: ServerPlayer): MutableList<ResourceKey<LootTable>> {
            player as GardenHeroRewards
            val rewards = player.`plantz$getWaveList`().mapIndexed { waveNumber, type ->
                type.lootTableFn(waveNumber, player.seenCredits)
            }.toMutableList()
            return rewards
        }
    }
}

interface PlantHeadAttachment {
    fun `plantz$hasPlantOnHead`(): Boolean
    fun `plantz$getPlant`(): Plant?
    fun `plantz$setPlant`(value: Plant?)
    fun `plantz$getPlantData`(): CompoundTag
    fun `plantz$setPlantData`(value: CompoundTag)
    companion object {
        fun potBlockInteraction(state: BlockState, level: Level, pos: BlockPos, player: Player) : InteractionResult {
            if (level !is ServerLevel) return InteractionResult.PASS
            val hasPlant = (player as PlantHeadAttachment).`plantz$hasPlantOnHead`()
            if (hasPlant && player.isShiftKeyDown) {
                val plant = player.`plantz$getPlant`()?: return InteractionResult.FAIL
                val plantBox = plant.boundingBox.move(pos.multiply(-1))
                val placePos = pos.above()

                if (level.getEntitiesOfClass(Plant::class.java, AABB(placePos)).isNotEmpty()) {
                    player.sendOverlayMessage(Component.translatable("message.plantz.already_planted").withStyle(ChatFormatting.RED))
                    return InteractionResult.FAIL
                }

                plant.detachFromEntity()
                plant.setPos(placePos.x.toDouble(), placePos.y.toDouble(), placePos.z.toDouble())
                plant.playSound(SoundEvents.HARNESS_UNEQUIP)// TODO custom sounds
                return InteractionResult.SUCCESS
            }
            return InteractionResult.PASS
        }
    }
}

fun Item.name(): Component = Component.translatable(this.descriptionId)

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

fun Player.removeSunFromStorageAndInventory(amount:Int = 1): Int {
    var remainder = amount

    val removedEnoughFromStorage = inventory.hasAnyMatching { itemStack ->
        val storedSun = itemStack.get(PazComponents.STORED_SUN) ?: return@hasAnyMatching false
        val sunToRemove = storedSun.storedSun.coerceAtMost(remainder)

        itemStack.set(PazComponents.STORED_SUN, storedSun.removeSun(sunToRemove))
        remainder -= sunToRemove

        remainder <= 0
    }

    if (!removedEnoughFromStorage) remainder -= removeItemFromInventory(PazItems.SUN, remainder)

    return remainder
}

fun Player.getTotalSun(): Int {
    var count: Int = getItemCount(PazItems.SUN)
    inventory.forEach { itemStack ->
        count += itemStack.get(PazComponents.STORED_SUN)?.storedSun ?: 0
    }
    return count
}

fun Player.getItemCount(itemType: Item): Int = inventory.countItem(itemType)

fun Player.removeItemFromInventory(itemType: Item, amount: Int = 1): Int {
    return inventory.clearOrCountMatchingItems({ it.`is`(itemType) }, amount, inventoryMenu.getCraftSlots())
}

fun Int.tickTimeFormat(): String = "%02d:%02d".format(
    (this / 20 / 60) % 60,
    (this / 20) % 60,
)

fun Int.tickSecondFormat(): String = "%2d".format(
    (this / 20),
)

fun Entity.hasSameRootOwner(target: Entity?): Boolean {
    if (target == null) return false

    if (this is Plant && this.isTame
        && (level() is ServerLevel && PazConfig.COOP_PLANTING)
        && ((target is TamableAnimal && target.isTame) || target is Player)) return true

    val owner = extractRootOwner(this) ?: return false
    if (owner.`is`(target)) return true

    val targetOwner = extractRootOwner(target) ?: return false

    return owner.`is`(targetOwner)
}

fun Entity?.isHypnotized(): Boolean {
    val self = this
    return self is LivingEntity && self.hasEffect(PazEffects.HYPNOTIZE)
}

fun DamageSource.isZombieFireworkExplosion(): Boolean {
    val hurtBy = entity ?: return false
    val isFirework = typeHolder().`is`(DamageTypes.FIREWORKS)
    return isFirework && hurtBy.`is`(PazTags.EntityTypes.ZOMBIE_RAIDERS)
}

fun extractRootOwner(entity: Entity): Entity? = when (entity) {
    is OwnableEntity -> entity.rootOwner
    is Projectile -> (entity.owner as? OwnableEntity)?.rootOwner ?: entity.owner
    else -> null
}

fun Entity.applyImpulse(xd: Double = 0.0, yd: Double = 1.0, zd: Double = 0.0, pow: Float = 1f, uncertainty: Float = 0f) {
    this.needsSync = true
    val impulse = Vec3(xd, yd, zd)
        .add(
            this.random.triangle(0.0, 0.0172275 * uncertainty),
            this.random.triangle(0.0, 0.0172275 * uncertainty),
            this.random.triangle(0.0, 0.0172275 * uncertainty)
        )
        .scale(pow.toDouble())
    this.addDeltaMovement(impulse)
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

fun Mob.attackRange(): Double {
    val DEFAULT = sqrt(2.04) - 0.6;
    val interactionRange = this.getAttribute(Attributes.ENTITY_INTERACTION_RANGE)?.value?: 0.0
    val attackRange = Mth.absMax(this.getActiveItem().get(DataComponents.ATTACK_RANGE)?.effectiveMaxRange(this)?.toDouble() ?: DEFAULT, interactionRange)
    val hitboxWidth = (this.boundingBox.xsize + this.boundingBox.ysize) / 2.0
    return hitboxWidth + attackRange
}

fun Path?.getEndPos(): BlockPos? = this?.endNode?.let { BlockPos(it.x, it.y, it.z) }

fun PathNavigation.moveToBlockPos(blockPos: BlockPos, speedModifier: Double) = this.moveTo(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), speedModifier)