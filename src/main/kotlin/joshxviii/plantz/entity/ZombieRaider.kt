package joshxviii.plantz.entity

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.world.entity.monster.zombie.Zombie
import java.util.UUID

/** Persisted snapshot of a raider, used to re-link zombies after their chunk unloads or the world reloads. */
data class RaiderRecord(val uuid: UUID, val wave: Int, val health: Float, val pos: BlockPos, val isLeader: Boolean, val isBoss: Boolean) {
    companion object {
        val CODEC: Codec<RaiderRecord> = RecordCodecBuilder.create { i ->
            i.group(
                UUIDUtil.CODEC.fieldOf("uuid").forGetter(RaiderRecord::uuid),
                Codec.INT.fieldOf("wave").forGetter(RaiderRecord::wave),
                Codec.FLOAT.fieldOf("health").forGetter(RaiderRecord::health),
                BlockPos.CODEC.fieldOf("pos").forGetter(RaiderRecord::pos),
                Codec.BOOL.fieldOf("is_leader").forGetter(RaiderRecord::isLeader),
                Codec.BOOL.fieldOf("is_boss").forGetter(RaiderRecord::isBoss)
            ).apply(i, ::RaiderRecord)
        }
        fun save(zombie: Zombie, wave: Int) = RaiderRecord(zombie.uuid, wave, zombie.getEffectiveRaidHealth(), zombie.blockPosition(), zombie.isLeader(), zombie.isBoss())
        fun loadInto(zombie: Zombie, record: RaiderRecord) {
            zombie.uuid = record.uuid
            if (zombie !is ZombieRaider) return
            zombie.`plantz$setIsFromRaid`(true)
            zombie.`plantz$setIsLeader`(record.isLeader)
            zombie.`plantz$setIsBoss`(record.isBoss)
        }
    }
}

interface ZombieRaider {
    fun `plantz$getIsFromRaid`(): Boolean
    fun `plantz$setIsFromRaid`(value: Boolean)
    fun `plantz$getAddsHealthToRaid`(): Boolean
    fun `plantz$setAddsHealthToRaid`(value: Boolean)
    fun `plantz$setIsLeader`(value: Boolean)
    fun `plantz$getIsLeader`(): Boolean
    fun `plantz$getIsBoss`(): Boolean
    fun `plantz$setIsBoss`(value: Boolean)
}

fun Zombie.getEffectiveRaidHealth(): Float {
    return if (this is ZombieRaider && this.`plantz$getAddsHealthToRaid`()) this.health else 0f
}

fun Zombie.setCountHealth(value: Boolean) {
    if (this is ZombieRaider) this.`plantz$setAddsHealthToRaid`(value)
}

fun Zombie.isActiveInRaid(): Boolean = this is ZombieRaider && this.`plantz$getIsFromRaid`() && this.isAlive

fun Zombie.isBoss(): Boolean = this is ZombieRaider && this.`plantz$getIsBoss`()

fun Zombie.isLeader(): Boolean = this is ZombieRaider && this.`plantz$getIsLeader`()