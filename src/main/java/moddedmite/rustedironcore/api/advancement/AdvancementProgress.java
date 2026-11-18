package moddedmite.rustedironcore.api.advancement;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdvancementProgress {

    private final Map<String, Long> criteria = new LinkedHashMap<>();

    public boolean award(String name) {
        return criteria.putIfAbsent(name, System.currentTimeMillis()) == null;
    }

    public boolean revoke(String name) {
        return criteria.remove(name) != null;
    }

    public boolean hasCriterion(String name) {
        return criteria.containsKey(name);
    }

    public boolean isDone(Advancement advancement) {
        return advancement.requirements().isDone(criteria.keySet());
    }

    public int completedGroups(Advancement advancement) {
        return advancement.requirements().completedGroups(criteria.keySet());
    }

    public Map<String, Long> criteria() {
        return Collections.unmodifiableMap(criteria);
    }

    public void restore(String name, long timestamp) {
        criteria.put(name, timestamp);
    }
}
