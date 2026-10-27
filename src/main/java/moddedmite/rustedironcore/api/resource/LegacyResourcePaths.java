package moddedmite.rustedironcore.api.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Legacy lookup candidates.  This class only describes names; callers that
 * render custom geometry must still validate the resource contract (including
 * image dimensions) through {@link LegacyTextureResources}.
 */
public final class LegacyResourcePaths {

    private static final Map<String, String> BLOCK_NAMES = Map.of(
            "pumpkin_face_off", "carved_pumpkin", "pumpkin_face_on", "jack_o_lantern");
    private static final Map<String, String> ITEM_NAMES = Map.of(
            "wood_shovel", "wooden_shovel", "gold_horse_armor", "golden_horse_armor");
    private static final List<List<String>> ENTITY_NAMES = List.of(
            List.of("bat", "bat/bat"),
            List.of("chicken", "chicken/chicken", "chicken/chicken_temperate"),
            List.of("cow/cow", "cow/cow_temperate"),
            List.of("cow/mooshroom", "cow/mooshroom_red", "cow/red_mooshroom"),
            List.of("pig/pig", "pig/pig_temperate"),
            List.of("pig/pig_saddle", "equipment/pig_saddle/saddle"),
            List.of("villager/villager", "villager/farmer", "villager/librarian", "villager/priest",
                    "villager/smith", "villager/butcher"),
            List.of("sheep/sheep_fur", "sheep/sheep_wool"),
            List.of("zombie_pigman", "piglin/zombified_piglin", "zombie/hill_pigman"));
    private static final List<String> ENTITY_ROOTS = List.of(
            "textures/entity/", "optifine/random/entity/", "optifine/mob/", "mcpatcher/mob/");

    /** Exact spelling wins inside one pack; aliases are tried inside that same pack. */
    public static List<String> candidates(String domain, String path) {
        List<String> result = new ArrayList<>();
        result.add(path);
        if (!path.endsWith(".png") && !path.endsWith(".properties") && !path.endsWith(".png.mcmeta")) return result;
        if (domain.equals("minecraft")) {
            for (String root : ENTITY_ROOTS) {
                if (!path.startsWith(root)) continue;
                String relative = path.substring(root.length());
                for (List<String> names : ENTITY_NAMES) {
                    for (String name : names) {
                        if (!relative.startsWith(name)) continue;
                        String suffix = relative.substring(name.length());
                        // Retain numbered variants, emissive/blink suffixes and metadata sidecars.
                        // In particular pig/pig must not claim pig/pig_saddle or pig/pig_cold.
                        if (!suffix.matches("(?:\\d+)?(?:_(?:e|glow|blink|blink2))*\\.(?:png(?:\\.mcmeta)?|properties)")) continue;
                        for (String alias : names) add(result, root + alias + suffix);
                        return result;
                    }
                }
            }
        }
        String metadata = path.endsWith(".mcmeta") ? ".mcmeta" : "";
        String image = metadata.isEmpty() ? path : path.substring(0, path.length() - metadata.length());
        if (!image.endsWith(".png")) return result;
        if (image.startsWith("textures/blocks/")) {
            String name = image.substring("textures/blocks/".length(), image.length() - 4);
            add(result, "textures/block/" + name + ".png" + metadata);
            if (BLOCK_NAMES.containsKey(name)) add(result, "textures/block/" + BLOCK_NAMES.get(name) + ".png" + metadata);
        } else if (image.startsWith("textures/items/")) {
            String name = image.substring("textures/items/".length(), image.length() - 4);
            add(result, "textures/item/" + name + ".png" + metadata);
            String renamed = ITEM_NAMES.get(name);
            if (name.startsWith("tools/")) {
                renamed = name.substring("tools/".length());
                if (renamed.startsWith("gold_")) renamed = "golden_" + renamed.substring(5);
                if (renamed.startsWith("wood_")) renamed = "wooden_" + renamed.substring(5);
            }
            if (renamed != null) add(result, "textures/item/" + renamed + ".png" + metadata);
        } else if (image.startsWith("textures/entity/horse/armor/horse_armor_")) {
            String material = image.substring("textures/entity/horse/armor/horse_armor_".length(), image.length() - 4);
            add(result, "textures/entity/equipment/horse_body/" + material + ".png" + metadata);
        }
        return result;
    }

    /**
     * Adds modern armor texture paths only for callers that validate an authored
     * model texture-size contract. Vanilla MITE armor renderers keep using their
     * exact 64x32 texture unless a custom model explicitly requires another size.
     */
    public static List<String> textureContractCandidates(String domain, String path) {
        List<String> result = new ArrayList<>(candidates(domain, path));
        if (!domain.equals("minecraft")) return result;
        String metadata = path.endsWith(".mcmeta") ? ".mcmeta" : "";
        String image = metadata.isEmpty() ? path : path.substring(0, path.length() - metadata.length());
        if (image.startsWith("textures/models/armor/")) addArmorAliases(result, image, metadata);
        return result;
    }

    private static void addArmorAliases(List<String> result, String image, String metadata) {
        String fileName = image.substring("textures/models/armor/".length());
        String[] suffixes = {
                "_layer_1_overlay.png", "_layer_2_overlay.png", "_layer_1.png", "_layer_2.png"
        };
        for (String suffix : suffixes) {
            if (!fileName.endsWith(suffix)) continue;
            boolean leggings = suffix.contains("_layer_2");
            boolean overlay = suffix.contains("_overlay");
            String material = fileName.substring(0, fileName.length() - suffix.length());
            String equipmentType = leggings ? "humanoid_leggings" : "humanoid";
            String textureName = material + (overlay ? "_overlay" : "") + ".png";
            add(result, "textures/entity/equipment/" + equipmentType + "/" + textureName + metadata);
            return;
        }
    }

    public static String find(String domain, String path, Predicate<String> exists) {
        for (String candidate : candidates(domain, path)) if (exists.test(candidate)) return candidate;
        return null;
    }

    private static void add(List<String> result, String path) {
        if (!result.contains(path)) result.add(path);
    }
}
