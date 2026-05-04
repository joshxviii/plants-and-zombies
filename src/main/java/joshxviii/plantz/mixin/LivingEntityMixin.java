package joshxviii.plantz.mixin;

import joshxviii.plantz.PazEffects;
import joshxviii.plantz.PazItems;
import joshxviii.plantz.PazTags;
import joshxviii.plantz.PlantHeadAttachment;
import joshxviii.plantz.entity.plant.Plant;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

import static joshxviii.plantz.PazItems.DUCKY_TUBE_DAMAGE_INTERVAL;

@Mixin(LivingEntity.class)
abstract public class LivingEntityMixin implements PlantHeadAttachment {

    @Unique
    private static final EntityDataAccessor<Boolean> DATA_HYPNO_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Shadow
    public int swingTime;
    @Unique
    private CompoundTag plantData = new CompoundTag();

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

    @Unique
    public boolean plantz$getHypnoId() {
        return ((Entity) (Object) this).getEntityData().get(DATA_HYPNO_ID);
    }

    @Unique
    private boolean prevFloatTag = false;

    @Inject(method = "onEquipItem", at = @At("TAIL"))
    private void plantz$checkFloatTag(EquipmentSlot slot, ItemStack oldStack, ItemStack stack, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof PathfinderMob mob) {
            if (stack.is(PazItems.DUCKY_TUBE) && slot == EquipmentSlot.LEGS) {
                prevFloatTag = mob.getNavigation().canFloat();
                mob.getNavigation().setCanFloat(true);
            }
            else if (oldStack.is(PazItems.DUCKY_TUBE) && slot == EquipmentSlot.LEGS) mob.getNavigation().setCanFloat(prevFloatTag);
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void plantz$applyDuckyTubeBuoyancy(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        var item = entity.getItemBySlot(EquipmentSlot.LEGS);
        if (!item.is(PazItems.DUCKY_TUBE)) return;
        if (entity.level().getBlockState(entity.blockPosition().above()).getFluidState().getType() != Fluids.WATER ) return;

        double upwardForce = 0.015;
        if (entity.isEyeInFluid(FluidTags.WATER)) upwardForce = 0.135;
        if (entity.isShiftKeyDown()) upwardForce *= 0.2;

        entity.addDeltaMovement(new Vec3(0.0, upwardForce, 0.0));

        entity.fallDistance = 0.0F;

        if (!entity.level().isClientSide() && entity.tickCount % DUCKY_TUBE_DAMAGE_INTERVAL==0 && entity.getRandom().nextFloat() > 0.5f)
            item.hurtAndBreak(1, entity, EquipmentSlot.LEGS);
    }

    @Inject(method = "defineSynchedData", at = @At(value = "TAIL"))
    public void defineData(SynchedEntityData.Builder entityData, CallbackInfo ci) {
        entityData.define(DATA_HYPNO_ID, false);
    }
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveHypnoFlag(ValueOutput output, CallbackInfo ci) {
        output.putBoolean("plantz:IsHypnotized", ((Entity) (Object) this).getEntityData().get(DATA_HYPNO_ID));
        if (!this.plantz$getPlantData().isEmpty()) {
            output.store("plantz:AttachedPlant", CompoundTag.CODEC, this.plantz$getPlantData());
        }
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadHypnoFlag(ValueInput input, CallbackInfo ci) {
        ((Entity) (Object) this).getEntityData().set(DATA_HYPNO_ID, input.getBooleanOr("plantz:IsHypnotized", false));
        plantz$setPlantData(input.read("plantz:AttachedPlant", CompoundTag.CODEC).orElseGet(CompoundTag::new));
        if ((LivingEntity) (Object) this instanceof PathfinderMob mob) {
            prevFloatTag = mob.getNavigation().canFloat();
            if (mob.getItemBySlot(EquipmentSlot.LEGS).is(PazItems.DUCKY_TUBE)) mob.getNavigation().setCanFloat(true);
        }
    }
    @Inject(method = "onEffectAdded", at = @At(value = "TAIL"))
    public void onHypnoAdded(MobEffectInstance effect, Entity source, CallbackInfo ci) {
        ((Entity) (Object) this).getEntityData().set(DATA_HYPNO_ID, this.hasEffect(PazEffects.HYPNOTIZE));
    }
    @Inject(method = "onEffectsRemoved", at = @At(value = "TAIL"))
    public void onHypnoRemoved(Collection<MobEffectInstance> effects, CallbackInfo ci) {
        ((Entity) (Object) this).getEntityData().set(DATA_HYPNO_ID, this.hasEffect(PazEffects.HYPNOTIZE));
    }
    @Inject(method = "canBeAffected", at = @At(value = "RETURN"), cancellable = true)
    public void immuneToHypnosis(MobEffectInstance newEffect, CallbackInfoReturnable<Boolean> cir) {
        if (newEffect.is(PazEffects.HYPNOTIZE)) {
            cir.setReturnValue(!((Entity) (Object) this).is(PazTags.EntityTypes.CANNOT_HYPNOTIZE));
        }
    }
    @Inject(method = "canAttack", at = @At(value = "RETURN"), cancellable = true)
    public void stopTargetingFriendlies(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (this.hasEffect(PazEffects.HYPNOTIZE) && (target instanceof Plant || target instanceof Player || target.hasEffect(PazEffects.HYPNOTIZE))) {
            cir.setReturnValue(false);
        }
    }
}