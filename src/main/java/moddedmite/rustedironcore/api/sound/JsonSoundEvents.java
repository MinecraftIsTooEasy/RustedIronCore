package moddedmite.rustedironcore.api.sound;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleSupplier;

/** Resource-pack sound event definitions, merged from low to high pack priority. */
public final class JsonSoundEvents {

    public record Selection(String file, float volume, float pitch) {}

    private record Sound(String name, boolean event, int weight, float volume, float pitch) {}

    private record WeightedSelection(Selection sound, int weight) {}

    private final Map<String, List<Sound>> events = new HashMap<>();

    public void merge(String namespace, JsonObject definitions) {
        for (var entry : definitions.entrySet()) {
            JsonObject definition = entry.getValue().getAsJsonObject();
            List<Sound> sounds = new ArrayList<>();
            if (definition.has("sounds")) for (JsonElement value : definition.getAsJsonArray("sounds")) {
                if (value.isJsonPrimitive()) {
                    sounds.add(new Sound(identifier(value.getAsString()), false, 1, 1, 1));
                } else {
                    JsonObject sound = value.getAsJsonObject();
                    String type = sound.has("type") ? sound.get("type").getAsString() : "file";
                    if (!type.equals("file") && !type.equals("event"))
                        throw new IllegalArgumentException("Unknown sound type: " + type);
                    int weight = sound.has("weight") ? sound.get("weight").getAsInt() : 1;
                    float volume = sound.has("volume") ? sound.get("volume").getAsFloat() : 1;
                    float pitch = sound.has("pitch") ? sound.get("pitch").getAsFloat() : 1;
                    if (weight <= 0 || !Float.isFinite(volume) || volume <= 0 || !Float.isFinite(pitch) || pitch <= 0)
                        throw new IllegalArgumentException("Invalid sound weight, volume or pitch");
                    sounds.add(new Sound(identifier(sound.get("name").getAsString()), type.equals("event"), weight, volume, pitch));
                }
            }
            String id = namespace + ":" + entry.getKey();
            if (!definition.has("replace") || !definition.get("replace").getAsBoolean()) {
                List<Sound> merged = new ArrayList<>(events.getOrDefault(id, List.of()));
                merged.addAll(sounds);
                sounds = merged;
            }
            events.put(id, List.copyOf(sounds));
        }
    }

    public Selection resolve(String event, DoubleSupplier random) {
        List<WeightedSelection> choices = new ArrayList<>();
        collect(identifier(event), 1, 1, new HashSet<>(), choices);
        double total = choices.stream().mapToDouble(WeightedSelection::weight).sum();
        if (total == 0) return null;
        double selected = random.getAsDouble() * total;
        for (WeightedSelection choice : choices) {
            selected -= choice.weight;
            if (selected < 0) return choice.sound;
        }
        return choices.get(choices.size() - 1).sound;
    }

    private void collect(String event, float volume, float pitch, Set<String> visiting, List<WeightedSelection> choices) {
        if (!visiting.add(event)) return;
        for (Sound sound : events.getOrDefault(event, List.of())) {
            if (sound.event) {
                // A referenced event contributes its children's weights to the parent event.
                collect(sound.name, volume * sound.volume, pitch * sound.pitch, visiting, choices);
            } else {
                choices.add(new WeightedSelection(new Selection(sound.name, volume * sound.volume, pitch * sound.pitch), sound.weight));
            }
        }
        visiting.remove(event);
    }

    public void clear() { events.clear(); }

    private static String identifier(String name) { return name.indexOf(':') < 0 ? "minecraft:" + name : name; }
}
