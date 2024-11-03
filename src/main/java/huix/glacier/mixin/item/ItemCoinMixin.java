package huix.glacier.mixin.item;

import huix.glacier.api.extension.material.ICoinMaterial;
import huix.glacier.api.extension.material.IComboMaterial;
import net.minecraft.Item;
import net.minecraft.ItemCoin;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( ItemCoin.class )
public class ItemCoinMixin extends Item {

    @Inject(method = "getExperienceValue",at = @At("HEAD"), cancellable = true)
    private void injectGetExperienceValue(CallbackInfoReturnable<Integer> cir){
        Material material = this.getExclusiveMaterial();
        if (material instanceof ICoinMaterial coinMaterial) {
            cir.setReturnValue(coinMaterial.getExperienceValue());
        }
    }

    @Inject(method = "getForMaterial",at = @At("HEAD"), cancellable = true)
    private static void injectGetForMaterial(Material material, CallbackInfoReturnable<ItemCoin> cir) {
        if (material instanceof ICoinMaterial coinMaterial) {
            cir.setReturnValue(coinMaterial.getForInstance());
        }
    }

    @Inject(method = "getNuggetPeer",at = @At(value = "HEAD"), cancellable = true)
    private void injectGetNuggetPeer(CallbackInfoReturnable<Item> cir){
        if (this.getExclusiveMaterial() instanceof ICoinMaterial coinMaterial) {
            cir.setReturnValue(coinMaterial.getNuggetPeer());
        }
    }

}
