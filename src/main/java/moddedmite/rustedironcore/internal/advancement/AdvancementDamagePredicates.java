package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.DamageSource;
import net.minecraft.EntityPlayer;

public class AdvancementDamagePredicates {

    public static void validate(JsonObject json) {
        AdvancementPredicates.keys(json, "dealt", "taken", "blocked", "source_entity", "type");
        AdvancementPredicates.validateRange(json.get("dealt"));
        AdvancementPredicates.validateRange(json.get("taken"));
        AdvancementPredicates.validateEntity(json.get("source_entity"));
        if (json.has("type")) validateSource(json.getAsJsonObject("type"));
        if (json.has("blocked") && (!json.get("blocked").isJsonPrimitive() || !json.get("blocked").getAsJsonPrimitive().isBoolean())) throw new IllegalArgumentException("blocked must be boolean");
    }

    public static void validateSource(JsonObject json) {
        AdvancementPredicates.keys(json, "direct_entity", "source_entity", "is_direct", "tags");
        if (json.has("is_direct") && (!json.get("is_direct").isJsonPrimitive() || !json.get("is_direct").getAsJsonPrimitive().isBoolean())) throw new IllegalArgumentException("is_direct must be boolean");
        AdvancementPredicates.validateEntity(json.get("direct_entity"));
        AdvancementPredicates.validateEntity(json.get("source_entity"));
        if (json.has("tags")) for (JsonElement element : json.getAsJsonArray("tags")) {
            JsonObject tag = element.getAsJsonObject();
            AdvancementPredicates.keys(tag, "id", "expected");
            AdvancementJson.id(tag.get("id").getAsString());
            if (!tag.get("expected").isJsonPrimitive() || !tag.get("expected").getAsJsonPrimitive().isBoolean()) throw new IllegalArgumentException("Damage tag expected must be boolean");
        }
    }

    public static boolean matches(JsonObject json, EntityPlayer player, DamageSource source, float dealt, float taken, boolean blocked) {
        return AdvancementPredicates.range(json.get("dealt"), dealt) && AdvancementPredicates.range(json.get("taken"), taken)
                && (!json.has("blocked") || json.get("blocked").getAsBoolean() == blocked)
                && AdvancementPredicates.entity(json.get("source_entity"), player, source.getResponsibleEntity())
                && (!json.has("type") || source(json.getAsJsonObject("type"), player, source));
    }

    public static boolean source(JsonObject json, EntityPlayer player, DamageSource source) {
        if (!AdvancementPredicates.entity(json.get("direct_entity"), player, source.getImmediateEntity())
                || !AdvancementPredicates.entity(json.get("source_entity"), player, source.getResponsibleEntity())) return false;
        if (json.has("is_direct") && json.get("is_direct").getAsBoolean() != (source.getResponsibleEntity() == source.getImmediateEntity())) return false;
        if (json.has("tags")) for (JsonElement element : json.getAsJsonArray("tags")) {
            JsonObject tag = element.getAsJsonObject();
            if (tag(tag.get("id").getAsString(), source) != tag.get("expected").getAsBoolean()) return false;
        }
        return true;
    }

    private static boolean tag(String id, DamageSource source) {
        return switch (AdvancementJson.id(id).toString()) {
            case "minecraft:is_fire" -> source.isFireDamage();
            case "minecraft:is_explosion" -> source.isExplosion();
            case "minecraft:is_projectile" -> source.isProjectile();
            case "minecraft:is_fall" -> source.isFallDamage();
            case "minecraft:is_drowning" -> source.isDrowning();
            case "minecraft:bypasses_armor" -> source.bypassesMundaneArmor();
            case "minecraft:bypasses_invulnerability" -> source.canHarmInCreative();
            default -> AdvancementTags.matches("damage_type", id, member -> member.equals("minecraft:" + source.getDamageType()));
        };
    }
}
