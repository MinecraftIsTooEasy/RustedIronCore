package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.SpawnConditionRegisterEvent;
import net.minecraft.Entity;
import net.minecraft.World;

import java.util.HashMap;
import java.util.Map;

public class SpawnConditionHandler extends EventHandler<SpawnConditionRegisterEvent> {
    private final Map<Class<? extends Entity>, SpawnCondition> ConditionMap = new HashMap<>();

    public boolean has(Class<?> clazz) {
        return ConditionMap.containsKey(clazz);
    }

    public SpawnCondition get(Class<?> clazz) {
        return ConditionMap.get(clazz);
    }

    public void addCondition(Class<? extends Entity> clazz, SpawnCondition predicate) {
        if (ConditionMap.containsKey(clazz)) {
            SpawnCondition condition = ConditionMap.get(clazz);
            SpawnCondition wrapped = wrap(clazz, condition, predicate);
            ConditionMap.put(clazz, wrapped);
        } else {
            ConditionMap.put(clazz, predicate);
        }
    }

    private static SpawnCondition wrap(Class<? extends Entity> clazz, SpawnCondition first, SpawnCondition second) {
        return (world, x, y, z) -> {
            Class<? extends Entity> result = first.getResult(world, x, y, z);
            if (result == null) return null;// spawn fail
            if (result == clazz) return second.getResult(world, x, y, z);// spawn condition wrapped
            return result;// if result is a new class, force success
        };
    }

    /**
     * The spawn result can be different from your original class, for example creeper can become infernal.
     * <br>
     * Returning null means try next loop.
     */
    @FunctionalInterface
    public interface SpawnCondition {
        Class<? extends Entity> getResult(World world, int x, int y, int z);
    }
}
