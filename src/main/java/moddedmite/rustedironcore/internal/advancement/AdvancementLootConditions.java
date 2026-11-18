package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.advancement.AdvancementContext;
import moddedmite.rustedironcore.api.advancement.Advancements;
import moddedmite.rustedironcore.api.advancement.JsonPredicate;
import moddedmite.rustedironcore.api.util.LogUtil;
import net.minecraft.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdvancementLootConditions {

    private static final Map<String, JsonElement> REFERENCES = new HashMap<>();
    private static final Map<String, JsonPredicate<AdvancementContext>> ADAPTERS = new HashMap<>();
    private static final Set<String> VALIDATING = new HashSet<>();

    public static void register(String id, JsonPredicate<AdvancementContext> predicate) {
        ADAPTERS.put(AdvancementJson.id(id).toString(), predicate);
    }

    public static void reload() {
        REFERENCES.clear();
        VALIDATING.clear();
        AdvancementResources.loadPredicates().forEach((id, json) -> {
            try {
                JsonElement selected = json.isJsonObject() ? AdvancementLoadConditions.select(json.getAsJsonObject()) : json;
                if (selected != null) REFERENCES.put(id.toString(), selected);
            } catch (RuntimeException exception) {
                LogUtil.getLogger().warn("Unable to load advancement predicate {}: {}", id, exception.getMessage());
            }
        });
    }

    public static void validateAll(JsonElement conditions) {
        if (conditions != null) for (JsonElement entry : conditions.getAsJsonArray()) validate(entry.getAsJsonObject());
    }

    public static void validate(JsonObject json) {
        String type = AdvancementJson.id(json.get("condition").getAsString()).toString();
        if (ADAPTERS.containsKey(type)) {
            ADAPTERS.get(type).validate(json);
            return;
        }
        switch (type) {
            case "minecraft:entity_properties" -> {
                AdvancementPredicates.keys(json, "condition", "entity", "predicate");
                String entity = AdvancementJson.string(json, "entity", "this");
                if (!entity.equals("this")) throw new IllegalArgumentException("Advancement context has no " + entity + " entity; use this or a registered condition");
                AdvancementEntityPredicates.validate(json.get("predicate"));
            }
            case "minecraft:match_tool" -> {
                AdvancementPredicates.keys(json, "condition", "predicate");
                if (json.has("predicate")) AdvancementItemPredicates.validate(json.getAsJsonObject("predicate"));
            }
            case "minecraft:location_check" -> {
                AdvancementPredicates.keys(json, "condition", "predicate", "offsetX", "offsetY", "offsetZ");
                if (json.has("predicate")) AdvancementLocationPredicates.validate(json.getAsJsonObject("predicate"));
                for (String field : Set.of("offsetX", "offsetY", "offsetZ")) if (json.has(field)) AdvancementJson.integer(json.get(field), Integer.MIN_VALUE, Integer.MAX_VALUE);
            }
            case "minecraft:block_state_property" -> {
                AdvancementPredicates.keys(json, "condition", "block", "properties");
                AdvancementJson.id(json.get("block").getAsString());
                if (json.has("properties")) AdvancementPredicates.validateState(json.getAsJsonObject("properties"));
            }
            case "minecraft:random_chance" -> {
                AdvancementPredicates.keys(json, "condition", "chance");
                double chance = json.get("chance").getAsDouble();
                if (!Double.isFinite(chance) || chance < 0 || chance > 1) throw new IllegalArgumentException("Invalid random chance: " + chance);
            }
            case "minecraft:inverted" -> {
                AdvancementPredicates.keys(json, "condition", "term");
                validate(json.getAsJsonObject("term"));
            }
            case "minecraft:all_of", "minecraft:any_of", "minecraft:alternative" -> {
                AdvancementPredicates.keys(json, "condition", "terms");
                validateAll(json.get("terms"));
            }
            case "minecraft:reference" -> {
                AdvancementPredicates.keys(json, "condition", "name");
                String id = AdvancementJson.id(json.get("name").getAsString()).toString();
                if (!REFERENCES.containsKey(id)) throw new IllegalArgumentException("Missing predicate: " + id);
                if (VALIDATING.size() >= 256 || !VALIDATING.add(id)) throw new IllegalArgumentException("Cyclic predicate: " + id);
                try {
                    JsonElement reference = REFERENCES.get(id);
                    if (reference.isJsonArray()) validateAll(reference); else validate(reference.getAsJsonObject());
                } finally { VALIDATING.remove(id); }
            }
            case "minecraft:time_check" -> {
                AdvancementPredicates.keys(json, "condition", "value", "period");
                AdvancementPredicates.validateRange(json.get("value"));
                if (json.has("period")) AdvancementJson.integer(json.get("period"), 1, Integer.MAX_VALUE);
            }
            case "minecraft:weather_check" -> {
                AdvancementPredicates.keys(json, "condition", "raining", "thundering");
                for (String key : Set.of("raining", "thundering")) if (json.has(key) && !json.get(key).getAsJsonPrimitive().isBoolean()) throw new IllegalArgumentException("Weather flag must be boolean");
            }
            case "minecraft:value_check" -> {
                AdvancementPredicates.keys(json, "condition", "value", "range");
                AdvancementNumberProviders.validate(json.get("value"));
                AdvancementNumberProviders.validateIntRange(json.get("range"));
            }
            default -> throw new IllegalArgumentException("Unsupported advancement loot condition: " + type);
        }
    }

    public static boolean all(JsonElement conditions, AdvancementContext context) {
        if (conditions != null) for (JsonElement entry : conditions.getAsJsonArray()) if (!matches(entry.getAsJsonObject(), context)) return false;
        return true;
    }

    public static boolean matches(JsonObject json, AdvancementContext context) {
        String type = AdvancementJson.id(json.get("condition").getAsString()).toString();
        JsonPredicate<AdvancementContext> adapter = ADAPTERS.get(type);
        if (adapter != null) return adapter.matches(json, context);
        return switch (type) {
            case "minecraft:entity_properties" -> {
                Entity entity = context.entity();
                yield context.player() != null && AdvancementEntityPredicates.matches(json.get("predicate"), context.player(), entity, context.random());
            }
            case "minecraft:match_tool" -> !json.has("predicate") || AdvancementItemPredicates.matches(json.getAsJsonObject("predicate"), context.tool());
            case "minecraft:location_check" -> !json.has("predicate") || AdvancementLocationPredicates.matches(json.getAsJsonObject("predicate"), context.world(),
                    context.x() + offset(json, "offsetX"), context.y() + offset(json, "offsetY"), context.z() + offset(json, "offsetZ"));
            case "minecraft:block_state_property" -> {
                int x = (int) Math.floor(context.x()), y = (int) Math.floor(context.y()), z = (int) Math.floor(context.z());
                yield AdvancementLocationPredicates.blockId(json.get("block").getAsString(), context.world(), x, y, z)
                        && (!json.has("properties") || AdvancementLocationPredicates.state(json.getAsJsonObject("properties"),
                        Advancements.blockState(context.world(), x, y, z)));
            }
            case "minecraft:random_chance" -> context.random().nextFloat() < json.get("chance").getAsDouble();
            case "minecraft:inverted" -> !matches(json.getAsJsonObject("term"), context);
            case "minecraft:all_of" -> all(json.get("terms"), context);
            case "minecraft:any_of", "minecraft:alternative" -> {
                boolean result = false;
                for (JsonElement term : json.getAsJsonArray("terms")) if (matches(term.getAsJsonObject(), context)) { result = true; break; }
                yield result;
            }
            case "minecraft:reference" -> {
                JsonElement reference = REFERENCES.get(AdvancementJson.id(json.get("name").getAsString()).toString());
                yield reference.isJsonArray() ? all(reference, context) : matches(reference.getAsJsonObject(), context);
            }
            case "minecraft:time_check" -> {
                long time = context.world().getTotalWorldTime();
                if (json.has("period")) time %= json.get("period").getAsLong();
                yield AdvancementPredicates.range(json.get("value"), time);
            }
            case "minecraft:weather_check" -> (!json.has("raining") || json.get("raining").getAsBoolean() == context.world().isPrecipitating(true))
                    && (!json.has("thundering") || json.get("thundering").getAsBoolean() == context.world().isThundering(true));
            case "minecraft:value_check" -> {
                int value = AdvancementNumberProviders.sampleInt(json.get("value"), context.random());
                yield AdvancementNumberProviders.clamp(json.get("range"), value, context.random()) == value;
            }
            default -> false;
        };
    }

    private static int offset(JsonObject json, String key) {
        return json.has(key) ? json.get(key).getAsInt() : 0;
    }
}
