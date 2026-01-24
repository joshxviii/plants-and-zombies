package joshxviii.plantz.mixin;

import joshxviii.plantz.PazBlocks;
import joshxviii.plantz.PazItems;
import joshxviii.plantz.raid.ZombieRaid;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

/**
 * @author Josh
 */
@Mixin(Zombie.class)
public class ZombieMixin {

    @Unique
    static private final String LEADER_MODIFIER_ID = "leader_zombie_bonus";

    @Inject( method = "finalizeSpawn", at = @At("RETURN"))
    public void checkForLeader(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        Zombie entity = (Zombie) (Object) this;
        var isLeader = Objects.requireNonNull(entity.getAttribute(Attributes.MAX_HEALTH)).hasModifier(Identifier.withDefaultNamespace(LEADER_MODIFIER_ID));

        if(isLeader) {
            entity.setItemSlot(EquipmentSlot.OFFHAND, PazBlocks.BRAINZ_FLAG.asItem().getDefaultInstance());
            entity.setDropChance(EquipmentSlot.OFFHAND, 1.0F);
        }
    }
}
