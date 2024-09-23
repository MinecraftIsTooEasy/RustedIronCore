package moddedmite.rustedironcore.api.event;

import moddedmite.rustedironcore.api.interfaces.IEnchantingListener;
import net.minecraft.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnchantingHandler {

    private static EnchantingHandler INSTANCE = new EnchantingHandler();

    public static EnchantingHandler getInstance() {
        return INSTANCE;
    }

    private List<IEnchantingListener> listeners = new ArrayList<>();

    public void registerEnchantingListener(IEnchantingListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public void unregisterEnchantListener(IEnchantingListener listener) {
        this.listeners.remove(listener);
    }

    public int onMaxEnchantNumModify(Random random, ItemStack item_stack, int enchantment_levels, int original) {
        for (IEnchantingListener listener : this.listeners) {
            original = listener.onMaxEnchantNumModify(random, item_stack, enchantment_levels, original);
        }
        return original;
    }
}
