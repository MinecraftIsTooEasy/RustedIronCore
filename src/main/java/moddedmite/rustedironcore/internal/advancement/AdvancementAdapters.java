package moddedmite.rustedironcore.internal.advancement;

import net.minecraft.BiomeGenBase;
import net.minecraft.Enchantment;
import net.minecraft.Entity;
import net.minecraft.EntityList;
import net.minecraft.EntityPlayer;
import net.minecraft.EntityLightningBolt;
import net.minecraft.EntityFishHook;
import net.minecraft.Potion;

import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.function.Predicate;

/** Registry aliases and explicit adapters for content supplied by extension mods. */
public class AdvancementAdapters {

    private static final Map<String, Predicate<Entity>> ENTITY_TYPES = new HashMap<>();
    private static final Map<String, Enchantment> ENCHANTMENTS = new HashMap<>();
    private static final Map<String, Potion> EFFECTS = new HashMap<>();
    private static final Map<String, Predicate<BiomeGenBase>> BIOMES = new HashMap<>();

    public static void registerEntityType(String id, Predicate<Entity> predicate) {
        ENTITY_TYPES.put(AdvancementJson.id(id).toString(), predicate);
    }

    public static void registerEnchantment(String id, Enchantment enchantment) {
        ENCHANTMENTS.put(AdvancementJson.id(id).toString(), enchantment);
    }

    public static void registerEffect(String id, Potion effect) {
        EFFECTS.put(AdvancementJson.id(id).toString(), effect);
    }

    public static void registerBiome(String id, Predicate<BiomeGenBase> predicate) {
        BIOMES.put(AdvancementJson.id(id).toString(), predicate);
    }

    public static boolean entityType(String id, Entity entity) {
        if (entity == null) return false;
        if (id.startsWith("#")) return AdvancementTags.matches("entity_type", id.substring(1), member -> entityType(member, entity));
        id = AdvancementJson.id(id).toString();
        Predicate<Entity> adapter = ENTITY_TYPES.get(id);
        if (adapter != null) return adapter.test(entity);
        if (!id.startsWith("minecraft:")) return false;
        if (id.equals("minecraft:player")) return entity instanceof EntityPlayer;
        if (id.equals("minecraft:lightning_bolt")) return entity instanceof EntityLightningBolt;
        if (id.equals("minecraft:fishing_bobber")) return entity instanceof EntityFishHook;
        String legacy = EntityList.getEntityString(entity);
        if (legacy == null) return false;
        String path = switch (legacy) {
            case "PigZombie" -> "zombified_piglin";
            case "EntityHorse" -> "horse";
            case "VillagerGolem" -> "iron_golem";
            case "LavaSlime" -> "magma_cube";
            case "MushroomCow" -> "mooshroom";
            case "SnowMan" -> "snow_golem";
            case "Ozelot" -> "ocelot";
            case "WitherBoss" -> "wither";
            case "EnderCrystal" -> "end_crystal";
            case "MinecartRideable" -> "minecart";
            case "ThrownPotion" -> "potion";
            case "ThrownEnderpearl" -> "ender_pearl";
            case "XPOrb" -> "experience_orb";
            case "XPBottle" -> "experience_bottle";
            case "PrimedTnt" -> "tnt";
            default -> legacy.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
        };
        return id.equals("minecraft:" + path);
    }

    public static Enchantment enchantment(String id) {
        id = AdvancementJson.id(id).toString();
        if (ENCHANTMENTS.containsKey(id)) return ENCHANTMENTS.get(id);
        if (!id.startsWith("minecraft:")) return null;
        return switch (id.substring(10)) {
            case "protection" -> Enchantment.protection;
            case "fire_protection" -> Enchantment.fireProtection;
            case "feather_falling" -> Enchantment.featherFalling;
            case "blast_protection" -> Enchantment.blastProtection;
            case "projectile_protection" -> Enchantment.projectileProtection;
            case "respiration" -> Enchantment.respiration;
            case "aqua_affinity" -> Enchantment.aquaAffinity;
            case "thorns" -> Enchantment.thorns;
            case "sharpness" -> Enchantment.sharpness;
            case "smite" -> Enchantment.smite;
            case "bane_of_arthropods" -> Enchantment.baneOfArthropods;
            case "knockback" -> Enchantment.knockback;
            case "fire_aspect" -> Enchantment.fireAspect;
            case "looting" -> Enchantment.looting;
            case "efficiency" -> Enchantment.efficiency;
            case "silk_touch" -> Enchantment.silkTouch;
            case "unbreaking" -> Enchantment.unbreaking;
            case "fortune" -> Enchantment.fortune;
            case "power" -> Enchantment.power;
            case "punch" -> Enchantment.punch;
            case "flame" -> Enchantment.flame;
            default -> null;
        };
    }

    public static Potion effect(String id) {
        id = AdvancementJson.id(id).toString();
        if (EFFECTS.containsKey(id)) return EFFECTS.get(id);
        if (!id.startsWith("minecraft:")) return null;
        int index = switch (id.substring(10)) {
            case "speed" -> 1;
            case "slowness" -> 2;
            case "haste" -> 3;
            case "mining_fatigue" -> 4;
            case "strength" -> 5;
            case "instant_health" -> 6;
            case "instant_damage" -> 7;
            case "jump_boost" -> 8;
            case "nausea" -> 9;
            case "regeneration" -> 10;
            case "resistance" -> 11;
            case "fire_resistance" -> 12;
            case "water_breathing" -> 13;
            case "invisibility" -> 14;
            case "blindness" -> 15;
            case "night_vision" -> 16;
            case "hunger" -> 17;
            case "weakness" -> 18;
            case "poison" -> 19;
            case "wither" -> 20;
            case "health_boost" -> 21;
            case "absorption" -> 22;
            case "saturation" -> 23;
            default -> -1;
        };
        return index < 0 ? null : Potion.potionTypes[index];
    }

    public static boolean biome(String id, BiomeGenBase biome) {
        if (id.startsWith("#")) return AdvancementTags.matches("worldgen/biome", id.substring(1), member -> biome(member, biome));
        id = AdvancementJson.id(id).toString();
        Predicate<BiomeGenBase> adapter = BIOMES.get(id);
        if (adapter != null) return adapter.test(biome);
        if (!id.startsWith("minecraft:")) return false;
        String legacy = switch (id.substring(10)) {
            case "nether_wastes" -> "Hell";
            case "the_end" -> "Sky";
            case "swamp" -> "Swampland";
            case "snowy_plains" -> "Ice Plains";
            case "windswept_hills" -> "Extreme Hills";
            default -> id.substring(10).replace("_", "");
        };
        return legacy.replace(" ", "").equalsIgnoreCase(biome.biomeName.replace(" ", ""));
    }
}
