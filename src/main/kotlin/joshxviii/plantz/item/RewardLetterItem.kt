package joshxviii.plantz.item

import joshxviii.plantz.PazComponents
import joshxviii.plantz.PazItems
import joshxviii.plantz.PazLootTables
import joshxviii.plantz.PazSounds
import joshxviii.plantz.item.component.RewardLetterDataComponent
import joshxviii.plantz.raid.WaveType
import joshxviii.plantz.raid.CompletedWave
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomModelData
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3

class RewardLetterItem(properties: Properties) : Item(properties) {

    override fun getName(itemStack: ItemStack): Component {
        val data = itemStack.get(PazComponents.REWARD_LETTER_DATA) ?: return super.getName(itemStack)
        val waveKey = if (data.wave == WaveType.DEFAULT) {
            if (data.lootTables.contains(PazLootTables.MAIL_REWARD_DEFAULT_HARD.identifier())) "default_hard" else "default_easy"
        } else data.wave.name.lowercase()
        return Component.translatable("item.plantz.reward_letter", Component.translatable("event.plantz.zombie_raid.wave.$waveKey"))
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        val itemStack = player.getItemInHand(hand)
        val data = itemStack.get(PazComponents.REWARD_LETTER_DATA) ?: return InteractionResult.PASS

        if (level is ServerLevel) {
            for (id in data.lootTables) {
                rollLoot(level, player.position(), ResourceKey.create(Registries.LOOT_TABLE, id), data.wave.brains.random()).forEach { loot ->
                    level.addFreshEntity(ItemEntity(level, player.x, player.y, player.z, loot))
                }
            }
            itemStack.shrink(1)
        }
        player.playSound(SoundEvents.UI_LOOM_SELECT_PATTERN, 1f, 1f)// TODO custom sounds
        player.awardStat(Stats.ITEM_USED.get(this))
        return InteractionResult.SUCCESS
    }

    companion object {
        fun create(reward: CompletedWave, index: Int = 0): ItemStack {
            val lootTables = listOf(reward.type.lootTableFn(index).identifier())
            val stack = ItemStack(PazItems.REWARD_LETTER)
            stack.set(PazComponents.REWARD_LETTER_DATA, RewardLetterDataComponent(reward.type, lootTables))
            stack.set(DataComponents.CUSTOM_MODEL_DATA, CustomModelData(emptyList(), emptyList(), emptyList(), listOf(reward.type.color)))
            return stack
        }

        fun rollLoot(level: ServerLevel, origin: Vec3, lootTable: ResourceKey<LootTable>, brainCount: Int): List<ItemStack> {
            val brains = PazItems.BRAIN.defaultInstance.copyWithCount(brainCount)
            val params = LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, origin)
                .create(LootContextParamSets.CHEST)
            return level.server.reloadableRegistries().getLootTable(lootTable).getRandomItems(params).plus(brains)
        }
    }
}
