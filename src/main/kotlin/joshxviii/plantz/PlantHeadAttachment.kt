package joshxviii.plantz

import com.mojang.logging.LogUtils
import joshxviii.plantz.entity.plant.Plant
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.util.ProblemReporter
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.TagValueInput
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.use

interface PlantHeadAttachment {
    fun `plantz$getPlantPointer`(): Plant?
    fun `plantz$setPlantPointer`(value: Plant?)
    fun `plantz$getPlantId`(): Int
    fun `plantz$getPendingPlant`(): CompoundTag
    fun `plantz$setPendingPlant`(value: CompoundTag)

    companion object {
        private val LOGGER = LogUtils.getLogger()

        fun register() {
            UseBlockCallback.EVENT.register { player, level, hand, hitResult ->
                if (hand != InteractionHand.MAIN_HAND || !player.mainHandItem.isEmpty) InteractionResult.PASS
                else placeWornPlant(player, level, hitResult.blockPos, hitResult.direction)
            }
        }

        private fun LivingEntity.attachment() = this as PlantHeadAttachment

        @JvmStatic
        fun wornPlant(wearer: LivingEntity): Plant? {
            val attachment = wearer.attachment()
            val level = wearer.level()
            val plant = if (level.isClientSide) level.getEntity(attachment.`plantz$getPlantId`()) as? Plant
            else attachment.`plantz$getPlantPointer`()
            if (plant != null && !plant.isRemoved && plant.isAttachedTo(wearer)) return plant
            if (!level.isClientSide && attachment.`plantz$getPlantPointer`() != null) attachment.`plantz$setPlantPointer`(null)
            return null
        }

        @JvmStatic
        fun hasWornPlant(wearer: LivingEntity): Boolean =
            wornPlant(wearer) != null || !wearer.attachment().`plantz$getPendingPlant`().isEmpty

        fun link(wearer: LivingEntity, plant: Plant?) = wearer.attachment().`plantz$setPlantPointer`(plant)

        fun serializePlant(plant: Plant): CompoundTag = plant.saveWithId()

        @JvmStatic
        fun dataToSave(wearer: LivingEntity): CompoundTag? {
            wornPlant(wearer)?.takeIf { it.isAlive }?.let { return serializePlant(it) }
            return wearer.attachment().`plantz$getPendingPlant`().takeUnless { it.isEmpty }
        }

        @JvmStatic
        fun stash(wearer: LivingEntity) {
            val plant = wornPlant(wearer) ?: return
            val data = serializePlant(plant)
            link(wearer, null)
            plant.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER)
            if (!data.isEmpty) wearer.attachment().`plantz$setPendingPlant`(data)
        }

        @JvmStatic
        fun transfer(oldWearer: LivingEntity, newWearer: LivingEntity) {
            stash(oldWearer)
            val pending = oldWearer.attachment().`plantz$getPendingPlant`()
            oldWearer.attachment().`plantz$setPendingPlant`(CompoundTag())
            if (!pending.isEmpty) newWearer.attachment().`plantz$setPendingPlant`(pending)
        }

        @JvmStatic
        fun onWearerRemoved(wearer: LivingEntity, reason: Entity.RemovalReason) {
            if (wearer.level().isClientSide) return
            val plant = wornPlant(wearer) ?: return
            when {
                reason == Entity.RemovalReason.CHANGED_DIMENSION || reason.shouldSave() -> stash(wearer)
                else -> {
                    plant.detachFromEntity()
                    plant.dropAsSeedPacketItem(force = true, dropPumpkin = true)
                }
            }
        }

        @JvmStatic
        fun tickWearer(wearer: LivingEntity) {
            val level = wearer.level() as? ServerLevel ?: return
            val plant = wornPlant(wearer)
            if (plant != null && plant.level() !== level) stash(wearer)

            val pending = wearer.attachment().`plantz$getPendingPlant`()
            if (!pending.isEmpty) {
                wearer.attachment().`plantz$setPendingPlant`(CompoundTag())
                if (wornPlant(wearer) == null) spawnPending(wearer, level, pending)
            }

            wornPlant(wearer)?.let { wearer.positionPlant(it) }
        }

        private fun spawnPending(wearer: LivingEntity, level: ServerLevel, tag: CompoundTag) {
            val plant = ProblemReporter.ScopedCollector(wearer.problemPath(), LOGGER).use { reporter ->
                EntityType.loadEntityRecursive(TagValueInput.create(reporter, level.registryAccess(), tag), level, EntitySpawnReason.LOAD) { it }
            } as? Plant
            if (plant == null) {
                LOGGER.warn("Failed to restore plant worn by {}", wearer)
                return
            }
            val existing = level.getEntity(plant.uuid)
            if (existing != null) {
                if (existing is Plant && !existing.isRemoved && existing.isAttachedTo(wearer)) {
                    link(wearer, existing)
                    return
                }
                plant.setUUID(Mth.createInsecureUUID(level.random))
            }
            plant.restoreAttachment(wearer)
            wearer.positionPlant(plant)
            plant.setOldPosAndRot()
            if (!level.addFreshEntity(plant)) {
                link(wearer, null)
                LOGGER.warn("Failed to spawn plant worn by {}", wearer)
            }
        }

        fun placeWornPlant(player: Player, level: Level, clickedPos: BlockPos, face: Direction): InteractionResult {
            if (!player.isSecondaryUseActive || player.isSpectator || face != Direction.UP) return InteractionResult.PASS
            val plant = wornPlant(player) ?: return InteractionResult.PASS
            if (!plant.canPlaceOn(level.getBlockState(clickedPos))) return InteractionResult.PASS

            val placePos = clickedPos.above()
            val spaceFree = level.getBlockState(placePos).getCollisionShape(level, placePos).isEmpty
            if (!spaceFree) return InteractionResult.FAIL
            if (level.getEntitiesOfClass(Plant::class.java, AABB(placePos)).any { it !== plant }) {
                if (!level.isClientSide) {
                    player.sendOverlayMessage(Component.translatable("message.plantz.already_planted").withStyle(ChatFormatting.RED))
                }
                return InteractionResult.FAIL
            }
            if (level.isClientSide) return InteractionResult.SUCCESS

            plant.detachFromEntity()
            plant.deltaMovement = Vec3.ZERO
            plant.snapTo(Vec3.atBottomCenterOf(placePos))
            plant.playSound(SoundEvents.HARNESS_UNEQUIP)// TODO custom sounds
            return InteractionResult.SUCCESS_SERVER
        }
    }
}