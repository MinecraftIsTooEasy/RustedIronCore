package moddedmite.rustedironcore.api.sound;

import net.minecraft.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/** Register event constants during mod initialization; sounds.json supplies their audio resources. */
public final class SoundEvents {

    private static final Map<String, SoundEvent> EVENTS = new LinkedHashMap<>();

    public static SoundEvent register(ResourceLocation id) {
        Objects.requireNonNull(id, "id");
        SoundEvent event = new SoundEvent(id);
        if (EVENTS.putIfAbsent(id.toString(), event) != null) throw new IllegalArgumentException("Duplicate sound event: " + id);
        return event;
    }

    public static SoundEvent get(String id) {
        return EVENTS.get(id);
    }
}
