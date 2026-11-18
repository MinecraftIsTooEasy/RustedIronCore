package moddedmite.rustedironcore.internal.advancement;

import com.google.gson.JsonObject;
import net.minecraft.Entity;
import net.minecraft.EntityLivingBase;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AdvancementSlots {

    public static void validate(String name) {
        if (!name.matches("(?:weapon\\.(?:mainhand|offhand|\\*)|armor\\.(?:head|chest|legs|feet|\\*)|(?:hotbar|inventory|container)\\.(?:[0-9]+|\\*)|enderchest\\.(?:[0-9]+|\\*))"))
            throw new IllegalArgumentException("Unsupported slot range: " + name);
        String[] parts = name.split("\\.", 2);
        if (parts[1].matches("[0-9]+")) {
            int last = switch (parts[0]) { case "hotbar" -> 8; case "container" -> 53; default -> 26; };
            try {
                if (Integer.parseInt(parts[1]) > last) throw new IllegalArgumentException("Slot outside range: " + name);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Slot outside range: " + name);
            }
        }
    }

    public static boolean matches(String name, JsonObject predicate, Entity entity) {
        List<ItemStack> values = new ArrayList<>();
        if (entity instanceof EntityLivingBase living) {
            switch (name) {
                case "weapon.mainhand" -> values.add(living.getCurrentItemOrArmor(0));
                case "weapon.offhand" -> values.add(null);
                case "weapon.*" -> {
                    values.add(living.getCurrentItemOrArmor(0));
                    values.add(null);
                }
                case "armor.feet" -> values.add(living.getCurrentItemOrArmor(1));
                case "armor.legs" -> values.add(living.getCurrentItemOrArmor(2));
                case "armor.chest" -> values.add(living.getCurrentItemOrArmor(3));
                case "armor.head" -> values.add(living.getCurrentItemOrArmor(4));
                case "armor.*" -> { for (int i = 1; i <= 4; i++) values.add(living.getCurrentItemOrArmor(i)); }
            }
        }
        if (entity instanceof EntityPlayer player) {
            int dot = name.indexOf('.');
            String range = name.substring(0, dot), slot = name.substring(dot + 1);
            int start = range.equals("inventory") ? 9 : 0;
            int end = range.equals("hotbar") ? 9 : range.equals("enderchest") ? player.getInventoryEnderChest().getSizeInventory() : player.inventory.mainInventory.length;
            if (List.of("inventory", "hotbar", "container", "enderchest").contains(range)) for (int i = start; i < end; i++) {
                if (!slot.equals("*") && i != start + Integer.parseInt(slot)) continue;
                values.add(range.equals("enderchest") ? player.getInventoryEnderChest().getStackInSlot(i) : player.inventory.mainInventory[i]);
            }
        }
        return values.stream().anyMatch(stack -> AdvancementItemPredicates.matches(predicate, stack));
    }
}
