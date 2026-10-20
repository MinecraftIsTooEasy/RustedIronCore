package moddedmite.rustedironcore.api.util;

import net.minecraft.Item;
import net.minecraft.ItemNugget;
import net.minecraft.ItemStack;
import net.minecraft.Material;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ItemUtil {
    public static ItemNugget getNuggetForMaterial(Material material) {
        return Item.adamantiumNugget.getForMaterial(material);
    }

    @SuppressWarnings("ConstantValue")
    public static boolean matches(final ItemStack a, final ItemStack b) {
        if (a == null && b != null) return false;
        if (a != null && b == null) return false;
        if (a == null && b == null) return true;

        if (a == b) {
            return true;
        } else {
            return a.stackSize == b.stackSize && isSameItemSameComponents(a, b);
        }
    }

    public static boolean listMatches(final List<ItemStack> left, final List<ItemStack> right) {
        if (left.size() != right.size()) {
            return false;
        }

        for (int i = 0; i < left.size(); i++) {
            if (!matches(left.get(i), right.get(i))) {
                return false;
            }
        }

        return true;
    }

    @SuppressWarnings("ConstantValue")
    public static boolean isSameItem(final ItemStack a, final ItemStack b) {
        if (a == null && b != null) return false;
        if (a != null && b == null) return false;
        if (a == null && b == null) return true;

        return a.getItem() == b.getItem();
    }

    public static boolean isSameItemSameComponents(final ItemStack a, final ItemStack b) {
        if (!isSameItem(a, b)) {
            return false;
        } else {
            if (a == null) return true;// simplified

            return a.getItemSubtype() == b.getItemSubtype();
        }
    }

    public static int hashStackList(final List<ItemStack> items) {
        int result = 0;

        for (ItemStack item : items) {
            result = result * 31 + hashItemAndComponents(item);
        }

        return result;
    }

    public static int hashItemAndComponents(final @Nullable ItemStack item) {
        if (item != null) {
            int result = 31 + item.getItem().hashCode();
            return 31 * result + item.getItemSubtype();
        } else {
            return 0;
        }
    }
}
