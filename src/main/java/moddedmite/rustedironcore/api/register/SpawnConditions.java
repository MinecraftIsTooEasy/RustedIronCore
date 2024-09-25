package moddedmite.rustedironcore.api.register;

import net.minecraft.*;

import java.util.HashMap;
import java.util.Map;

public class SpawnConditions {

    private static final Map<Class<? extends Entity>, SpawningCondition> conditionMap = new HashMap<>();

//    static {
//        conditionMap.put(EntityCreeper.class, (world, x, y, z) -> {
//            if (world.hasSkylight() && !world.isDaytime() && world.rand.nextInt(4) != 0 && world.isOutdoors(x, y, z)) {
//                return null;
//            }
//            if (world.rand.nextInt(40) >= y && world.rand.nextFloat() < 0.5f) {
//                return EntityInfernalCreeper.class;
//            }
//            return EntityCreeper.class;
//        });
//        conditionMap.put(EntitySlime.class, (world, x, y, z) -> {
//            if (world.blockTypeIsAbove(Block.stone, x, y, z)) {
//                return null;
//            }
//            return EntitySlime.class;
//        });
//    }

    public static boolean has(Class<?> clazz) {
        return conditionMap.containsKey(clazz);
    }

    public static SpawningCondition get(Class<?> clazz) {
        return conditionMap.get(clazz);
    }

    public static void registerSpawningCondition(Class<? extends Entity> clazz, SpawningCondition predicate) {
        conditionMap.put(clazz, predicate);
    }

    // if you want your monster to spawn without checking, just do not register
    // the result can be different from your original class, for example creeper can become infernal
    // returning null means try next loop
    @FunctionalInterface
    public interface SpawningCondition {
        Class<? extends Entity> getResult(World world, int x, int y, int z);
    }
}
