package moddedmite.rustedironcore.api.sound;

import com.google.gson.JsonParser;
import moddedmite.rustedironcore.api.util.LogUtil;
import moddedmite.rustedironcore.mixin.client.audio.SoundPoolInvoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Minecraft;
import net.minecraft.Resource;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.SoundPool;
import net.minecraft.SoundPoolEntry;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.logging.log4j.Logger;

/** Plays an explicitly requested OGG resource through the normal client volume/mute/source lifecycle. */
@Environment(EnvType.CLIENT)
public final class ResourceSoundPlayer {

    private static final Map<String, SoundPoolEntry> ENTRIES = new HashMap<>();
    private static final JsonSoundEvents EVENTS = new JsonSoundEvents();
    private static final Logger LOGGER = LogUtil.getLogger();
    private static boolean eventsLoaded;

    /** Plays a sounds.json event, including the selected packs' append/replace mappings. */
    public static void playEvent(ResourceLocation event, float volume, float pitch) {
        if (!eventsLoaded) loadEvents(Minecraft.getMinecraft().getResourceManager());
        JsonSoundEvents.Selection selected = EVENTS.resolve(event.toString(), () -> ThreadLocalRandom.current().nextDouble());
        if (selected == null) return;
        ResourceLocation sound = new ResourceLocation(selected.file(), false);
        play(new ResourceLocation(sound.getResourceDomain(), "sounds/" + sound.getResourcePath() + ".ogg", false),
                volume * selected.volume(), pitch * selected.pitch());
    }

    private static void loadEvents(ResourceManager resources) {
        for (Object domain : resources.getResourceDomains()) {
            String namespace = (String) domain;
            try {
                for (Object resource : resources.getAllResources(new ResourceLocation(namespace, "sounds.json", false))) {
                    try (var reader = new InputStreamReader(((Resource) resource).getInputStream(), StandardCharsets.UTF_8)) {
                        EVENTS.merge(namespace, new JsonParser().parse(reader).getAsJsonObject());
                    } catch (RuntimeException | IOException invalid) {
                        LOGGER.warn("Failed to read {}/sounds.json", namespace, invalid);
                    }
                }
            } catch (Exception failure) {
                // The legacy ResourceManager interface omits its implementation's IOException declaration.
                if (!(failure instanceof IOException)) LOGGER.warn("Failed to enumerate {}/sounds.json", namespace, failure);
            }
        }
        eventsLoaded = true;
    }

    public static void play(ResourceLocation file, float volume, float pitch) {
        Minecraft client = Minecraft.getMinecraft();
        String path = file.getResourcePath();
        if (!path.startsWith("sounds/") || !path.endsWith(".ogg"))
            throw new IllegalArgumentException("Expected a sounds/*.ogg resource: " + file);
        String token = "ric.resource:" + file;
        try {
            // Test availability after every reload; removed pack files must not leave stale sounds.
            try (var stream = client.getResourceManager().getResource(file).getInputStream()) {
                if (!ENTRIES.containsKey(token)) {
                    SoundPool pool = new SoundPool(client.getResourceManager(), "sounds", false);
                    String sound = file.getResourceDomain() + ":" + path.substring("sounds/".length());
                    ENTRIES.put(token, new SoundPoolEntry(sound, ((SoundPoolInvoker) pool).ric$getSoundUrl(sound)));
                }
            }
            client.sndManager.playSoundFX(token, volume, pitch);
        } catch (IOException missing) {
            ENTRIES.remove(token);
        }
    }

    public static SoundPoolEntry lookup(String token) {
        return ENTRIES.get(token);
    }

    /** The cached resource URLs belong to the pack stack from the last reload. */
    public static void clearCache() {
        ENTRIES.clear();
        EVENTS.clear();
        eventsLoaded = false;
    }
}
