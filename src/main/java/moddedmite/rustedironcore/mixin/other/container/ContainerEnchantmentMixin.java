package moddedmite.rustedironcore.mixin.other.container;

import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.Container;
import net.minecraft.ContainerEnchantment;
import net.minecraft.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ContainerEnchantment.class)
public abstract class ContainerEnchantmentMixin extends Container {
    public ContainerEnchantmentMixin(EntityPlayer player) {
        super(player);
    }

    @ModifyArg(method = "calcEnchantmentLevelsForSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/EnchantmentHelper;getEnchantmentLevelsAlteredByItemEnchantability(ILnet/minecraft/Item;)I"))
    private int modifyEnchantLevel(int enchantment_levels, @Local(argsOnly = true, ordinal = 0) int slot_index) {
        return Handlers.Enchanting.onEnchantLevelModify((ContainerEnchantment) (Object) this, slot_index, enchantment_levels);
    }
}
