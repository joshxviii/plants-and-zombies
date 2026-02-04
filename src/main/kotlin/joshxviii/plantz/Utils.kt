package joshxviii.plantz

import joshxviii.plantz.PazMain.MODID
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerEntityGetter
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.control.LookControl
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.pathfinder.Path

fun pazResource(path: String): Identifier = Identifier.fromNamespaceAndPath(MODID, path)

fun ItemStack.canBlockDamage(source: DamageSource): Boolean {
    return this.components.has(PazComponents.BLOCKS_PROJECTILE_DAMAGE) && source.`is`(PazTags.DamageTypes.BLOCKABLE_DAMAGE)
}

fun LivingEntity.canArmorAbsorbDamage(source: DamageSource): Boolean {
    val slots = setOf(EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND)
    slots.forEach { slot ->
       if (this.getItemBySlot(slot).canBlockDamage(source)) return true
    }
    return false
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
    add("")
}

fun resolveTextureLocation(base: String, suffixes: List<String>, rm: ResourceManager): Identifier? {
    for (suffix in suffixes.permutationsDescending()) {
        if (suffix.isEmpty()) break
        val candidate = pazResource("${base}_${suffix}.png")
        if (rm.getResource(candidate).isPresent) return candidate
    }
    return null
}