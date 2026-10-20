package moddedmite.rustedironcore.world.item.crafting;

import net.minecraft.ItemStack;

public interface RecipeInput {
    ItemStack getItem(int index);

    int size();

    default boolean isEmpty() {
        for (int i = 0; i < this.size(); i++) {
            if (this.getItem(i) != null) {
                return false;
            }
        }

        return true;
    }
}
