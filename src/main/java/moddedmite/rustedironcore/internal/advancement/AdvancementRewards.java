package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.advancement.Advancement;
import moddedmite.rustedironcore.api.advancement.AdvancementRecipes;
import moddedmite.rustedironcore.api.chat.ChatComponents;
import moddedmite.rustedironcore.api.util.LogUtil;
import net.minecraft.ResourceLocation;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** Validates reward adapters and applies completion effects on the server. */
public class AdvancementRewards {

    private static final Map<String, Consumer<ServerPlayer>> REWARDS = new HashMap<>();
    private static final List<BiConsumer<ServerPlayer, Advancement>> COMPLETION_LISTENERS = new ArrayList<>();

    /** Register MITE equivalents for loot tables, recipe unlocks, or functions used by rewards. */
    public static void register(String kind, ResourceLocation id, Consumer<ServerPlayer> reward) {
        if (!Set.of("loot", "recipes", "function").contains(kind)) throw new IllegalArgumentException("Unknown advancement reward kind: " + kind);
        if (REWARDS.putIfAbsent(kind + ":" + id, reward) != null) throw new IllegalArgumentException("Duplicate advancement reward: " + kind + " " + id);
    }

    public static void onCompleted(BiConsumer<ServerPlayer, Advancement> listener) {
        COMPLETION_LISTENERS.add(listener);
    }

    public static void validate(JsonObject rewards) {
        for (Map.Entry<String, JsonElement> entry : rewards.entrySet()) {
            String kind = entry.getKey();
            if (kind.equals("experience")) {
                AdvancementJson.integer(entry.getValue(), 0, Integer.MAX_VALUE);
                continue;
            }
            if (!Set.of("loot", "recipes", "function").contains(kind)) throw new IllegalArgumentException("Unknown advancement reward: " + kind);
            if (kind.equals("function")) validateReward(kind, entry.getValue().getAsString());
            else for (JsonElement id : entry.getValue().getAsJsonArray()) validateReward(kind, id.getAsString());
        }
    }

    private static void validateReward(String kind, String id) {
        id = AdvancementJson.id(id).toString();
        if (REWARDS.containsKey(kind + ":" + id)) return;
        switch (kind) {
            case "recipes" -> {
                if (!AdvancementRecipes.contains(AdvancementJson.id(id))) throw new IllegalArgumentException("Unregistered recipe: " + id);
            }
            case "function" -> AdvancementFunctions.validate(id);
            case "loot" -> AdvancementLootTables.validate(id);
            default -> throw new IllegalArgumentException("Unsupported reward: " + kind);
        }
    }

    private static void apply(String kind, String id, ServerPlayer player) {
        id = AdvancementJson.id(id).toString();
        try {
            Consumer<ServerPlayer> adapter = REWARDS.get(kind + ":" + id);
            if (adapter != null) adapter.accept(player);
            else switch (kind) {
                case "recipes" -> AdvancementRecipes.unlock(player, AdvancementJson.id(id));
                case "function" -> AdvancementFunctions.run(id, player);
                case "loot" -> AdvancementLootTables.give(id, player);
                default -> throw new IllegalArgumentException("Unsupported reward: " + kind);
            }
        } catch (RuntimeException exception) {
            LogUtil.getLogger().warn("Unable to apply advancement reward {} {} to {}", kind, id, player.getCommandSenderName(), exception);
        }
    }

    public static void completed(ServerPlayer player, Advancement advancement) {
        JsonObject rewards = advancement.rewards();
        if (rewards.has("experience")) player.addExperience(rewards.get("experience").getAsInt());
        for (String kind : List.of("loot", "recipes")) {
            if (rewards.has(kind)) for (JsonElement id : rewards.getAsJsonArray(kind)) apply(kind, id.getAsString(), player);
        }
        if (rewards.has("function")) apply("function", rewards.get("function").getAsString(), player);
        for (BiConsumer<ServerPlayer, Advancement> listener : COMPLETION_LISTENERS) {
            try {
                listener.accept(player, advancement);
            } catch (RuntimeException exception) {
                LogUtil.getLogger().warn("Advancement completion listener failed for {}", advancement.id(), exception);
            }
        }
        JsonObject display = advancement.display();
        if (display != null && AdvancementJson.flag(display, "announce_to_chat", true)
                && (!player.worldObj.getGameRules().hasRule("announceAdvancements") || Boolean.parseBoolean(player.worldObj.getGameRules().getGameRuleStringValue("announceAdvancements")))) {
            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(
                    ChatComponents.create(AdvancementChat.announcement(player.getTranslatedEntityName(), display)));
        }
    }
}
