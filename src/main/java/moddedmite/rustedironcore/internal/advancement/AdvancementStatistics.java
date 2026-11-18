package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.advancement.Advancements;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
import net.minecraft.StatBase;
import net.minecraft.StatList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ToDoubleFunction;

public class AdvancementStatistics {

    private static final Map<String, ToDoubleFunction<EntityPlayer>> ADAPTERS = new HashMap<>();

    public static void register(String type, String id, ToDoubleFunction<EntityPlayer> getter) {
        ADAPTERS.put(AdvancementJson.id(type) + "/" + AdvancementJson.id(id), getter);
    }

    public static void validate(JsonObject json) {
        AdvancementPredicates.keys(json, "type", "stat", "value");
        String type = AdvancementJson.id(json.get("type").getAsString()).toString();
        String id = AdvancementJson.id(json.get("stat").getAsString()).toString();
        AdvancementPredicates.validateRange(json.get("value"));
        if (!ADAPTERS.containsKey(type + "/" + id) && nativeStat(type, id) == null) throw new IllegalArgumentException("Unsupported statistic: " + type + " " + id);
    }

    public static boolean matches(JsonObject json, EntityPlayer player) {
        String type = AdvancementJson.id(json.get("type").getAsString()).toString();
        String id = AdvancementJson.id(json.get("stat").getAsString()).toString();
        ToDoubleFunction<EntityPlayer> adapter = ADAPTERS.get(type + "/" + id);
        if (adapter != null) return AdvancementPredicates.range(json.get("value"), adapter.applyAsDouble(player));
        StatBase stat = nativeStat(type, id);
        Object value = stat == null ? null : player.stats.get(stat.statId);
        return AdvancementPredicates.range(json.get("value"), value instanceof Number number ? number.doubleValue() : 0);
    }

    private static StatBase nativeStat(String type, String id) {
        if (type.equals("minecraft:custom")) return switch (id) {
            case "minecraft:play_time" -> StatList.minutesPlayedStat;
            case "minecraft:walk_one_cm" -> StatList.distanceWalkedStat;
            case "minecraft:swim_one_cm" -> StatList.distanceSwumStat;
            case "minecraft:fall_one_cm" -> StatList.distanceFallenStat;
            case "minecraft:climb_one_cm" -> StatList.distanceClimbedStat;
            case "minecraft:fly_one_cm" -> StatList.distanceFlownStat;
            case "minecraft:boat_one_cm" -> StatList.distanceByBoatStat;
            case "minecraft:minecart_one_cm" -> StatList.distanceByMinecartStat;
            case "minecraft:pig_one_cm" -> StatList.distanceByPigStat;
            case "minecraft:jump" -> StatList.jumpStat;
            case "minecraft:drop" -> StatList.dropStat;
            case "minecraft:damage_dealt" -> StatList.damageDealtStat;
            case "minecraft:damage_taken" -> StatList.damageTakenStat;
            case "minecraft:deaths" -> StatList.deathsStat;
            case "minecraft:mob_kills" -> StatList.mobKillsStat;
            case "minecraft:player_kills" -> StatList.playerKillsStat;
            case "minecraft:fish_caught" -> StatList.fishCaughtStat;
            default -> null;
        };
        ItemStack item = Advancements.resolveItem(id);
        if (item == null) return null;
        StatBase[] stats = switch (type) {
            case "minecraft:crafted" -> StatList.objectCraftStats;
            case "minecraft:used" -> StatList.objectUseStats;
            case "minecraft:broken" -> StatList.objectBreakStats;
            case "minecraft:mined" -> StatList.mineBlockStatArray;
            default -> null;
        };
        return stats == null || item.itemID >= stats.length ? null : stats[item.itemID];
    }
}
