package moddedmite.rustedironcore.api.interfaces;

import net.minecraft.ItemStack;

import java.util.Random;

public interface IEnchantingListener {
    default int onMaxEnchantNumModify(Random random, ItemStack item_stack, int enchantment_levels, int original) {
        return original;
    }
}
