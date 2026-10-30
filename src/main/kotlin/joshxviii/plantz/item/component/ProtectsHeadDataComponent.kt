package joshxviii.plantz.item.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazSounds
import joshxviii.plantz.PazTags
import net.minecraft.ChatFormatting
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.RegistryCodecs
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.stats.Stats
import net.minecraft.tags.DamageTypeTags
import net.minecraft.util.ExtraCodecs
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipProvider
import net.minecraft.world.level.Level
import java.util.Optional
import java.util.function.Consumer

private const val BELOW_EYE_LEVEL_TOLERANCE = 0.25

class ProtectsHeadDataComponent(
    val slot: EquipmentSlotGroup = EquipmentSlotGroup.HEAD,
    val damageReductions: List<DamageReduction> = listOf(DamageReduction()),
    val itemDamage: ItemDamageFunction = ItemDamageFunction.DEFAULT,
    val blockSound: SoundEvent = SoundEvents.EMPTY
) : TooltipProvider {

    override fun addToTooltip(
        context: TooltipContext,
        consumer: Consumer<Component>,
        flag: TooltipFlag,
        components: DataComponentGetter
    ) {
        consumer.accept(Component.translatable("component.blocks_damage.description").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC))
    }

    fun canBlockFrom(source: DamageSource, user: LivingEntity): Boolean {
        if (source.`is`(DamageTypeTags.DAMAGES_HELMET)) return true
        val sourcePosition = source.directEntity?.let { it.boundingBox.center } ?: source.sourcePosition ?: return false
        return sourcePosition.y >= user.eyeY - BELOW_EYE_LEVEL_TOLERANCE && !(source.`is`(PazTags.DamageTypes.BYPASSES_HEAD_PROTECTION))
    }

    fun resolveBlockedDamage(source: DamageSource, dealtDamage: Float): Float {

        val blockedDamage = damageReductions.sumOf { it.resolve(source, dealtDamage).toDouble() }.toFloat()
        return Mth.clamp(blockedDamage, 0.0f, dealtDamage)
    }

    fun hurtBlockingItem(level: Level, item: ItemStack, user: LivingEntity, slot: EquipmentSlot, damage: Float) {
        if (BlockingItemBreakChance.handle(level, item, user, damage)) return

        if (user is Player && !level.isClientSide) {
            user.awardStat(Stats.ITEM_USED.get(item.item))
        }

        val durabilityDamage = itemDamage.apply(damage)
        if (durabilityDamage > 0) {
            item.hurtAndBreak(durabilityDamage, user, slot)
        }
    }

    fun onBlocked(level: ServerLevel, user: LivingEntity) {
        blockSound.let { sound ->
            level.playSound(null, user.x, user.y, user.z, sound, user.soundSource, 1.0f, 0.8f + level.random.nextFloat() * 0.4f)
        }
    }

    companion object {
        val CODEC: Codec<ProtectsHeadDataComponent> = RecordCodecBuilder.create { inst ->
            inst.group(
                EquipmentSlotGroup.CODEC.optionalFieldOf("slot", EquipmentSlotGroup.HEAD).forGetter { it.slot },
                DamageReduction.CODEC.listOf().optionalFieldOf("damage_reductions", listOf(DamageReduction())).forGetter { it.damageReductions },
                ItemDamageFunction.CODEC.optionalFieldOf("item_damage", ItemDamageFunction.DEFAULT).forGetter { it.itemDamage },
                SoundEvent.DIRECT_CODEC.fieldOf("block_sound").forGetter { it.blockSound }
            ).apply(inst, ::ProtectsHeadDataComponent)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ProtectsHeadDataComponent> = StreamCodec.composite(
            EquipmentSlotGroup.STREAM_CODEC,
            ProtectsHeadDataComponent::slot,
            DamageReduction.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ProtectsHeadDataComponent::damageReductions,
            ItemDamageFunction.STREAM_CODEC,
            ProtectsHeadDataComponent::itemDamage,
            SoundEvent.DIRECT_STREAM_CODEC,
            ProtectsHeadDataComponent::blockSound,
            ::ProtectsHeadDataComponent
        )
    }

    data class DamageReduction(
        val type: Optional<HolderSet<DamageType>> = Optional.empty(),
        val base: Float = 0.0f,
        val factor: Float = 0.9f
    ) {
        fun resolve(source: DamageSource, dealtDamage: Float): Float {
            if (type.isPresent && !type.get().contains(source.typeHolder())) return 0.0f
            return Mth.clamp(base + factor * dealtDamage, 0.0f, dealtDamage)
        }

        companion object {
            val CODEC: Codec<DamageReduction> = RecordCodecBuilder.create { inst ->
                inst.group(
                    RegistryCodecs.homogeneousList(Registries.DAMAGE_TYPE).optionalFieldOf("type").forGetter { it.type },
                    Codec.FLOAT.fieldOf("base").forGetter { it.base },
                    Codec.FLOAT.fieldOf("factor").forGetter { it.factor }
                ).apply(inst, ::DamageReduction)
            }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DamageReduction> = StreamCodec.composite(
                ByteBufCodecs.holderSet(Registries.DAMAGE_TYPE).apply(ByteBufCodecs::optional),
                DamageReduction::type,
                ByteBufCodecs.FLOAT,
                DamageReduction::base,
                ByteBufCodecs.FLOAT,
                DamageReduction::factor,
                ::DamageReduction
            )
        }
    }

    data class ItemDamageFunction(val threshold: Float, val base: Float, val factor: Float) {
        fun apply(dealtDamage: Float): Int = if (dealtDamage < threshold) 0 else Mth.floor(base + factor * dealtDamage)

        companion object {
            val DEFAULT = ItemDamageFunction(1.0f, 0.0f, 1.0f)

            val CODEC: Codec<ItemDamageFunction> = RecordCodecBuilder.create { inst ->
                inst.group(
                    ExtraCodecs.NON_NEGATIVE_FLOAT.fieldOf("threshold").forGetter { it.threshold },
                    Codec.FLOAT.fieldOf("base").forGetter { it.base },
                    Codec.FLOAT.fieldOf("factor").forGetter { it.factor }
                ).apply(inst, ::ItemDamageFunction)
            }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ItemDamageFunction> = StreamCodec.composite(
                ByteBufCodecs.FLOAT,
                ItemDamageFunction::threshold,
                ByteBufCodecs.FLOAT,
                ItemDamageFunction::base,
                ByteBufCodecs.FLOAT,
                ItemDamageFunction::factor,
                ::ItemDamageFunction
            )
        }
    }
}
