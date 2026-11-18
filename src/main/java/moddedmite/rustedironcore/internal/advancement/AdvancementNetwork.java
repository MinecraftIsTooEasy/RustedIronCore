package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import moddedmite.rustedironcore.api.advancement.Advancement;
import moddedmite.rustedironcore.api.advancement.AdvancementProgress;
import moddedmite.rustedironcore.api.advancement.Advancements;
import moddedmite.rustedironcore.api.advancement.PlayerAdvancements;
import moddedmite.rustedironcore.internal.network.packets.S2CAdvancements;
import moddedmite.rustedironcore.network.Network;
import net.minecraft.ServerPlayer;
import net.minecraft.ResourceLocation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPOutputStream;

public class AdvancementNetwork {

    public static final int MAX_PAYLOAD = 16 * 1024 * 1024;
    public static final int FRAGMENT_SIZE = 24000;
    private static final AtomicInteger TRANSFERS = new AtomicInteger();

    public static void sendSnapshot(ServerPlayer player) {
        JsonObject json = new JsonObject();
        json.addProperty("snapshot", true);
        JsonObject definitions = new JsonObject();
        Advancements.all().forEach(advancement -> definitions.add(advancement.id().toString(), advancement.json()));
        json.add("definitions", definitions);
        JsonObject progress = new JsonObject();
        PlayerAdvancements state = Advancements.getProgress(player);
        for (Advancement advancement : Advancements.all()) {
            progress.add(advancement.id().toString(), progress(state.getProgress(advancement.id())));
        }
        json.add("progress", progress);
        JsonArray recipes = new JsonArray();
        state.recipes().forEach(id -> recipes.add(new JsonPrimitive(id)));
        json.add("recipes", recipes);
        send(player, json);
    }

    public static void sendRecipe(ServerPlayer player, ResourceLocation recipe) {
        JsonObject json = new JsonObject();
        json.addProperty("snapshot", false);
        json.addProperty("recipe", recipe.toString());
        send(player, json);
    }

    public static void sendProgress(ServerPlayer player, Advancement advancement, AdvancementProgress progress, boolean completed) {
        JsonObject json = new JsonObject();
        json.addProperty("snapshot", false);
        json.addProperty("id", advancement.id().toString());
        json.addProperty("completed", completed);
        json.add("progress", progress(progress));
        send(player, json);
    }

    private static JsonObject progress(AdvancementProgress progress) {
        JsonObject json = new JsonObject();
        progress.criteria().forEach(json::addProperty);
        return json;
    }

    private static void send(ServerPlayer player, JsonObject json) {
        byte[] source = json.toString().getBytes(StandardCharsets.UTF_8);
        if (source.length > MAX_PAYLOAD) throw new IllegalStateException("Advancement synchronization exceeds 16 MiB");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (GZIPOutputStream compressed = new GZIPOutputStream(output)) {
            compressed.write(source);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
        byte[] bytes = output.toByteArray();
        int transfer = TRANSFERS.incrementAndGet();
        for (int offset = 0; offset < bytes.length; offset += FRAGMENT_SIZE) {
            int end = Math.min(bytes.length, offset + FRAGMENT_SIZE);
            Network.sendToClient(player, new S2CAdvancements(transfer, offset / FRAGMENT_SIZE, end == bytes.length, Arrays.copyOfRange(bytes, offset, end)));
        }
    }
}
