package joshxviii.plantz.mixin;

import joshxviii.plantz.PazBlocks;
import joshxviii.plantz.PazDamageTypes;
import joshxviii.plantz.entity.zombie.Gargantuar;
import joshxviii.plantz.entity.zombie.ZombieYeti;
import net.minecraft.resources.Identifier;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

/**
 * @author Josh
 */
@Mixin(Zombie.class)
public class ZombieMixin {

    @Unique
    static private final String LEADER_MODIFIER_ID = "leader_zombie_bonus";

    @ModifyArgs(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Monster;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void updateDamage(Args args) {// exploding plants deal 999 dmg
        DamageSource source = args.get(1);
        if (source.is(PazDamageTypes.EXPLODE)) args.set(2, 999F);
    }

    @Inject( method = "finalizeSpawn", at = @At("RETURN"))
    public void checkForLeader(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Zombie entity = (Zombie) (Object) this;
        var isLeader = Objects.requireNonNull(entity.getAttribute(Attributes.MAX_HEALTH)).hasModifier(Identifier.withDefaultNamespace(LEADER_MODIFIER_ID));

        if(isLeader) {
            var dropChance = spawnReason.equals(EntitySpawnReason.EVENT) ? 0.0F : 1.0F;
            if (entity instanceof Gargantuar || entity instanceof ZombieYeti) {
                entity.setItemSlot(EquipmentSlot.HEAD, PazBlocks.BRAINZ_FLAG.asItem().getDefaultInstance());
                entity.setDropChance(EquipmentSlot.HEAD, dropChance);
            }
            else {
                entity.setItemSlot(EquipmentSlot.OFFHAND, PazBlocks.BRAINZ_FLAG.asItem().getDefaultInstance());
                entity.setDropChance(EquipmentSlot.OFFHAND, dropChance);
            }
        }
    }
}
