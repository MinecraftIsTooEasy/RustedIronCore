package moddedmite.rustedironcore.mixin.other.item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.EntityLivingBase;
import net.minecraft.ItemArmor;
import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemArmor.class)
public class ItemArmorMixin {
    @ModifyReturnValue(method = "getProtectionAfterDamageFactor", at = @At("RETURN"))
    private float onArmorProtectionModify(float original, @Local(argsOnly = true) ItemStack item_stack, @Local(argsOnly = true) EntityLivingBase owner) {
        return Handlers.Combat.onArmorProtectionModify(item_stack, owner, original);
    }
}
