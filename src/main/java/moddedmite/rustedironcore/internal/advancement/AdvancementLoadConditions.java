package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.advancement.Advancements;
import net.minecraft.ResourceLocation;
import net.xiaoyu233.fml.FishModLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/** Forge conditional advancements and NeoForge resource loading conditions. */
public class AdvancementLoadConditions {

    private static final Map<String, Predicate<JsonObject>> ADAPTERS = new HashMap<>();

    public static void register(String id, Predicate<JsonObject> condition) {
        ADAPTERS.put(AdvancementJson.id(id).toString(), condition);
    }

    public static JsonObject select(JsonObject json) {
        if (!all(json.get("neoforge:conditions")) || !all(json.get("forge:conditions"))) return null;
        if (json.has("advancements")) {
            for (JsonElement entry : json.getAsJsonArray("advancements")) {
                JsonObject candidate = entry.getAsJsonObject();
                if (all(candidate.get("conditions"))) return select(candidate.getAsJsonObject("advancement"));
            }
            return null;
        }
        if (!all(json.get("conditions"))) return null;
        JsonObject selected = new JsonObject();
        json.entrySet().stream().filter(entry -> !entry.getKey().equals("conditions")
                        && !entry.getKey().equals("forge:conditions") && !entry.getKey().equals("neoforge:conditions"))
                .forEach(entry -> selected.add(entry.getKey(), entry.getValue()));
        return selected;
    }

    private static boolean all(JsonElement json) {
        if (json != null) for (JsonElement entry : json.getAsJsonArray()) if (!matches(entry.getAsJsonObject())) return false;
        return true;
    }

    private static boolean matches(JsonObject json) {
        ResourceLocation id = AdvancementJson.id(json.get("type").getAsString());
        Predicate<JsonObject> adapter = ADAPTERS.get(id.toString());
        if (adapter != null) return adapter.test(json);
        if (!id.getResourceDomain().equals("forge") && !id.getResourceDomain().equals("neoforge")) throw new IllegalArgumentException("Unknown advancement loading condition: " + id);
        return switch (id.getResourcePath()) {
            case "true" -> true;
            case "false" -> false;
            case "mod_loaded" -> FishModLoader.hasMod(json.get("modid").getAsString());
            case "item_exists" -> Advancements.resolveItem(json.get("item").getAsString()) != null;
            case "tag_empty" -> itemTagEmpty(AdvancementJson.id(json.get("tag").getAsString()).toString());
            case "not" -> !matches(json.getAsJsonObject("value"));
            case "and" -> all(json.get("values"));
            case "or" -> {
                boolean result = false;
                for (JsonElement child : json.getAsJsonArray("values")) if (matches(child.getAsJsonObject())) { result = true; break; }
                yield result;
            }
            default -> throw new IllegalArgumentException("Unknown advancement loading condition: " + id);
        };
    }

    private static boolean itemTagEmpty(String id) {
        return AdvancementTags.items(id).isEmpty();
    }
}
