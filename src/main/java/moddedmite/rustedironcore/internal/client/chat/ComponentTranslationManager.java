package moddedmite.rustedironcore.internal.client.chat;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.internal.chat.JsonComponentTranslations;
import net.minecraft.Minecraft;
import net.minecraft.Resource;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.ResourceManagerReloadListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class ComponentTranslationManager implements ResourceManagerReloadListener {

    public static final ComponentTranslationManager INSTANCE = new ComponentTranslationManager();

    private volatile Map<String, TextComponent> componentTranslations = Map.of();
    private volatile String loadedLanguage;

    private ComponentTranslationManager() {
    }

    public static Optional<TextComponent> get(String key) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null || minecraft.gameSettings == null || minecraft.getResourceManager() == null) {
            return Optional.empty();
        }

        String language = languageCode(minecraft.gameSettings.language);
        if (!language.equals(INSTANCE.loadedLanguage)) {
            INSTANCE.reload(minecraft.getResourceManager(), language);
        }
        TextComponent component = INSTANCE.componentTranslations.get(key);
        return component == null ? Optional.empty() : Optional.of(component.copy());
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Minecraft minecraft = Minecraft.getMinecraft();
        String language = minecraft == null || minecraft.gameSettings == null
                ? "en_us"
                : languageCode(minecraft.gameSettings.language);
        this.reload(resourceManager, language);
    }

    private synchronized void reload(ResourceManager resourceManager, String language) {
        Map<String, TextComponent> loaded = new HashMap<>();
        List<String> languages = language.equals("en_us") ? List.of("en_us") : List.of("en_us", language);
        for (String languageCode : languages) {
            String path = "lang/" + languageCode + ".json";
            for (Object domainObject : resourceManager.getResourceDomains()) {
                String domain = (String) domainObject;
                try {
                    for (Object resourceObject : resourceManager.getAllResources(new ResourceLocation(domain, path))) {
                        Resource resource = (Resource) resourceObject;
                        try (InputStream stream = resource.getInputStream();
                             InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                            JsonElement root = new JsonParser().parse(reader);
                            if (root.isJsonObject()) {
                                JsonComponentTranslations.applyDocument(root.getAsJsonObject(), loaded);
                            }
                        } catch (RuntimeException | IOException ignored) {
                            // Malformed or unavailable locale resources do not prevent other packs from loading.
                        }
                    }
                } catch (Exception ignored) {
                    // A missing translation file is normal for most resource domains.
                }
            }
        }
        this.componentTranslations = Map.copyOf(loaded);
        this.loadedLanguage = language;
    }

    private static String languageCode(String language) {
        return language == null || language.isBlank() ? "en_us" : language.replace('-', '_').toLowerCase(Locale.ROOT);
    }
}
