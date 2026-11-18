package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.advancement.AdvancementContext;
import moddedmite.rustedironcore.api.advancement.Advancements;
import moddedmite.rustedironcore.api.util.LogUtil;
import net.minecraft.ItemStack;
import net.minecraft.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Server-side loot pools for advancement rewards; unsupported functions require explicit adapters. */
public class AdvancementLootTables {

    private static final Map<String, JsonObject> TABLES = new HashMap<>();
    private static final Set<String> VALIDATING = new HashSet<>();

    public static void reload() {
        TABLES.clear();
        VALIDATING.clear();
        AdvancementResources.loadResources("data/loot_tables", "data/loot_table").forEach((id, json) -> {
            try {
                JsonObject selected = AdvancementLoadConditions.select(json);
                if (selected != null) TABLES.put(id.toString(), selected);
            } catch (RuntimeException exception) {
                LogUtil.getLogger().warn("Unable to load advancement loot table {}: {}", id, exception.getMessage());
            }
        });
    }

    public static void validate(String id) {
        id = AdvancementJson.id(id).toString();
        JsonObject table = TABLES.get(id);
        if (table == null) throw new IllegalArgumentException("Missing advancement loot table: " + id);
        if (VALIDATING.size() >= 256 || !VALIDATING.add(id)) throw new IllegalArgumentException("Cyclic loot table: " + id);
        try {
            validateTable(table, 0);
        } finally { VALIDATING.remove(id); }
    }

    private static void validateTable(JsonObject table, int depth) {
        if (depth >= 256) throw new IllegalArgumentException("Loot entries nested deeper than 256");
        AdvancementPredicates.keys(table, "type", "pools", "functions", "random_sequence");
        if (table.has("random_sequence")) AdvancementJson.id(table.get("random_sequence").getAsString());
        if (table.has("type") && !Set.of("minecraft:advancement_reward", "minecraft:generic", "minecraft:empty").contains(AdvancementJson.id(table.get("type").getAsString()).toString()))
            throw new IllegalArgumentException("Loot table needs advancement_reward or generic context");
        AdvancementLootFunctions.validate(table.get("functions"));
        if (table.has("pools")) for (JsonElement element : table.getAsJsonArray("pools")) {
            JsonObject pool = element.getAsJsonObject();
            AdvancementPredicates.keys(pool, "rolls", "bonus_rolls", "entries", "conditions", "functions");
            AdvancementNumberProviders.validate(pool.get("rolls"));
            if (pool.has("bonus_rolls")) AdvancementNumberProviders.validate(pool.get("bonus_rolls"));
            AdvancementLootConditions.validateAll(pool.get("conditions"));
            AdvancementLootFunctions.validate(pool.get("functions"));
            for (JsonElement entry : pool.getAsJsonArray("entries")) validateEntry(entry.getAsJsonObject(), depth + 1);
        }
    }

    private static void validateEntry(JsonObject entry, int depth) {
        if (depth >= 256) throw new IllegalArgumentException("Loot entries nested deeper than 256");
        AdvancementPredicates.keys(entry, "type", "name", "value", "weight", "quality", "conditions", "functions", "children", "expand");
        AdvancementLootConditions.validateAll(entry.get("conditions"));
        AdvancementLootFunctions.validate(entry.get("functions"));
        if (entry.has("weight")) AdvancementJson.integer(entry.get("weight"), 0, 1000000);
        if (entry.has("quality")) AdvancementJson.integer(entry.get("quality"), -1000000, 1000000);
        String type = AdvancementJson.id(entry.get("type").getAsString()).toString();
        if (Set.of("minecraft:alternatives", "minecraft:sequence", "minecraft:group").contains(type))
            AdvancementPredicates.keys(entry, "type", "conditions", "children");
        else if (entry.has("children") || entry.has("expand") && !type.equals("minecraft:tag")) throw new IllegalArgumentException("Unexpected loot entry field: " + entry);
        switch (AdvancementJson.id(entry.get("type").getAsString()).toString()) {
            case "minecraft:item" -> {
                if (Advancements.resolveItem(entry.get("name").getAsString()) == null) throw new IllegalArgumentException("Unknown loot item: " + entry.get("name"));
            }
            case "minecraft:empty" -> { }
            case "minecraft:tag" -> {
                String tag = AdvancementJson.id(entry.get("name").getAsString()).toString();
                if (!AdvancementTags.contains("item", tag) && !Advancements.hasRegisteredTag("item", tag)) throw new IllegalArgumentException("Unknown loot item tag: " + tag);
                if (!entry.has("expand") || !entry.get("expand").isJsonPrimitive() || !entry.get("expand").getAsJsonPrimitive().isBoolean()) throw new IllegalArgumentException("Loot tag needs an expand flag");
            }
            case "minecraft:loot_table" -> {
                JsonElement value = entry.has("value") ? entry.get("value") : entry.get("name");
                if (value.isJsonObject()) validateTable(value.getAsJsonObject(), depth + 1);
                else validate(value.getAsString());
            }
            case "minecraft:alternatives", "minecraft:sequence", "minecraft:group" -> {
                for (JsonElement child : entry.getAsJsonArray("children")) validateEntry(child.getAsJsonObject(), depth + 1);
            }
            default -> throw new IllegalArgumentException("Unsupported loot entry: " + entry.get("type"));
        }
    }

