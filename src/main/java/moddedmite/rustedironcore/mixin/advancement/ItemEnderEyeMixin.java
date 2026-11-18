package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import net.minecraft.EntityEnderEye;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemEnderEye;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEnderEye.class)
public class ItemEnderEyeMixin {

    @WrapOperation(method = "onItemRightClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/EntityEnderEye;moveTowards(DID)V"))
    private void trackEnderEye(EntityEnderEye eye, double x, int y, double z, Operation<Void> original, @Local(argsOnly = true) EntityPlayer player) {
        original.call(eye, x, y, z);
        CriteriaTriggers.usedEnderEye(player, x, z);
    }
}
