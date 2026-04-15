package joshxviii.plantz.mixin;

import joshxviii.plantz.PazItems;
import joshxviii.plantz.ServerPlayerAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements ServerPlayerAccessor {

	@Unique
	private boolean plantz$hasPlantOnHead = false;

	@Override
	public boolean plantz$hasPlantOnHead() {
		return plantz$hasPlantOnHead;
	}

	@Override
	public boolean plantz$wearingPlantPotHelmet() {
		var value = ((ServerPlayer) (Object) this).getItemBySlot(EquipmentSlot.HEAD).is(PazItems.PLANT_POT_HELMET);
		if (!value) plantz$hasPlantOnHead = false;
		return value;
	}

	@Override
	public void plantz$setHasPlantOnHead(boolean value) {
		this.plantz$hasPlantOnHead = value;
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void plantz$writePlantData(ValueOutput output, CallbackInfo ci) {
		output.putBoolean("hasPlantOnHead", this.plantz$hasPlantOnHead);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void plantz$readPlantData(ValueInput input, CallbackInfo ci) {
		this.plantz$hasPlantOnHead = input.getBooleanOr("hasPlantOnHead", false);
	}
}