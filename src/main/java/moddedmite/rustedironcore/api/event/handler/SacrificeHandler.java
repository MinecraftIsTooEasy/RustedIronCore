package moddedmite.rustedironcore.api.event.handler;

import net.minecraft.ItemStack;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/** Rules for items that may be converted into player experience. */
public final class SacrificeHandler {

    private final CopyOnWriteArrayList<Predicate<ItemStack>> blockedRules = new CopyOnWriteArrayList<>();

    /**
     * Registers a rule that blocks experience conversion when it returns true.
     */
    public void registerBlockedRule(Predicate<ItemStack> rule) {
        this.blockedRules.add(Objects.requireNonNull(rule, "rule"));
    }

    /**
     * Blocks conversion for stacks carrying a true boolean NBT value at the given key.
     */
    public void registerNonSacrificialTag(String key) {
        Objects.requireNonNull(key, "key");
        registerBlockedRule(stack -> stack.hasTagCompound()
                && stack.getTagCompound().getBoolean(key));
    }

    public boolean canSacrifice(ItemStack stack) {
        for (Predicate<ItemStack> rule : this.blockedRules) {
            if (rule.test(stack)) return false;
        }
        return true;
    }
}
