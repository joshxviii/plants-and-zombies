import joshxviii.plantz.entity.zombie.PazZombie;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.Level;

public abstract class PazOwnableZombie extends PazZombie implements OwnableEntity {
    public PazOwnableZombie(EntityType<? extends PazZombie> type, Level level) {
        super(type, level);
    }
}
