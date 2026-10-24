package joshxviii.plantz.mixin.client;


import joshxviii.plantz.PazItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @author Josh
 */
@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {
    @Inject(at = @At("HEAD"), method = "getArmPose(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;", cancellable = true)
    private static void p(Avatar avatar, ItemStack itemInHand, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (!avatar.isSwinging() && itemInHand.is(PazItems.DYE_BLASTER) && avatar.isUsingItem()) cir.setReturnValue(HumanoidModel.ArmPose.CROSSBOW_HOLD);
    }
}
