package moddedmite.rustedironcore.api.advancement;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Each inner group is OR; all groups must be satisfied. Parent nodes do not gate progress. */
public record AdvancementRequirements(List<List<String>> groups) {

    public AdvancementRequirements {
        groups = groups.stream().map(List::copyOf).toList();
    }

    public static AdvancementRequirements allOf(Collection<String> criteria) {
        return new AdvancementRequirements(criteria.stream().map(List::of).toList());
    }

    public static AdvancementRequirements anyOf(Collection<String> criteria) {
        return new AdvancementRequirements(List.of(List.copyOf(criteria)));
    }

    public static AdvancementRequirements parse(JsonArray json, Set<String> criteria) {
        if (criteria.isEmpty()) throw new IllegalArgumentException("An advancement needs at least one criterion");
        if (json == null) return allOf(criteria);
        List<List<String>> groups = new ArrayList<>();
        Set<String> used = new HashSet<>();
        for (JsonElement element : json) {
            List<String> group = new ArrayList<>();
            for (JsonElement name : element.getAsJsonArray()) {
                String key = name.getAsString();
                if (!criteria.contains(key)) throw new IllegalArgumentException("Unknown criterion in requirements: " + key);
                group.add(key);
                used.add(key);
            }
            if (group.isEmpty()) throw new IllegalArgumentException("Empty requirements group");
            groups.add(group);
        }
        if (!used.equals(criteria)) throw new IllegalArgumentException("Requirements must reference every criterion");
        return new AdvancementRequirements(groups);
    }

    public boolean isDone(Collection<String> awarded) {
        return !groups.isEmpty() && groups.stream().allMatch(group -> group.stream().anyMatch(awarded::contains));
    }

    public int completedGroups(Collection<String> awarded) {
        return (int) groups.stream().filter(group -> group.stream().anyMatch(awarded::contains)).count();
    }

    public JsonArray toJson() {
        JsonArray result = new JsonArray();
        for (List<String> group : groups) {
            JsonArray names = new JsonArray();
            group.forEach(name -> names.add(new JsonPrimitive(name)));
            result.add(names);
        }
        return result;
    }
}
