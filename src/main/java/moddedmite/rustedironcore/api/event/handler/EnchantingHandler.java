package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IEnchantingListener;
import net.minecraft.ItemStack;

import java.util.Random;

public class EnchantingHandler extends AbstractHandler<IEnchantingListener> {
    public int onMaxEnchantNumModify(Random random, ItemStack item_stack, int enchantment_levels, int original) {
        for (IEnchantingListener listener : this.listeners) {
            original = listener.onMaxEnchantNumModify(random, item_stack, enchantment_levels, original);
        }
        return original;
    }
}
