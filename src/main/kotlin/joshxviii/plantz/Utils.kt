package joshxviii.plantz

import joshxviii.plantz.PazMain.MODID
import joshxviii.plantz.raid.ZombieRaid
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.Vec3i
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerEntityGetter
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.tags.PoiTypeTags
import net.minecraft.world.attribute.EnvironmentAttributes
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.ai.village.poi.PoiManager
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.entity.raid.Raid
import net.minecraft.world.entity.raid.Raids
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.gamerules.GameRules
import net.minecraft.world.level.pathfinder.Path
import net.minecraft.world.phys.Vec3

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

fun Path?.canReachTarget(target: BlockPos?): Boolean {
    if (target==null) return false
    return this?.endNode?.let {
        target.distSqr(Vec3i(it.x, it.y, it.z)) <= 2.25
    }?: false
}

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

private fun Raids.getOrCreateZombieRaid(level: ServerLevel, pos: BlockPos): ZombieRaid {
    val raid = level.getRaidAt(pos) as? ZombieRaid
    return raid ?: ZombieRaid(pos, level.difficulty)
}

fun Raids.createOrExtendZombieRaid(player: ServerPlayer, raidPosition: BlockPos): Raid? {
    if (player.isSpectator) return null
    else {
        val level = player.level()
        if (!level.gameRules.get(GameRules.RAIDS)) {
            return null
        } else if (!level.environmentAttributes()
                .getValue(EnvironmentAttributes.CAN_START_RAID, raidPosition)
        ) {
            return null
        } else {
            val posses = level.poiManager.getInRange(
                { e: Holder<PoiType> -> e.`is`(PoiTypeTags.VILLAGE) },
                raidPosition,
                64,
                PoiManager.Occupancy.IS_OCCUPIED
            ).toList()
            var count = 0
            var posTotals = Vec3.ZERO

            for (p in posses) {
                val pos = p.pos
                posTotals = posTotals.add(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
                count++
            }

            val raidCenterPos: BlockPos?
            if (count > 0) {
                posTotals = posTotals.scale(1.0 / count)
                raidCenterPos = BlockPos.containing(posTotals)
            } else {
                raidCenterPos = raidPosition
            }

            val raid: ZombieRaid = getOrCreateZombieRaid(level, raidCenterPos)
            if (!raid.isStarted) { }

            if (!raid.isStarted || raid.raidOmenLevel < raid.maxRaidOmenLevel) {
                raid.absorbRaidOmen(player)
            }

            this.setDirty()
            return raid
        }
    }
}