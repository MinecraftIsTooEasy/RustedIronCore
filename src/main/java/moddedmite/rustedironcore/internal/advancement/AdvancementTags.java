package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.util.LogUtil;
import moddedmite.rustedironcore.api.advancement.Advancements;
import net.minecraft.Item;
import net.minecraft.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/** Data-pack tag references are flattened at reload, so cycles never recurse during play. */
public class AdvancementTags {

    private static final Map<String, Map<String, List<String>>> TAGS = new HashMap<>();
    private static final Set<String> REPLACED = new HashSet<>();
    private static final Map<String, List<ItemStack>> ITEM_MEMBERS = new HashMap<>();

    public static void reload() {
        TAGS.clear();
        REPLACED.clear();
        ITEM_MEMBERS.clear();
        for (String type : List.of("item", "block", "fluid", "entity_type", "damage_type", "worldgen/biome", "worldgen/structure", "enchantment")) {
            String plural = switch (type) {
                case "item" -> "items";
                case "block" -> "blocks";
                case "entity_type" -> "entity_types";
                case "fluid" -> "fluids";
                default -> type;
            };
            Map<String, JsonObject> raw = new HashMap<>();
            AdvancementResources.loadResources(plural.equals(type) ? new String[]{"data/tags/" + type} : new String[]{"data/tags/" + plural, "data/tags/" + type})
                    .forEach((id, json) -> raw.put(id.toString(), json));
            raw.forEach((id, json) -> { if (AdvancementJson.flag(json, "replace", false)) REPLACED.add(type + ":" + id); });
            Map<String, List<String>> resolved = new HashMap<>();
            for (String id : raw.keySet()) try {
                flatten(type, id, raw, resolved, new HashSet<>());
            } catch (IllegalArgumentException exception) {
                LogUtil.getLogger().warn("Unable to load advancement tag {}: {}", id, exception.getMessage());
            }
            TAGS.put(type, resolved);
        }
    }

    private static List<String> flatten(String type, String id, Map<String, JsonObject> raw, Map<String, List<String>> resolved, Set<String> visiting) {
        if (resolved.containsKey(id)) return resolved.get(id);
        if (!raw.containsKey(id)) throw new IllegalArgumentException("Missing tag " + id);
        if (visiting.size() >= 256 || !visiting.add(id)) throw new IllegalArgumentException("Cyclic or excessively nested tag " + id);
        List<String> values = new ArrayList<>();
        for (JsonElement entry : raw.get(id).getAsJsonArray("values")) {
            String value = entry.isJsonObject() ? entry.getAsJsonObject().get("id").getAsString() : entry.getAsString();
            if (value.startsWith("#")) {
                String child = AdvancementJson.id(value.substring(1)).toString();
                if (!raw.containsKey(child) && Advancements.hasRegisteredTag(type, child)) values.add("#" + child);
                else if (raw.containsKey(child) || !entry.isJsonObject() || AdvancementJson.flag(entry.getAsJsonObject(), "required", true))
                    values.addAll(flatten(type, child, raw, resolved, visiting));
            } else values.add(AdvancementJson.id(value).toString());
        }
        visiting.remove(id);
        List<String> result = List.copyOf(values);
        resolved.put(id, result);
        return result;
    }

    public static boolean matches(String type, String id, Predicate<String> member) {
        return TAGS.getOrDefault(type, Map.of()).getOrDefault(id, List.of()).stream().anyMatch(member);
    }

    public static boolean contains(String type, String id) {
        return TAGS.getOrDefault(type, Map.of()).containsKey(id);
    }

    public static List<ItemStack> items(String id) {
        List<ItemStack> members = ITEM_MEMBERS.computeIfAbsent(id, tag -> {
            List<ItemStack> result = new ArrayList<>();
            Set<Long> seen = new HashSet<>();
            for (Item item : Item.itemsList) {
                if (item == null) continue;
                List<ItemStack> variants = new ArrayList<>();
                variants.add(new ItemStack(item));
                for (Object value : item.getSubItems()) variants.add((ItemStack) value);
                for (ItemStack stack : variants) {
                    long key = (long) stack.itemID << 32 | Integer.toUnsignedLong(stack.getItemSubtype());
                    if (seen.add(key) && Advancements.matchesItemTag(tag, stack)) result.add(stack.copy());
                }
            }
            return List.copyOf(result);
        });
        return members.stream().map(ItemStack::copy).toList();
    }

    public static boolean allowsRegisteredEntries(String type, String id) {
        return !REPLACED.contains(type + ":" + id);
    }
}
