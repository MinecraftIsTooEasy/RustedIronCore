package moddedmite.rustedironcore.random;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RandomUtil {
    public static <T extends IntegerWeightedEntry> T getRandomEntry(List<T> list, Random random) {
        int total = getTotalWeight(list);
        float randomInt = random.nextInt(total);
        Iterator<T> iterator = list.iterator();
        T chosen;
        do {
            if (!iterator.hasNext()) {
                return null;
            }
            chosen = iterator.next();
            randomInt -= chosen.getWeight();
        } while (randomInt >= 0);
        return chosen;
    }

    public static <T extends IntegerWeightedEntry> int getTotalWeight(List<T> list) {
        int result = 0;
        for (T t : list) {
            result += t.getWeight();
        }
        return result;
    }

    public static <T extends FloatWeightedEntry> T getRandomEntryFloat(List<T> list, Random random) {
        float total = getTotalWeightFloat(list);
        float randomFloat = random.nextFloat(total);
        Iterator<T> iterator = list.iterator();
        T chosen;
        do {
            if (!iterator.hasNext()) {
                return null;
            }
            chosen = iterator.next();
            randomFloat -= chosen.getWeight();
        } while (randomFloat >= 0.0F);
        return chosen;
    }

    public static <T extends FloatWeightedEntry> float getTotalWeightFloat(List<T> list) {
        float result = 0.0F;
        for (T t : list) {
            result += t.getWeight();
        }
        return result;
    }

}
