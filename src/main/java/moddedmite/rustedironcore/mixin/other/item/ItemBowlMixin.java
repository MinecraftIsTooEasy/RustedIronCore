package moddedmite.rustedironcore.mixin.other.item;

import moddedmite.rustedironcore.api.util.VesselUtil;
import net.minecraft.ItemBowl;
import net.minecraft.ItemVessel;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBowl.class)
public class ItemBowlMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int id, Material contents, String texture, CallbackInfo ci) {
        if (isExactClass(this, ItemBowl.class)) {
            VesselUtil.registerBowl((ItemBowl) (Object) this);
        }
    }

    @Inject(method = "getPeer", at = @At("HEAD"), cancellable = true)
    private static void makeSafe(Material vessel_material, Material contents, CallbackInfoReturnable<ItemVessel> cir) {
        VesselUtil.getBowl(vessel_material, contents).ifPresent(cir::setReturnValue);
    }

    @Unique
    private static boolean isExactClass(Object obj, Class<?> clazz) {
        return obj.getClass() == clazz;
    }
}
