package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Random;

public class AdvancementNumberProviders {

    public static void validate(JsonElement json) {
        if (json == null || json.isJsonNull()) throw new IllegalArgumentException("Missing number provider");
        if (json.isJsonPrimitive()) {
            AdvancementPredicates.validateRange(json);
            return;
        }
        JsonObject provider = json.getAsJsonObject();
        switch (AdvancementJson.id(provider.get("type").getAsString()).toString()) {
            case "minecraft:constant" -> {
                AdvancementPredicates.keys(provider, "type", "value");
                AdvancementPredicates.validateRange(provider.get("value"));
                if (!provider.get("value").isJsonPrimitive()) throw new IllegalArgumentException("Expected constant number");
            }
            case "minecraft:uniform" -> {
                AdvancementPredicates.keys(provider, "type", "min", "max");
                validate(provider.get("min"));
                validate(provider.get("max"));
                Double min = constant(provider.get("min")), max = constant(provider.get("max"));
                if (min != null && max != null && min > max) throw new IllegalArgumentException("Inverted uniform provider");
            }
            case "minecraft:binomial" -> {
                AdvancementPredicates.keys(provider, "type", "n", "p");
                validate(provider.get("n"));
                validate(provider.get("p"));
                Double n = constant(provider.get("n")), p = constant(provider.get("p"));
                if (n != null && (n < 0 || n > 65536)) throw new IllegalArgumentException("Binomial trials outside [0, 65536]");
                if (p != null && (p < 0 || p > 1)) throw new IllegalArgumentException("Binomial probability outside [0, 1]");
            }
            default -> throw new IllegalArgumentException("Unsupported number provider: " + provider.get("type"));
        }
    }

    private static Double constant(JsonElement json) {
        if (json.isJsonPrimitive()) return json.getAsDouble();
        JsonObject provider = json.getAsJsonObject();
        return AdvancementJson.id(provider.get("type").getAsString()).toString().equals("minecraft:constant") ? provider.get("value").getAsDouble() : null;
    }

    public static void validateIntRange(JsonElement json) {
        if (json.isJsonPrimitive()) {
            AdvancementJson.integer(json, Integer.MIN_VALUE, Integer.MAX_VALUE);
            return;
        }
        JsonObject range = json.getAsJsonObject();
        AdvancementPredicates.keys(range, "min", "max");
        if (range.has("min")) validate(range.get("min"));
        if (range.has("max")) validate(range.get("max"));
        Double min = range.has("min") ? constant(range.get("min")) : null;
        Double max = range.has("max") ? constant(range.get("max")) : null;
        if (min != null && max != null && min > max) throw new IllegalArgumentException("Inverted integer range");
    }

    public static int clamp(JsonElement json, int value, Random random) {
        if (json.isJsonPrimitive()) return json.getAsInt();
        JsonObject range = json.getAsJsonObject();
        int min = range.has("min") ? sampleInt(range.get("min"), random) : Integer.MIN_VALUE;
        int max = range.has("max") ? sampleInt(range.get("max"), random) : Integer.MAX_VALUE;
        if (min > max) throw new IllegalArgumentException("Inverted integer range");
        return Math.max(min, Math.min(max, value));
    }

    public static double sample(JsonElement json, Random random) {
        if (json.isJsonPrimitive()) return json.getAsDouble();
        JsonObject provider = json.getAsJsonObject();
        return switch (AdvancementJson.id(provider.get("type").getAsString()).toString()) {
            case "minecraft:constant" -> provider.get("value").getAsDouble();
            case "minecraft:uniform" -> {
                double min = sample(provider.get("min"), random), max = sample(provider.get("max"), random);
                if (min > max) throw new IllegalArgumentException("Inverted uniform provider");
                yield min + random.nextDouble() * (max - min);
            }
            case "minecraft:binomial" -> {
                double trials = sample(provider.get("n"), random);
                if (trials < 0 || trials > 65536) throw new IllegalArgumentException("Binomial trials outside [0, 65536]");
                int count = (int) Math.floor(trials);
                double probability = sample(provider.get("p"), random);
                if (probability < 0 || probability > 1) throw new IllegalArgumentException("Binomial probability outside [0, 1]");
                int successes = 0;
                for (int i = 0; i < count; i++) if (random.nextFloat() < probability) successes++;
                yield successes;
            }
            default -> throw new IllegalArgumentException("Unknown number provider");
        };
    }

    public static int sampleInt(JsonElement json, Random random) {
        if (json.isJsonObject() && AdvancementJson.id(json.getAsJsonObject().get("type").getAsString()).toString().equals("minecraft:uniform")) {
            JsonObject provider = json.getAsJsonObject();
            int min = (int) Math.floor(sample(provider.get("min"), random));
            int max = (int) Math.floor(sample(provider.get("max"), random));
            if (min > max) throw new IllegalArgumentException("Inverted uniform provider");
            return (int) random.nextLong(min, (long) max + 1);
        }
        return (int) Math.floor(sample(json, random));
    }
}
