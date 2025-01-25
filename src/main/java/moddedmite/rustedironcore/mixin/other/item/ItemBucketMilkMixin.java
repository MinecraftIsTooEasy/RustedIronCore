package moddedmite.rustedironcore.mixin.other.item;

import moddedmite.rustedironcore.api.util.VesselUtil;
import net.minecraft.ItemBucket;
import net.minecraft.ItemBucketMilk;
import net.minecraft.ItemVessel;
import net.minecraft.Material;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemBucketMilk.class)
public abstract class ItemBucketMilkMixin extends ItemVessel {
    public ItemBucketMilkMixin(int id, Material vessel_material, Material contents_material, int standard_volume, int max_stack_size_empty, int max_stack_size_full, String texture) {
        super(id, vessel_material, contents_material, standard_volume, max_stack_size_empty, max_stack_size_full, texture);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void register(int id, Material material, CallbackInfo ci) {
        VesselUtil.registerBucket(material, Material.milk, ReflectHelper.dyCast(this));
    }
}
