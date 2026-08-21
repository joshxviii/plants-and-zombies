package joshxviii.plantz.block.entity

import com.mojang.serialization.Codec
import joshxviii.plantz.GardenHeroRewards
import joshxviii.plantz.MailboxData
import joshxviii.plantz.PazBlocks
import joshxviii.plantz.PazCriteria
import joshxviii.plantz.PazEffects
import joshxviii.plantz.PazLootTables
import joshxviii.plantz.PazServerParticles
import joshxviii.plantz.block.MailboxBlock.Companion.FACING
import joshxviii.plantz.block.MailboxBlock.Companion.STATE
import joshxviii.plantz.block.MailboxState
import joshxviii.plantz.effect.GardenHeroEffect
import joshxviii.plantz.inventory.MailboxMenu
import joshxviii.plantz.raid.ZombieRaid.Companion.TACO_TIME_WAVE
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.Containers
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.minecart.Minecart
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import kotlin.jvm.optionals.getOrDefault

class MailboxBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState,
    var color : DyeColor = DyeColor.WHITE,
) : BaseContainerBlockEntity(PazBlocks.MAILBOX_ENTITY, worldPosition, blockState), ExtendedMenuProvider<MailboxData> {
    private var name: Component? = null
    private var ejectTimer: Int = 0
    private var tickCount : Int = 0
    private var heroMailBuffer: MutableList<ResourceKey<LootTable>> = mutableListOf()
    private var heroMailIndex: Int = 0

    companion object {
        const val HERO_MAIL_EJECT_DELAY = 10
        val DEFAULT_NAME = Component.translatable("item.plantz.mailbox");

        fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: MailboxBlockEntity) {
            blockEntity.tickCount++

            if (level.isClientSide && blockEntity.tickCount % 25 == 0) {
                blockEntity.tickCount += level.random.nextInt(3)
                if (state.getValue(STATE) == MailboxState.HAS_MAIL)
                    level.addParticle(PazServerParticles.NOTIFY, pos.x+0.5, pos.y+0.8, pos.z+0.5, 0.0, 0.0, 0.0)
                return
            }

            if (state.getValue(STATE) == MailboxState.EJECTING) {
                val buffer = blockEntity.heroMailBuffer
                if (buffer.isNotEmpty()) {// Hero Mail Rewards
                    val dropPos = blockEntity.blockState.getValue(FACING).unitVec3.scale(0.75).add(blockEntity.blockPos.center)
                    if (blockEntity.ejectTimer % HERO_MAIL_EJECT_DELAY == 0) buffer.getOrNull(blockEntity.heroMailIndex)?.let {
                        val items = blockEntity.getHeroMail(it).toMutableList()

                        if (blockEntity.heroMailIndex == TACO_TIME_WAVE-1) {// Add taco reward when reaching wave 10
                            items.addAll(blockEntity.getHeroMail(PazLootTables.MAIL_REWARDS_TACO))
                            blockEntity.playSound(SoundEvents.PLAYER_LEVELUP, 1.6f)// TODO add surprise horn taco sfx
                            (level as? ServerLevel)?.sendParticles(
                                PazServerParticles.CONFETTI,
                                dropPos.x, dropPos.y, dropPos.z, 16,
                                0.1, 0.2, 0.1, 0.075
                            )
                        }

                        items.forEach { item ->
                            Containers.dropItemStack(level, dropPos.x, dropPos.y, dropPos.z, item)
                        }

                        blockEntity.playSound(SoundEvents.VAULT_EJECT_ITEM, (blockEntity.heroMailIndex / buffer.size.toFloat()) * 0.2f + 1.0f)
                        blockEntity.heroMailIndex++
                        if (blockEntity.heroMailIndex >= buffer.size) {
                            buffer.clear()
                            blockEntity.heroMailIndex = 0
                        }
                    }
                }
                if (blockEntity.ejectTimer > 0) {
                    blockEntity.ejectTimer--
                } else {
                    blockEntity.updateMailboxState(MailboxState.INACTIVE)
                    blockEntity.ejectTimer = 0
                    blockEntity.setChanged()
                    blockEntity.playSound(SoundEvents.VAULT_CLOSE_SHUTTER, 1.7f)
                }
            }

            if (blockEntity.tickCount % 100 == 0) {
                (level as? ServerLevel)?.getMailboxMailQueue()?.deliverTo(blockEntity)
            }
        }
    }

    private val inventory = SimpleContainer(5)
    override fun getContainerSize(): Int = 5

    override fun createMenu(containerId: Int, inventory: Inventory): AbstractContainerMenu = MailboxMenu(containerId, inventory, asMailBoxData())
    override fun getScreenOpeningData(player: ServerPlayer): MailboxData = asMailBoxData()

    fun tryToGetMail(player: Player): Boolean {
        val currentState = blockState.getValue(STATE)
        val heroEffect = player.getEffect(PazEffects.GARDEN_HERO)?.effect

        val dropPos = blockState.getValue(FACING).unitVec3.scale(0.75).add(blockPos.center)
        if (heroEffect != null) {
            if (player !is ServerPlayer) return false
            heroMailBuffer = GardenHeroRewards.collectRewards(player)
            updateMailboxState(MailboxState.EJECTING)
            ejectTimer = HERO_MAIL_EJECT_DELAY * heroMailBuffer.size
            player.removeEffect(heroEffect)
            PazCriteria.RECEIVE_HERO_MAIL.trigger(player, true)
            setChanged()
            return true
        }
        return when (currentState) {
            MailboxState.HAS_MAIL -> {
                items.forEach {
                    Containers.dropItemStack(level!!, dropPos.x, dropPos.y, dropPos.z, it)
                }
                playSound(SoundEvents.VAULT_EJECT_ITEM)
                updateMailboxState(MailboxState.EJECTING)
                ejectTimer = 25
                setChanged()
                true
            }
            else -> false
        }
    }

    fun getHeroMail(lootTable: ResourceKey<LootTable>): List<ItemStack> {
        val level = level as? ServerLevel ?: return emptyList()
        val params: LootParams = LootParams.Builder(level)
            .withParameter(LootContextParams.ORIGIN, blockPos.center)
            .create(LootContextParamSets.CHEST)
        val lootTable: LootTable = level.server.reloadableRegistries().getLootTable(lootTable)
        val items = lootTable.getRandomItems(params)
        return items
    }

    fun updateMailboxState(newState: MailboxState) {
        level!!.setBlock(blockPos, blockState.setValue(STATE, newState), 3)
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.store("Color", DyeColor.CODEC, color)
        output.storeNullable<Component>("CustomName", ComponentSerialization.CODEC, this.name)
        output.store("EjectTimer", Codec.INT, ejectTimer)
        ContainerHelper.saveAllItems(output, inventory.items)
    }
    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        color = input.read("Color", DyeColor.CODEC).getOrDefault(DyeColor.WHITE)
        this.name = parseCustomNameSafe(input, "CustomName")
        ejectTimer = input.getInt("EjectTimer").get()
        ContainerHelper.loadAllItems(input, inventory.items)
    }

    override fun getName(): Component = this.name?: blockState.block.name
    override fun getDisplayName(): Component = this.name?: blockState.block.name
    override fun getDefaultName(): Component = DEFAULT_NAME

    override fun getItems(): NonNullList<ItemStack> = inventory.items
    override fun setItems(items: NonNullList<ItemStack>) {}

    override fun canPlaceItem(slot: Int, itemStack: ItemStack): Boolean {
        return if (blockState.getValue(STATE) == MailboxState.EJECTING) false
        else super.canPlaceItem(slot, itemStack)
    }
    override fun canTakeItem(into: Container, slot: Int, itemStack: ItemStack): Boolean {
        return if (blockState.getValue(STATE) == MailboxState.EJECTING) false
        else super.canTakeItem(into, slot, itemStack)
    }

    override fun setChanged() {
        super.setChanged()
        if (blockState.getValue(STATE) == MailboxState.EJECTING) return
        if (isEmpty) updateMailboxState(MailboxState.INACTIVE)
        else updateMailboxState(MailboxState.HAS_MAIL)
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }
    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return this.saveWithoutMetadata(registries)
    }
    override fun applyImplicitComponents(components: DataComponentGetter) {
        super.applyImplicitComponents(components)
        this.name = components.get<Component>(DataComponents.CUSTOM_NAME)
    }
    override fun collectImplicitComponents(components: DataComponentMap.Builder) {
        super.collectImplicitComponents(components)
        components.set<Component>(DataComponents.CUSTOM_NAME, this.name)
    }
    override fun removeComponentsFromTag(output: ValueOutput) {
        output.discard("CustomName")
    }
    fun playSound(event: SoundEvent, pitch: Float = 0.9f) {
        val direction = blockState.getValue(FACING).unitVec3i
        val x = worldPosition.x + 0.5 + direction.x / 2.0
        val y = worldPosition.y + 0.5 + direction.y / 2.0
        val z = worldPosition.z + 0.5 + direction.z / 2.0
        level!!.playSound(
            null, x, y, z, event, SoundSource.BLOCKS, 0.5f, level!!.getRandom().nextFloat() * 0.1f + pitch
        )
    }
    fun asMailBoxData(): MailboxData {
        return MailboxData(
            blockPos,
            color.textColor,
            getName(),
        )
    }
}
