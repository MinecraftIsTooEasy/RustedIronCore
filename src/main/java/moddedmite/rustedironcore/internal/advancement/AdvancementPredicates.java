package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.advancement.AdvancementContext;
import net.minecraft.Entity;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
import net.minecraft.World;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/** Shared JSON primitives and context-aware condition dispatch. */
public class AdvancementPredicates {

    public static void keys(JsonObject json, String... supported) {
        Set<String> allowed = Set.of(supported);
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (!allowed.contains(entry.getKey())) throw new IllegalArgumentException("Unsupported advancement condition: " + entry.getKey());
        }
    }

    public static boolean range(JsonElement json, double value) {
        if (json == null || json.isJsonNull()) return true;
        if (json.isJsonPrimitive()) return value == json.getAsDouble();
        JsonObject range = json.getAsJsonObject();
        return (!range.has("min") || value >= range.get("min").getAsDouble())
                && (!range.has("max") || value <= range.get("max").getAsDouble());
    }

    public static void validateItem(JsonObject json) {
        AdvancementItemPredicates.validate(json);
    }

    public static void validateRange(JsonElement json) {
        if (json == null || json.isJsonNull()) return;
        if (json.isJsonPrimitive()) {
            if (!json.getAsJsonPrimitive().isNumber() || !Double.isFinite(json.getAsDouble())) throw new IllegalArgumentException("Expected a finite number: " + json);
            return;
        }
        JsonObject range = json.getAsJsonObject();
        keys(range, "min", "max");
        for (String bound : Set.of("min", "max")) {
            if (!range.has(bound)) continue;
            JsonElement value = range.get(bound);
            if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber() || !Double.isFinite(value.getAsDouble())) throw new IllegalArgumentException("Expected a finite range bound: " + value);
        }
        if (range.has("min") && range.has("max") && range.get("min").getAsDouble() > range.get("max").getAsDouble()) throw new IllegalArgumentException("Inverted range: " + json);
    }

    public static void validateIds(JsonElement json) {
        if (json == null) return;
        if (json.isJsonArray()) {
            for (JsonElement id : json.getAsJsonArray()) validateIds(id);
        } else {
            String id = json.getAsString();
            AdvancementJson.id(id.startsWith("#") ? id.substring(1) : id);
        }
    }

    public static void validateState(JsonObject json) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getValue().isJsonObject()) validateRange(entry.getValue());
            else entry.getValue().getAsString();
        }
    }

    public static boolean anyId(JsonElement json, Predicate<String> predicate) {
        if (!json.isJsonArray()) return predicate.test(json.getAsString());
        for (JsonElement id : json.getAsJsonArray()) if (predicate.test(id.getAsString())) return true;
        return false;
    }

    public static boolean item(JsonObject json, ItemStack stack) {
        return AdvancementItemPredicates.matches(json, stack);
    }

    public static void validatePlayer(JsonElement json) {
        AdvancementEntityPredicates.validate(json);
    }

    public static boolean player(JsonElement json, EntityPlayer player) {
        return AdvancementEntityPredicates.matches(json, player, player);
    }

    public static void validateEntity(JsonElement json) {
        AdvancementEntityPredicates.validate(json);
    }

    public static boolean entity(JsonElement json, EntityPlayer player, Entity entity) {
        return AdvancementEntityPredicates.matches(json, player, entity);
    }

    public static void validateLocation(JsonObject json) {
        AdvancementLocationPredicates.validate(json);
    }

    public static boolean location(JsonObject json, World world, int x, int y, int z) {
        return AdvancementLocationPredicates.matches(json, world, x, y, z);
    }

    public static void validateBlockConditions(JsonElement json) {
        if (json == null || json.isJsonNull()) return;
        if (json.isJsonObject()) { validateLocation(json.getAsJsonObject()); return; }
        AdvancementLootConditions.validateAll(json);
    }

    public static boolean blockConditions(JsonElement json, EntityPlayer player, World world, int x, int y, int z, ItemStack stack) {
        if (json == null || json.isJsonNull()) return true;
        if (json.isJsonObject()) return location(json.getAsJsonObject(), world, x, y, z);
        return AdvancementLootConditions.all(json, new AdvancementContext(world, player, player, stack, x + 0.5, y + 0.5, z + 0.5));
    }

    public static boolean dimension(String id, int dimension) {
        return switch (id) {
            case "minecraft:overworld" -> dimension == 0;
            case "minecraft:the_nether" -> dimension == -1;
            case "minecraft:the_end" -> dimension == 1;
            case "minecraft:underworld", "ric:underworld" -> dimension == -2;
            default -> id.equals(Integer.toString(dimension));
        };
    }

    public static boolean block(JsonObject json, World world, int x, int y, int z) {
        return AdvancementLocationPredicates.block(json, world, x, y, z);
    }

}
