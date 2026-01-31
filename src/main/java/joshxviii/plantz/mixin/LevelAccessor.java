package joshxviii.plantz.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;


@Mixin(Level.class)
public interface LevelAccessor {

    @Accessor("blockEntityTickers")
    List<TickingBlockEntity> blockEntityTickers();
}

