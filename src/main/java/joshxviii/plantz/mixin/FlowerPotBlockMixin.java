package joshxviii.plantz.mixin;

import joshxviii.plantz.PazComponents;
import joshxviii.plantz.entity.Plant;
import joshxviii.plantz.item.SeedPacketItem;
import joshxviii.plantz.item.component.SeedPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowerPotBlock.class)
public class FlowerPotBlockMixin {

    @Shadow
    @Final
    private Block potted;

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true, remap = false)
    private void onUseItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {

        // Check if the item is a SeedPacketItem
        if (itemStack.getItem() instanceof SeedPacketItem) {
            // Check if the flower pot is empty (potted block is AIR)
            if (this.potted != Blocks.AIR) {
                cir.setReturnValue(InteractionResult.CONSUME);
                return;
            }

            SeedPacket seedPacket = itemStack.get(PazComponents.SEED_PACKET);
            if (seedPacket == null || seedPacket.getEntityId() == null) {
                cir.setReturnValue(InteractionResult.PASS);
                return;
            }

            if (level.isClientSide()) {
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }

            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            player.awardStat(Stats.POT_FLOWER);

            Identifier entityId = seedPacket.getEntityId();
            var entityTypeHolder = BuiltInRegistries.ENTITY_TYPE.get(entityId);
            if (entityTypeHolder.isPresent()) {
                EntityType<?> entityType = entityTypeHolder.get().value();
                var entity = entityType.spawn((ServerLevel) level, pos, EntitySpawnReason.SPAWN_ITEM_USE);
                if (entity instanceof Plant) {
                    ((Plant) entity).setPotted(true);
                    ((Plant) entity).tame(player);
                    level.addFreshEntity(entity);
                    entity.playSound(SoundEvents.BIG_DRIPLEAF_PLACE, 1.0f, 1.0f);
                }
            }

            itemStack.consume(1, player);

            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