    public static void give(String id, ServerPlayer player) {
        int[] budget = {65536};
        for (ItemStack stack : generate(id, AdvancementContext.of(player, player), budget)) {
            while (stack.stackSize > 0) {
                if (--budget[0] < 0) throw new IllegalStateException("Loot table exceeded 65536 operations");
                int amount = Math.min(stack.stackSize, stack.getMaxStackSize());
                ItemStack portion = stack.copy();
                portion.stackSize = amount;
                player.inventory.addItemStackToInventoryOrDropIt(portion);
                stack.stackSize -= amount;
            }
        }
    }

    private static List<ItemStack> generate(String id, AdvancementContext context, int[] budget) {
        return generate(TABLES.get(AdvancementJson.id(id).toString()), context, budget);
    }

    private static List<ItemStack> generate(JsonObject table, AdvancementContext context, int[] budget) {
        if (--budget[0] < 0) throw new IllegalStateException("Loot table exceeded 65536 operations");
        if (table.has("random_sequence")) context = context.withRandom(AdvancementRandomSequences.get(context.world(), AdvancementJson.id(table.get("random_sequence").getAsString()).toString()));
        List<ItemStack> result = new ArrayList<>();
        if (table.has("pools")) for (JsonElement element : table.getAsJsonArray("pools")) {
            JsonObject pool = element.getAsJsonObject();
            if (!AdvancementLootConditions.all(pool.get("conditions"), context)) continue;
            int rolls = Math.max(0, Math.min(65536, AdvancementNumberProviders.sampleInt(pool.get("rolls"), context.random())));
            for (int i = 0; i < rolls; i++) {
                if (--budget[0] < 0) throw new IllegalStateException("Loot table exceeded 65536 operations");
                List<AdvancementLootEntry> candidates = new ArrayList<>();
                for (JsonElement entry : pool.getAsJsonArray("entries")) expand(entry.getAsJsonObject(), context, candidates, budget);
                long total = 0;
                for (AdvancementLootEntry entry : candidates) total += weight(entry.definition());
                if (total == 0) continue;
                long selected = context.random().nextLong(total);
                for (AdvancementLootEntry entry : candidates) {
                    selected -= weight(entry.definition());
                    if (selected >= 0) continue;
                    List<ItemStack> rolled = create(entry, context, budget);
                    AdvancementLootFunctions.apply(pool.get("functions"), rolled, context);
                    result.addAll(rolled);
                    break;
                }
            }
        }
        AdvancementLootFunctions.apply(table.get("functions"), result, context);
        result.removeIf(stack -> stack.stackSize <= 0);
        return result;
    }

    private static int weight(JsonObject entry) {
        return entry.has("weight") ? entry.get("weight").getAsInt() : 1;
    }

    private static boolean expand(JsonObject entry, AdvancementContext context, List<AdvancementLootEntry> result, int[] budget) {
        if (--budget[0] < 0) throw new IllegalStateException("Loot table exceeded 65536 operations");
        if (!AdvancementLootConditions.all(entry.get("conditions"), context)) return false;
        String type = AdvancementJson.id(entry.get("type").getAsString()).toString();
        if (type.equals("minecraft:tag") && entry.get("expand").getAsBoolean()) {
            for (ItemStack stack : AdvancementTags.items(AdvancementJson.id(entry.get("name").getAsString()).toString())) {
                if (--budget[0] < 0) throw new IllegalStateException("Loot table exceeded 65536 operations");
                result.add(new AdvancementLootEntry(entry, stack));
            }
            return true;
        }
        if (!Set.of("minecraft:alternatives", "minecraft:sequence", "minecraft:group").contains(type)) {
            result.add(new AdvancementLootEntry(entry, null));
            return true;
        }
        boolean matched = false;
        for (JsonElement child : entry.getAsJsonArray("children")) {
            boolean success = expand(child.getAsJsonObject(), context, result, budget);
            matched |= success;
            if (type.equals("minecraft:alternatives") && success) return true;
            if (type.equals("minecraft:sequence") && !success) return false;
        }
        return !type.equals("minecraft:alternatives") || matched;
    }

    private static List<ItemStack> create(AdvancementLootEntry choice, AdvancementContext context, int[] budget) {
        JsonObject entry = choice.definition();
        List<ItemStack> result = new ArrayList<>();
        if (choice.item() != null) result.add(choice.item().copy());
        else switch (AdvancementJson.id(entry.get("type").getAsString()).toString()) {
            case "minecraft:item" -> result.add(Advancements.resolveItem(entry.get("name").getAsString()));
            case "minecraft:tag" -> {
                for (ItemStack stack : AdvancementTags.items(AdvancementJson.id(entry.get("name").getAsString()).toString())) {
                    if (--budget[0] < 0) throw new IllegalStateException("Loot table exceeded 65536 operations");
                    result.add(stack);
                }
            }
            case "minecraft:loot_table" -> {
                JsonElement value = entry.has("value") ? entry.get("value") : entry.get("name");
                result.addAll(value.isJsonObject() ? generate(value.getAsJsonObject(), context, budget) : generate(value.getAsString(), context, budget));
            }
            case "minecraft:empty" -> { }
            default -> throw new IllegalArgumentException("Unsupported loot entry");
        }
        AdvancementLootFunctions.apply(entry.get("functions"), result, context);
        return result;
    }

}
