package moddedmite.rustedironcore.mixin.other.item;

import moddedmite.rustedironcore.property.MaterialProperties;
import net.minecraft.ItemBucket;
import net.minecraft.ItemVessel;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBucket.class)
public abstract class ItemBucketMixin extends ItemVessel {

    public ItemBucketMixin(int id, Material vessel_material, Material contents_material, int standard_volume, int max_stack_size_empty, int max_stack_size_full, String texture) {
        super(id, vessel_material, contents_material, standard_volume, max_stack_size_empty, max_stack_size_full, texture);
    }

    @Inject(method = "getChanceOfMeltingWhenFilledWithLava", at = @At("HEAD"), cancellable = true)
    private void modifyChance(CallbackInfoReturnable<Float> cir) {
        MaterialProperties.BucketMeltingChance.getOptional(this.getVesselMaterial()).ifPresent(cir::setReturnValue);
    }
}
