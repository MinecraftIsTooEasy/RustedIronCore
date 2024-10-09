package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.ContainerEnchantment;
import net.minecraft.ItemStack;

import java.util.Random;

public interface IEnchantingListener {
    default int onMaxEnchantNumModify(Random random, ItemStack item_stack, int enchantment_levels, int original) {
        return original;
    }

    default int onEnchantLevelModify(ContainerEnchantment containerEnchantment, int slot_index, int original) {
        return original;
    }
}
