package moddedmite.rustedironcore.internal.advancement;

import net.minecraft.Item;
import net.minecraft.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class AdvancementPotions {

    private static final Map<String, Predicate<ItemStack>> ADAPTERS = new HashMap<>();

    public static void register(String id, Predicate<ItemStack> predicate) {
        ADAPTERS.put(AdvancementJson.id(id).toString(), predicate);
    }

    public static boolean matches(String id, ItemStack stack) {
        if (stack == null) return false;
        id = AdvancementJson.id(id).toString();
        Predicate<ItemStack> adapter = ADAPTERS.get(id);
        if (adapter != null) return adapter.test(stack);
        if (stack.getItem() != Item.potion || !id.startsWith("minecraft:")) return false;
        int data = stack.getItemSubtype() & 127;
        String base = switch (data & 15) {
            case 1 -> "regeneration";
            case 2 -> "swiftness";
            case 3 -> "fire_resistance";
            case 4 -> "poison";
            case 5 -> "healing";
            case 6 -> "night_vision";
            case 8 -> "weakness";
            case 9 -> "strength";
            case 10 -> "slowness";
            case 12 -> "harming";
            case 13 -> "water_breathing";
            case 14 -> "invisibility";
            default -> null;
        };
        if (base == null) return id.equals("minecraft:" + switch (data) {
            case 0 -> "water";
            case 16 -> "awkward";
            case 32 -> "thick";
            default -> "mundane";
        });
        return id.equals("minecraft:" + ((data & 32) != 0 ? "strong_" : (data & 64) != 0 ? "long_" : "") + base);
    }
}
