package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemBucket;
import net.minecraft.ItemStack;
import net.minecraft.Material;
import net.minecraft.RaycastCollision;
import net.minecraft.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBucket.class)
public class ItemBucketMixin {

    @Inject(method = "onItemRightClick", at = @At("HEAD"))
    private void captureBucketContents(EntityPlayer player, float partialTick, boolean control, CallbackInfoReturnable<Boolean> callback, @Share("ric$filledMaterial") LocalRef<Material> contents) {
        if (!(player instanceof ServerPlayer) || !player.inCreativeMode() || !((ItemBucket) (Object) this).isEmpty()) return;
        RaycastCollision hit = player.getSelectedObject(partialTick, true);
        if (hit == null || !hit.isBlock()) return;
        Material material = hit.getBlockHitMaterial().isLiquid() ? hit.getBlockHitMaterial() : hit.getNeighborOfBlockHitMaterial();
        if (material != null && material.isLiquid()) contents.set(material);
    }

    @Inject(method = "onItemRightClick", at = @At("RETURN"))
    private void trackFilledBucket(EntityPlayer player, float partialTick, boolean control, CallbackInfoReturnable<Boolean> callback, @Share("ric$filledMaterial") LocalRef<Material> contents) {
        if (callback.getReturnValue() && contents.get() != null) CriteriaTriggers.filledBucket(player, new ItemStack(((ItemBucket) (Object) this).getPeerForContents(contents.get())));
    }

    @WrapOperation(method = "onItemRightClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/EntityPlayer;convertOneOfHeldItem(Lnet/minecraft/ItemStack;)V"))
    private void trackProducedBucket(EntityPlayer player, ItemStack result, Operation<Void> original) {
        original.call(player, result);
        if (((ItemBucket) (Object) this).isEmpty() && result != null && result.getItem() instanceof ItemBucket bucket
                && bucket.getContents() != null && bucket.getContents().isLiquid()) CriteriaTriggers.filledBucket(player, result);
    }
}
