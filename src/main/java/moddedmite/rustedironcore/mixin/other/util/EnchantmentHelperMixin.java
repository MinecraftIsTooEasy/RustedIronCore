package moddedmite.rustedironcore.mixin.other.util;

import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.EnchantmentHelper;
import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.Random;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyConstant(method = "buildEnchantmentList", constant = @Constant(intValue = 2, ordinal = 0))
    private static int enchantSizeModify1(int constant, @Local(argsOnly = true) Random random, @Local(argsOnly = true) ItemStack item_stack, @Local(argsOnly = true) int enchantment_levels) {
        return Handlers.Enchanting.onMaxEnchantNumModify(random, item_stack, enchantment_levels, constant);
    }

    @ModifyConstant(method = "buildEnchantmentList", constant = @Constant(intValue = 2, ordinal = 2))
    private static int enchantSizeModify2(int constant, @Local(argsOnly = true) Random random, @Local(argsOnly = true) ItemStack item_stack, @Local(argsOnly = true) int enchantment_levels) {
        return Handlers.Enchanting.onMaxEnchantNumModify(random, item_stack, enchantment_levels, constant);
    }
}
