package moddedmite.rustedironcore.internal.client.advancement;

import moddedmite.rustedironcore.api.advancement.Advancement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdvancementLayout {

    private final Map<String, List<Advancement>> children = new LinkedHashMap<>();
    private final Map<String, AdvancementNode> nodes = new LinkedHashMap<>();
    private float nextRow;

    public Map<String, AdvancementNode> build(Advancement root) {
        for (Advancement advancement : ClientAdvancements.DEFINITIONS.values()) {
            if (advancement.parent() != null) children.computeIfAbsent(advancement.parent().toString(), ignored -> new ArrayList<>()).add(advancement);
        }
        place(root, 0);
        return nodes;
    }

    private float place(Advancement advancement, int column) {
        boolean displayed = advancement.display() != null;
        List<Advancement> descendants = children.getOrDefault(advancement.id().toString(), List.of());
        float first = nextRow, last = first;
        for (int index = 0; index < descendants.size(); index++) {
            float row = place(descendants.get(index), column + (displayed ? 1 : 0));
            if (index == 0) first = row;
            last = row;
        }
        float row = descendants.isEmpty() ? nextRow++ : (first + last) / 2;
        if (displayed) nodes.put(advancement.id().toString(), new AdvancementNode(advancement, column, row));
        return row;
    }
}
