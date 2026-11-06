package joshxviii.plantz.mixin.client;

import joshxviii.plantz.gui.ZombieRaidOverlay;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static joshxviii.plantz.gui.ZombieRaidOverlay.isZombieRaid;

/**
 * @author Josh
 */
@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {

    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    private Collection<LerpingBossEvent> plantz$filterEvents(Map<UUID, LerpingBossEvent> map) {
        return map.values().stream().filter(it -> !isZombieRaid(it)).collect(Collectors.toSet());
    }

}
