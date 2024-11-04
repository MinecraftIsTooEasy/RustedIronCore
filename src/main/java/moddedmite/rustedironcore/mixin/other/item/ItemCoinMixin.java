package moddedmite.rustedironcore.mixin.other.item;

import huix.glacier.api.extension.material.ICoinMaterial;
import moddedmite.rustedironcore.api.util.ItemUtil;
import moddedmite.rustedironcore.property.MaterialProperties;
import net.minecraft.Item;
import net.minecraft.ItemCoin;
import net.minecraft.ItemNugget;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemCoin.class)
public class ItemCoinMixin extends Item {
    @Inject(method = "getExperienceValue", at = @At("HEAD"), cancellable = true)
    private void inject(CallbackInfoReturnable<Integer> cir) {
        Material material = this.getExclusiveMaterial();
        if (material instanceof ICoinMaterial coinMaterial) {
            cir.setReturnValue(coinMaterial.getExperienceValue());
        }
        MaterialProperties.PeerCoinXP.getOptional(this.getExclusiveMaterial()).ifPresent(cir::setReturnValue);
    }

    @Inject(method = "getForMaterial", at = @At("HEAD"), cancellable = true)
    private static void inject(Material material, CallbackInfoReturnable<ItemCoin> cir) {
        if (material instanceof ICoinMaterial coinMaterial) {
            cir.setReturnValue(coinMaterial.getForInstance());
        }
        MaterialProperties.PeerCoin.getOptional(material).ifPresent(cir::setReturnValue);
    }

    @Inject(method = "getNuggetPeer", at = @At("HEAD"), cancellable = true)
    private void inject_1(CallbackInfoReturnable<Item> cir) {
        ItemNugget nuggetForMaterial = ItemUtil.getNuggetForMaterial(this.getExclusiveMaterial());
        if (nuggetForMaterial != null) {
            cir.setReturnValue(nuggetForMaterial);
        }
        if (this.getExclusiveMaterial() instanceof ICoinMaterial coinMaterial) {
            cir.setReturnValue(coinMaterial.getNuggetPeer());
        }
    }
}
