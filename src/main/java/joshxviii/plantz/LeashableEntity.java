package joshxviii.plantz;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.level.Level;

public abstract class LeashableEntity extends Entity implements Leashable {
    public LeashableEntity(EntityType<?> type, Level level) {
        super(type, level);
    }
}
