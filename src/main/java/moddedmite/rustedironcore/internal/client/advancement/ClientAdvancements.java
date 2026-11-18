package moddedmite.rustedironcore.internal.client.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import moddedmite.rustedironcore.api.advancement.Advancement;
import moddedmite.rustedironcore.api.advancement.AdvancementProgress;
import moddedmite.rustedironcore.api.advancement.Advancements;
import moddedmite.rustedironcore.api.advancement.AdvancementRecipes;
import moddedmite.rustedironcore.api.chat.TextComponentSerializer;
import moddedmite.rustedironcore.api.util.LogUtil;
import moddedmite.rustedironcore.client.api.hud.AdvancementToast;
import moddedmite.rustedironcore.client.api.hud.AdvancementToastFrame;
import moddedmite.rustedironcore.client.api.hud.RecipeToast;
import moddedmite.rustedironcore.internal.advancement.AdvancementJson;
import moddedmite.rustedironcore.internal.advancement.AdvancementNetwork;
import moddedmite.rustedironcore.internal.client.hud.ClientHud;
import moddedmite.rustedironcore.internal.network.packets.S2CAdvancements;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
import net.minecraft.Block;
import net.minecraft.ResourceLocation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;

@Environment(EnvType.CLIENT)
public class ClientAdvancements {

    public static final Map<String, Advancement> DEFINITIONS = new LinkedHashMap<>();
    public static final Map<String, AdvancementProgress> PROGRESS = new LinkedHashMap<>();
    private static ByteArrayOutputStream pending;
    private static int transfer;
    private static int nextIndex;
    private static int revision;
    private static boolean receivedSnapshot;

    public static int revision() {
        return revision;
    }

    public static boolean isAwaitingSnapshot() {
        return !receivedSnapshot;
    }

    public static void clear() {
        DEFINITIONS.clear();
        PROGRESS.clear();
        pending = null;
        receivedSnapshot = false;
        revision++;
    }

    public static void receive(S2CAdvancements fragment, EntityPlayer player) {
        try {
            if (fragment.index() == 0) {
                pending = new ByteArrayOutputStream();
                transfer = fragment.transfer();
                nextIndex = 0;
            }
            if (pending == null || fragment.transfer() != transfer || fragment.index() != nextIndex++) throw new IllegalArgumentException("Out-of-order advancement fragment");
            if (pending.size() + fragment.bytes().length > AdvancementNetwork.MAX_PAYLOAD) throw new IllegalArgumentException("Oversized advancement synchronization");
            pending.write(fragment.bytes());
            if (!fragment.last()) return;
            byte[] compressed = pending.toByteArray();
            pending = null;
            byte[] bytes;
            try (GZIPInputStream input = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
                bytes = input.readNBytes(AdvancementNetwork.MAX_PAYLOAD + 1);
            }
            if (bytes.length > AdvancementNetwork.MAX_PAYLOAD) throw new IllegalArgumentException("Oversized advancement data");
            apply(new JsonParser().parse(new String(bytes, StandardCharsets.UTF_8)).getAsJsonObject(), player);
        } catch (IOException | RuntimeException exception) {
            pending = null;
            LogUtil.getLogger().warn("Unable to receive advancements", exception);
        }
    }

    private static void apply(JsonObject json, EntityPlayer player) {
        if (json.get("snapshot").getAsBoolean()) {
            clear();
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("definitions").entrySet()) {
                DEFINITIONS.put(entry.getKey(), AdvancementJson.parse(AdvancementJson.id(entry.getKey()), entry.getValue().getAsJsonObject(), false));
            }
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("progress").entrySet()) update(entry.getKey(), entry.getValue().getAsJsonObject());
            Set<String> recipes = new LinkedHashSet<>();
            for (JsonElement recipe : json.getAsJsonArray("recipes")) recipes.add(AdvancementJson.id(recipe.getAsString()).toString());
            Advancements.getProgress(player).replaceRecipes(recipes);
            receivedSnapshot = true;
        } else if (json.has("recipe")) {
            ResourceLocation id = AdvancementJson.id(json.get("recipe").getAsString());
            if (Advancements.getProgress(player).unlockRecipe(id)) {
                ItemStack result = AdvancementRecipes.getResult(id);
                if (result != null) ClientHud.getToasts().addToast(new RecipeToast(new ItemStack(Block.workbench), result));
            }
        } else {
            String id = json.get("id").getAsString();
            update(id, json.getAsJsonObject("progress"));
            Advancement advancement = DEFINITIONS.get(id);
            if (advancement != null && json.get("completed").getAsBoolean() && advancement.display() != null
                    && AdvancementJson.flag(advancement.display(), "show_toast", true)) {
                JsonObject display = advancement.display();
                AdvancementToastFrame frame = AdvancementToastFrame.valueOf(AdvancementJson.string(display, "frame", "task").toUpperCase(Locale.ROOT));
                ClientHud.getToasts().addToast(new AdvancementToast(advancement.id(), TextComponentSerializer.fromJson(display.get("title")),
                        AdvancementIcons.icon(display.getAsJsonObject("icon")), frame));
            }
        }
        revision++;
    }

    private static void update(String id, JsonObject criteria) {
        AdvancementProgress progress = new AdvancementProgress();
        criteria.entrySet().forEach(entry -> progress.restore(entry.getKey(), entry.getValue().getAsLong()));
        PROGRESS.put(id, progress);
    }

    public static AdvancementProgress progress(Advancement advancement) {
        return PROGRESS.getOrDefault(advancement.id().toString(), new AdvancementProgress());
    }
}
