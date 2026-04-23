package joshxviii.plantz.mixin;

import joshxviii.plantz.PazEffects;
import joshxviii.plantz.PazTags;
import joshxviii.plantz.PlantHeadAttachment;
import joshxviii.plantz.entity.plant.Plant;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

@Mixin(LivingEntity.class)
abstract public class LivingEntityMixin implements PlantHeadAttachment {

    @Unique
    private static final EntityDataAccessor<Boolean> DATA_HYPNO_ID = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);

    @Shadow
    public abstract boolean hasEffect(Holder<MobEffect> effect);

    @Unique
    private CompoundTag plantEntityOnHead = new CompoundTag();

    @Override
    public @NotNull CompoundTag plantz$getPlantEntityOnHead() {
        return plantEntityOnHead;
    }

    @Override
    public void plantz$setPlantEntityOnHead(@NotNull CompoundTag value) {
        plantEntityOnHead = value;
    }

    @Override
    public boolean plantz$hasPlantOnHead() {
        return !plantEntityOnHead.isEmpty();
    }

    @Unique
    public boolean plantz$getHypnoId() {
        return ((Entity) (Object) this).getEntityData().get(DATA_HYPNO_ID);
    }

    @Inject(method = "defineSynchedData", at = @At(value = "TAIL"))
    public void defineData(SynchedEntityData.Builder entityData, CallbackInfo ci) {
        entityData.define(DATA_HYPNO_ID, false);
    }
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveHypnoFlag(ValueOutput output, CallbackInfo ci) {
        output.putBoolean("plantz:IsHypnotized", ((Entity) (Object) this).getEntityData().get(DATA_HYPNO_ID));
        if (!this.plantz$getPlantEntityOnHead().isEmpty()) {
            output.store("PlantEntityOnHead", CompoundTag.CODEC, this.plantz$getPlantEntityOnHead());
        }
    }
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadHypnoFlag(ValueInput input, CallbackInfo ci) {
        ((Entity) (Object) this).getEntityData().set(DATA_HYPNO_ID, input.getBooleanOr("plantz:IsHypnotized", false));
        plantz$setPlantEntityOnHead(input.read("PlantEntityOnHead", CompoundTag.CODEC).orElseGet(CompoundTag::new));
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