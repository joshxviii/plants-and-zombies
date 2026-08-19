package joshxviii.plantz.mixin;

import com.mojang.serialization.Codec;
import joshxviii.plantz.*;
import joshxviii.plantz.effect.PaintedMobEffect;
import joshxviii.plantz.entity.plant.Plant;
import joshxviii.plantz.raid.WaveType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static joshxviii.plantz.PazDataSerializers.DATA_PAINT_COLORS;
import static joshxviii.plantz.PazItems.DUCKY_TUBE_DAMAGE_INTERVAL;

@Mixin(LivingEntity.class)
abstract public class LivingEntityMixin implements PlantHeadAttachment, GardenHeroRewards {

    @Unique
    private static final EntityDataAccessor<Boolean> DATA_HYPNO_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Boolean> DATA_FREEZE_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);
    @Unique
    private static final EntityDataAccessor<Map<Integer, Integer>> DATA_PAINTED_COLORS = SynchedEntityData.defineId(LivingEntity.class, DATA_PAINT_COLORS);

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow
    public int swingTime;

    @Shadow
    public abstract @org.jspecify.annotations.Nullable MobEffectInstance getEffect(Holder<MobEffect> effect);

    @Unique
    private CompoundTag plantData = new CompoundTag();

    @Unique
    private List<WaveType> completedRaidWaveList = List.of();

    @Unique
    private @Nullable Plant plantEntity = null;

    @Override
    public @Nullable Plant plantz$getPlant() {
        return plantEntity;
    }

    @Override
    public void plantz$setPlant(@Nullable Plant value) {
        plantEntity = value;
    }

    @Override
    public @NotNull CompoundTag plantz$getPlantData() {
        return plantData;
    }

    @Override
    public void plantz$setPlantData(@NotNull CompoundTag value) {
        plantData = value;
    }

    @Override
    public boolean plantz$hasPlantOnHead() {
        return plantEntity != null &&  plantEntity.isAlive() && !plantEntity.isRemoved();
    }

    @Override
    public @NotNull List<WaveType> plantz$getWaveList() {
        return completedRaidWaveList;
    }

    @Override
    public void plantz$setWaveList(@NotNull List<WaveType> value) {
        completedRaidWaveList = value;
    }



    @Unique
    public boolean plantz$getHypnoId() {
        return ((Entity) (Object) this).getEntityData().get(DATA_HYPNO_ID);
    }
    @Unique
    public boolean plantz$getFreezeId() {
        return ((Entity) (Object) this).getEntityData().get(DATA_FREEZE_ID);
    }
    @Unique
    public Map<Integer, Integer> plantz$getPaintedColors() {
        return ((Entity) (Object) this).getEntityData().get(DATA_PAINTED_COLORS);
    }

    @Unique
    private boolean prevFloatTag = false;
    @Unique
    private float prevWaterMalus = 0f;

    @Inject(method = "onEquipItem", at = @At("TAIL"))
    private void plantz$checkFloatTag(EquipmentSlot slot, ItemStack oldStack, ItemStack stack, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof PathfinderMob mob) {
            if (stack.is(PazItems.DUCKY_TUBE) && slot == EquipmentSlot.LEGS) {
                prevFloatTag = mob.getNavigation().canFloat();
                prevWaterMalus = mob.getPathfindingMalus(PathType.WATER);
                mob.getNavigation().setCanFloat(true);
            }
            else if (oldStack.is(PazItems.DUCKY_TUBE) && slot == EquipmentSlot.LEGS) {
                mob.getNavigation().setCanFloat(prevFloatTag);
                mob.setPathfindingMalus(PathType.WATER, 0.0F);
            }
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void plantz$applyDuckyTubeBuoyancy(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        var item = entity.getItemBySlot(EquipmentSlot.LEGS);
        if (!item.is(PazItems.DUCKY_TUBE) && !entity.is(PazTags.EntityTypes.PLANTABLE_ON_WATER)) return;
        if (entity instanceof Player player && player.getAbilities().flying) return;
        var fluidType = entity.level().getBlockState(BlockPos.containing(entity.position().add(0.0, entity.getBbHeight() * 0.5, 0.0))).getFluidState().getType();
        if (fluidType == Fluids.EMPTY ) return;

        //base
        double upwardForce = fluidType == Fluids.LAVA ? 0.1 : 0.015;
        // submerged
        if (entity.isEyeInFluid(FluidTags.WATER)) upwardForce = 0.135;
        if (entity.isEyeInFluid(FluidTags.LAVA)) upwardForce = 0.15;
        // sneaking
        if (entity.isShiftKeyDown()) upwardForce *= fluidType == Fluids.LAVA? 0.0 : 0.2;

        entity.addDeltaMovement(new Vec3(0.0, upwardForce, 0.0));

        entity.fallDistance = 0.0F;

        if (!entity.level().isClientSide() && entity.tickCount % DUCKY_TUBE_DAMAGE_INTERVAL==0 && entity.getRandom().nextFloat() > 0.5f)
            item.hurtAndBreak(1, entity, EquipmentSlot.LEGS);
    }

    @Inject(method = "defineSynchedData", at = @At(value = "TAIL"))
    public void defineData(SynchedEntityData.Builder entityData, CallbackInfo ci) {
        entityData.define(DATA_HYPNO_ID, false);
        entityData.define(DATA_FREEZE_ID, false);
        entityData.define(DATA_PAINTED_COLORS, new HashMap<>());
    }
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveCustomFlags(ValueOutput output, CallbackInfo ci) {
        var self = (LivingEntity) (Object) this;
        output.putBoolean("plantz:IsHypnotized", self.getEntityData().get(DATA_HYPNO_ID));
        output.putBoolean("plantz:IsFrozen", self.getEntityData().get(DATA_FREEZE_ID));
        output.store("plantz:PaintedColor", Codec.unboundedMap(Codec.INT, Codec.INT), self.getEntityData().get(DATA_PAINTED_COLORS));
        if (!this.plantz$getPlantData().isEmpty()) {
            output.store("plantz:AttachedPlant", CompoundTag.CODEC, this.plantz$getPlantData());
        }
        if (self instanceof ServerPlayer) output.store("plantz:CompletedWaveList", Codec.list(WaveType.Companion.getCODEC()), this.plantz$getWaveList());

    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadCustomFlags(ValueInput input, CallbackInfo ci) {
        var self = (LivingEntity) (Object) this;
        self.getEntityData().set(DATA_HYPNO_ID, input.getBooleanOr("plantz:IsHypnotized", false));
        self.getEntityData().set(DATA_FREEZE_ID, input.getBooleanOr("plantz:IsFrozen", false));
        self.getEntityData().set(DATA_PAINTED_COLORS, input.read("plantz:PaintedColor", Codec.unboundedMap(Codec.INT, Codec.INT)).orElseGet(HashMap::new));
        plantz$setPlantData(input.read("plantz:AttachedPlant", CompoundTag.CODEC).orElseGet(CompoundTag::new));
        if (self instanceof ServerPlayer) plantz$setWaveList(input.read("plantz:CompletedWaveList", Codec.list(WaveType.Companion.getCODEC())).orElseGet(List::of));

        if (self instanceof PathfinderMob mob) {
            prevFloatTag = mob.getNavigation().canFloat();
            prevWaterMalus = mob.getPathfindingMalus(PathType.WATER);
            if (mob.getItemBySlot(EquipmentSlot.LEGS).is(PazItems.DUCKY_TUBE)) {
                mob.getNavigation().setCanFloat(true);
                mob.setPathfindingMalus(PathType.WATER, 0.0F);
            }
        }
    }

    @Inject(method = "onEffectAdded", at = @At(value = "TAIL"))
    public void onHypnoAdded(MobEffectInstance effect, Entity source, CallbackInfo ci) {
        updateEffects();
    }
    @Inject(method = "onEffectsRemoved", at = @At(value = "TAIL"))
    public void onHypnoRemoved(Collection<MobEffectInstance> effects, CallbackInfo ci) {
        updateEffects();
    }
    @Unique
    public void updateEffects() {
        var self = (LivingEntity) (Object) this;
        self.getEntityData().set(DATA_HYPNO_ID, this.hasEffect(PazEffects.HYPNOTIZE));
        self.getEntityData().set(DATA_FREEZE_ID, this.hasEffect(PazEffects.FREEZE));
        self.getEntityData().set(DATA_PAINTED_COLORS, PaintedMobEffect.getPaintColors(self));
    }

    @Inject(method = "canBeAffected", at = @At(value = "RETURN"), cancellable = true)
    public void immuneToHypnosisOrFreeze(MobEffectInstance newEffect, CallbackInfoReturnable<Boolean> cir) {
        var entity = (LivingEntity) (Object) this;
        if (newEffect.is(PazEffects.HYPNOTIZE)) {
            cir.setReturnValue(!entity.is(PazTags.EntityTypes.CANNOT_HYPNOTIZE));
        }
        if (newEffect.is(PazEffects.FREEZE)) {
            cir.setReturnValue(entity.canFreeze());
        }
        updateEffects();
    }
    @Inject(method = "canAttack", at = @At(value = "RETURN"), cancellable = true)
    public void stopTargetingFriendlies(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (this.hasEffect(PazEffects.HYPNOTIZE) && (target instanceof Plant || target instanceof Player || target.hasEffect(PazEffects.HYPNOTIZE))) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    public void ownerIgnorePlantAttacks(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        var self = (LivingEntity) (Object) this;
        var sourceEntity = source.getEntity();
        var directEntity = source.getDirectEntity();
        if (source.is(DamageTypes.LIGHTNING_BOLT)) self.addEffect(new MobEffectInstance(PazEffects.ELECTRIFIED, 300, 1));
        if (source.is(PazTags.DamageTypes.IS_ELECTRIC) && self.is(PazTags.EntityTypes.IMMUNE_TO_ELECTRICITY)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
        if (sourceEntity instanceof Plant plant) {
            if (plant.hasSameOwner(self)) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
        else if (directEntity instanceof Plant plant) {
            if (plant.hasSameOwner(self)) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }


}