package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.advancement.AdvancementContext;
import moddedmite.rustedironcore.api.advancement.JsonPredicate;
import net.minecraft.Entity;
import net.minecraft.EntityPlayer;
import net.minecraft.EntitySlime;

import java.util.HashMap;
import java.util.Map;

public class AdvancementEntitySubPredicates {

    private static final Map<String, JsonPredicate<AdvancementContext>> ADAPTERS = new HashMap<>();

    public static void register(String id, JsonPredicate<AdvancementContext> predicate) {
        ADAPTERS.put(AdvancementJson.id(id).toString(), predicate);
    }

    public static void validate(JsonObject json) {
        String type = AdvancementJson.id(AdvancementJson.string(json, "type", "player")).toString();
        JsonPredicate<AdvancementContext> adapter = ADAPTERS.get(type);
        if (adapter != null) { adapter.validate(json); return; }
        switch (type) {
            case "minecraft:player" -> AdvancementEntityPredicates.validatePlayer(json);
            case "minecraft:slime" -> {
                AdvancementPredicates.keys(json, "type", "size");
                AdvancementPredicates.validateRange(json.get("size"));
            }
            case "minecraft:lightning" -> {
                AdvancementPredicates.keys(json, "type", "blocks_set_on_fire", "entity_struck");
                AdvancementPredicates.validateRange(json.get("blocks_set_on_fire"));
                AdvancementEntityPredicates.validate(json.get("entity_struck"));
            }
            default -> throw new IllegalArgumentException("Register an entity sub-predicate adapter for " + type);
        }
    }

    public static boolean matches(JsonObject json, EntityPlayer player, Entity entity) {
        String type = AdvancementJson.id(AdvancementJson.string(json, "type", "player")).toString();
        JsonPredicate<AdvancementContext> adapter = ADAPTERS.get(type);
        if (adapter != null) return adapter.matches(json, AdvancementContext.of(player, entity));
        return switch (type) {
            case "minecraft:player" -> AdvancementEntityPredicates.player(json, entity);
            case "minecraft:slime" -> entity instanceof EntitySlime slime && AdvancementPredicates.range(json.get("size"), slime.getSize());
            case "minecraft:lightning" -> entity instanceof AdvancementLightning lightning
                    && AdvancementPredicates.range(json.get("blocks_set_on_fire"), lightning.ric$getBlocksSetOnFire())
                    && (!json.has("entity_struck") || lightning.ric$getStruckEntities().stream().anyMatch(struck -> AdvancementEntityPredicates.matches(json.get("entity_struck"), player, struck)));
            default -> false;
        };
    }
}
