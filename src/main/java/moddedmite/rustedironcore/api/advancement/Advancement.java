package moddedmite.rustedironcore.api.advancement;

import com.google.gson.JsonObject;
import moddedmite.rustedironcore.internal.advancement.AdvancementJson;
import net.minecraft.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record Advancement(ResourceLocation id, ResourceLocation parent, JsonObject display,
                          Map<String, Criterion<?>> criteria, AdvancementRequirements requirements,
                          JsonObject rewards, JsonObject json) {

    public Advancement {
        criteria = Collections.unmodifiableMap(new LinkedHashMap<>(criteria));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final JsonObject json = new JsonObject();
        private final JsonObject criteria = new JsonObject();

        public Builder parent(ResourceLocation parent) {
            json.addProperty("parent", parent.toString());
            return this;
        }

        public Builder display(JsonObject display) {
            json.add("display", display);
            return this;
        }

        public Builder addCriterion(String name, Criterion<?> criterion) {
            JsonObject value = new JsonObject();
            value.addProperty("trigger", Advancements.getTriggerId(criterion.trigger()).toString());
            value.add("conditions", criterion.conditions());
            if (criteria.has(name)) throw new IllegalArgumentException("Duplicate criterion: " + name);
            criteria.add(name, value);
            return this;
        }

        public Builder requirements(AdvancementRequirements requirements) {
            json.add("requirements", requirements.toJson());
            return this;
        }

        public Builder rewards(JsonObject rewards) {
            json.add("rewards", rewards);
            return this;
        }

        public Advancement build(ResourceLocation id) {
            json.add("criteria", criteria);
            return AdvancementJson.parse(id, json, true);
        }

        public Advancement register(ResourceLocation id) {
            Advancement advancement = build(id);
            Advancements.register(advancement);
            return advancement;
        }
    }
}
