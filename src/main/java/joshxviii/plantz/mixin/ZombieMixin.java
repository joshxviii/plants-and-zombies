package joshxviii.plantz.mixin;

import joshxviii.plantz.*;
import joshxviii.plantz.entity.zombie.Gargantuar;
import joshxviii.plantz.entity.zombie.PazZombie;
import joshxviii.plantz.entity.zombie.ZombieYeti;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

import static joshxviii.plantz.entity.zombie.PazZombie.LEADER_MODIFIER_ID;

/**
 * @author Josh
 */
@Mixin(Zombie.class)
public class ZombieMixin implements ZombieRaider {

    @Unique
    static private final EntityDataAccessor<Boolean> IS_FROM_RAID  = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);

    @Override
    public boolean plantz$getIsFromRaid() {
        return ((Entity) (Object) this).getEntityData().get(IS_FROM_RAID);
    }

    @Override
    public void plantz$setIsFromRaid(boolean value) {
        ((Entity) (Object) this).getEntityData().set(IS_FROM_RAID, value);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    public void syncedData(SynchedEntityData.Builder entityData, CallbackInfo ci) {
        entityData.define(IS_FROM_RAID, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveCustomFlags(ValueOutput output, CallbackInfo ci) {
        output.putBoolean("plantz:IsFromRaid", this.plantz$getIsFromRaid());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadCustomFlags(ValueInput input, CallbackInfo ci) {
        this.plantz$setIsFromRaid(input.getBooleanOr("plantz:IsFromRaid", false));
    }

    @ModifyArgs(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Monster;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void updateDamage(Args args) {
        DamageSource source = args.get(1);
        float damage = args.get(2);
        if (source.is(PazDamageTypes.PLANT_EXPLODE)) args.set(2, damage*3f);
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    public void zombieAoeIgnore(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        Zombie entity = (Zombie) (Object) this;
        if (source.is(PazDamageTypes.ZOMBIE_SMASH) && !entity.hasEffect(PazEffects.HYPNOTIZE)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }


    @Unique
    private static final float BONUS_LEADER_CHANCE = 0.0015F;
    @Unique
    private static final int LEADER_GROUP_SIZE = 3;

    @Inject( method = "finalizeSpawn", at = @At("RETURN"))
    public void checkForLeader(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (spawnReason == EntitySpawnReason.REINFORCEMENT || spawnReason == EntitySpawnReason.EVENT) return;

        Zombie entity = (Zombie) (Object) this;
        if (!entity.is(PazTags.EntityTypes.CAN_BE_LEADER)) return;

        boolean isBornLeader = PazZombie.Companion.isBornLeader(entity);
        boolean bonusPromotionChance = level.getRandom().nextFloat() < BONUS_LEADER_CHANCE;

        if (isBornLeader || bonusPromotionChance) promoteToLeader(entity, spawnReason);
    }

    @Unique
    private void promoteToLeader(Zombie entity, EntitySpawnReason spawnReason) {
        entity.setItemSlot(EquipmentSlot.OFFHAND, PazBlocks.BRAINZ_FLAG.asItem().getDefaultInstance());
        entity.setDropChance(EquipmentSlot.OFFHAND, 1.0F);

        if (spawnReason == EntitySpawnReason.NATURAL) PazZombie.Companion.spawnZombieGroup(entity, LEADER_GROUP_SIZE);
    }
}
