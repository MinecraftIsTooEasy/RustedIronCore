package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import moddedmite.rustedironcore.api.advancement.Advancement;
import moddedmite.rustedironcore.api.advancement.AdvancementRequirements;
import moddedmite.rustedironcore.api.advancement.Advancements;
import moddedmite.rustedironcore.api.advancement.Criterion;
import moddedmite.rustedironcore.api.advancement.CriterionCodec;
import moddedmite.rustedironcore.api.advancement.SimpleCriterionTrigger;
import moddedmite.rustedironcore.api.chat.TextComponentSerializer;
import net.minecraft.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public class AdvancementJson {

    private static final SimpleCriterionTrigger<JsonObject> CLIENT_TRIGGER = new SimpleCriterionTrigger<>(CriterionCodec.JSON);

    public static ResourceLocation id(String id) {
        String qualified = id.contains(":") ? id : "minecraft:" + id;
        if (!qualified.matches("[a-z0-9_.-]+:[a-z0-9_./-]+")) throw new IllegalArgumentException("Invalid advancement identifier: " + id);
        for (String segment : qualified.substring(qualified.indexOf(':') + 1).split("/", -1))
            if (segment.isEmpty() || segment.equals(".") || segment.equals("..")) throw new IllegalArgumentException("Invalid advancement identifier: " + id);
        return new ResourceLocation(qualified);
    }

    public static Advancement parse(ResourceLocation id, JsonObject json, boolean server) {
        id(id.toString());
        json = new JsonParser().parse(json.toString()).getAsJsonObject();
        ResourceLocation parent = json.has("parent") ? id(json.get("parent").getAsString()) : null;
        JsonObject display = json.has("display") ? json.getAsJsonObject("display") : null;
        if (display != null) {
            TextComponentSerializer.fromJson(display.get("title"));
            TextComponentSerializer.fromJson(display.get("description"));
            JsonObject icon = display.getAsJsonObject("icon");
            if (icon == null || (!icon.has("item") && !icon.has("id"))) throw new IllegalArgumentException("Display icon needs item or id");
            if (server) AdvancementStacks.create(icon);
            id(string(icon, "id", string(icon, "item", "minecraft:paper")));
            for (String field : new String[]{"data", "count"}) if (icon.has(field)) integer(icon.get(field), field.equals("count") ? 1 : 0, 32767);
            if (icon.has("components")) {
                JsonObject components = icon.getAsJsonObject("components");
                if (components.has("minecraft:damage")) integer(components.get("minecraft:damage"), 0, Integer.MAX_VALUE);
            }
            if (display.has("background")) id(display.get("background").getAsString());
            for (String field : new String[]{"show_toast", "announce_to_chat", "hidden"}) {
                if (display.has(field) && (!display.get(field).isJsonPrimitive() || !display.get(field).getAsJsonPrimitive().isBoolean()))
                    throw new IllegalArgumentException("Display flag must be boolean: " + field);
            }
            String frame = string(display, "frame", "task");
            if (!frame.equals("task") && !frame.equals("goal") && !frame.equals("challenge")) throw new IllegalArgumentException("Unknown advancement frame: " + frame);
        }
        Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
        JsonObject entries = json.getAsJsonObject("criteria");
        if (entries == null) throw new IllegalArgumentException("Missing criteria");
        for (Map.Entry<String, JsonElement> entry : entries.entrySet()) {
            JsonObject value = entry.getValue().getAsJsonObject();
            ResourceLocation triggerId = id(value.get("trigger").getAsString());
            SimpleCriterionTrigger<?> trigger = server ? Advancements.getTrigger(triggerId) : CLIENT_TRIGGER;
            if (trigger == null) throw new IllegalArgumentException("Unregistered advancement trigger: " + triggerId);
            JsonObject conditions = value.has("conditions") ? value.getAsJsonObject("conditions") : new JsonObject();
            if (server) AdvancementPredicates.validatePlayer(conditions.get("player"));
            criteria.put(entry.getKey(), trigger.parse(conditions));
        }
        AdvancementRequirements requirements = AdvancementRequirements.parse(json.getAsJsonArray("requirements"), criteria.keySet());
        JsonObject rewards = json.has("rewards") ? json.getAsJsonObject("rewards") : new JsonObject();
        return new Advancement(id, parent, display, criteria, requirements, rewards, json);
    }

    public static String string(JsonObject json, String name, String fallback) {
        return json.has(name) ? json.get(name).getAsString() : fallback;
    }

    public static int integer(JsonElement json, int min, int max) {
        if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isNumber()) throw new IllegalArgumentException("Expected an integer: " + json);
        double value = json.getAsDouble();
        if (!Double.isFinite(value) || value != Math.rint(value) || value < min || value > max) throw new IllegalArgumentException("Integer out of range: " + json);
        return (int) value;
    }

    public static boolean flag(JsonObject json, String name, boolean fallback) {
        return json.has(name) ? json.get(name).getAsBoolean() : fallback;
    }
}
