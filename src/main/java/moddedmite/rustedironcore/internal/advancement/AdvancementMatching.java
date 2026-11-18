package moddedmite.rustedironcore.internal.advancement;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

/** Distinct matches with reassignment, so overlapping ingredient predicates are order independent. */
public class AdvancementMatching {

    public static <P, T> boolean distinct(List<P> predicates, List<T> values, BiPredicate<P, T> matches) {
        if (predicates.size() > values.size()) return false;
        int[] owners = new int[values.size()];
        Arrays.fill(owners, -1);
        for (int i = 0; i < predicates.size(); i++)
            if (!assign(i, predicates, values, matches, owners, new boolean[values.size()])) return false;
        return true;
    }

    private static <P, T> boolean assign(int index, List<P> predicates, List<T> values, BiPredicate<P, T> matches, int[] owners, boolean[] visited) {
        for (int i = 0; i < values.size(); i++) {
            if (visited[i] || !matches.test(predicates.get(index), values.get(i))) continue;
            visited[i] = true;
            if (owners[i] < 0 || assign(owners[i], predicates, values, matches, owners, visited)) {
                owners[i] = index;
                return true;
            }
        }
        return false;
    }
}
