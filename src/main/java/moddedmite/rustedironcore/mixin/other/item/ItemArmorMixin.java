package moddedmite.rustedironcore.mixin.other.item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import huix.glacier.api.extension.material.IArmorMaterial;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.EntityLivingBase;
import net.minecraft.ItemArmor;
import net.minecraft.ItemStack;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemArmor.class)
public class ItemArmorMixin {
    @ModifyReturnValue(method = "getProtectionAfterDamageFactor", at = @At("RETURN"))
    private float onArmorProtectionModify(float original, @Local(argsOnly = true) ItemStack item_stack, @Local(argsOnly = true) EntityLivingBase owner) {
        return Handlers.Combat.onArmorProtectionModify(item_stack, owner, original);
    }

    @Shadow
    protected Material effective_material;

    @Shadow
    @Final
    private boolean is_chain_mail;

    @Inject(method = "getMaterialProtection", at = @At("HEAD"), cancellable = true)
    public void registerMaterialProtection(CallbackInfoReturnable<Integer> cir) {
        if (this.effective_material instanceof IArmorMaterial armorMaterial) {
            int protection = armorMaterial.getProtection();
            if (this.is_chain_mail) protection -= armorMaterial.getLossOfChainMail();
            cir.setReturnValue(protection);
        }
    }
}
