package joshxviii.plantz.item

import joshxviii.plantz.*
import joshxviii.plantz.entity.plant.GraveBuster
import joshxviii.plantz.entity.plant.PacketInteractionResult
import joshxviii.plantz.entity.plant.Plant
import joshxviii.plantz.entity.plant.processSeedPacketInteraction
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SpawnEggItem
import net.minecraft.world.item.component.TypedEntityData
import net.minecraft.world.item.component.UseCooldown
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import java.util.*
import kotlin.jvm.optionals.getOrNull

class GardeningGloveItem(properties: Properties) : Item(properties) {

    override fun getName(itemStack: ItemStack): Component {
        val component = itemStack.get(DataComponents.ENTITY_DATA) ?: return super.getName(itemStack)
        val entityId = BuiltInRegistries.ENTITY_TYPE.getKey(component.type())

        val entityName = Component.translatable("entity.${entityId.namespace}.${entityId.path}")
        return Component.translatable("item.plantz.gardening_glove.entity", entityName)
    }

}
