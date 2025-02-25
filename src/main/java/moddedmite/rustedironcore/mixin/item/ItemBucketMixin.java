package moddedmite.rustedironcore.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import huix.glacier.api.extension.material.IBucketMaterial;
import moddedmite.rustedironcore.api.util.VesselUtil;
import moddedmite.rustedironcore.property.MaterialProperties;
import net.minecraft.ItemBucket;
import net.minecraft.ItemVessel;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBucket.class)
public abstract class ItemBucketMixin extends ItemVessel {

    public ItemBucketMixin(int id, Material vessel_material, Material contents_material, int standard_volume, int max_stack_size_empty, int max_stack_size_full, String texture) {
        super(id, vessel_material, contents_material, standard_volume, max_stack_size_empty, max_stack_size_full, texture);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void register(int id, Material material, Material contents, CallbackInfo ci) {
        VesselUtil.registerBucket(material, contents, (ItemBucket) (Object) this);
    }

    @ModifyExpressionValue(method = "onItemRightClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/ItemBucket;getPeerForContents(Lnet/minecraft/Material;)Lnet/minecraft/ItemVessel;"))
    private ItemVessel fixNPE(ItemVessel original) {
        if (original == null) return this;
        return original;
    }// if null, won't convert anything

    @Inject(method = "getPeer", at = @At("HEAD"), cancellable = true)
    private static void makeSafe(Material vessel_material, Material contents, CallbackInfoReturnable<ItemVessel> cir) {
        VesselUtil.getBucket(vessel_material, contents).ifPresent(cir::setReturnValue);
    }

    @Inject(method = "getChanceOfMeltingWhenFilledWithLava", at = @At("HEAD"), cancellable = true)
    private void modifyChance(CallbackInfoReturnable<Float> cir) {
        Material vesselMaterial = this.getVesselMaterial();
        if (vesselMaterial instanceof IBucketMaterial iBucketMaterial) {
            cir.setReturnValue(iBucketMaterial.getMeltingChance());
            return;
        }
        MaterialProperties.BucketMeltingChance.getOptional(vesselMaterial).ifPresent(cir::setReturnValue);
    }
}
