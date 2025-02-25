package moddedmite.rustedironcore.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.BarbecueHandler;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin extends Entity {
    @Shadow
    public abstract ItemStack getEntityItem();

    @Shadow private float cooking_progress;

    @Shadow public abstract void setEntityItemStack(ItemStack par1ItemStack);

    public EntityItemMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "onCollideWithPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/EntityItem;playSound(Ljava/lang/String;FF)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void achievements(EntityPlayer par1EntityPlayer, CallbackInfo ci, boolean was_empty_handed_before, ItemStack var2, int var3) {
        Handlers.Achievement.onItemPickUp(par1EntityPlayer, var2);
    }

    @WrapOperation(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/ItemStack;canDouseFire()Z"))
    private boolean protectWaterBowl(ItemStack instance, Operation<Boolean> original) {
        BarbecueHandler barbecue = Handlers.Barbecue;
        if (barbecue.getCookResult(instance).isPresent() || barbecue.isCookResult(instance)) return false;
        return original.call(instance);
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/EntityDamageResult;startTrackingHealth(F)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void itfCookItemEntity(Damage damage, CallbackInfoReturnable<EntityDamageResult> cir, EntityDamageResult result) {
        ItemStack item_stack = this.getEntityItem();

        BarbecueHandler barbecue = Handlers.Barbecue;
        Optional<ItemStack> cookResult = barbecue.getCookResult(item_stack);
        if (damage.isFireDamage() && (cookResult.isPresent() || barbecue.isCookResult(item_stack))) {
            if (cookResult.isPresent()) {
                int x = this.getBlockPosX();
                int y = this.getBlockPosY();
                int z = this.getBlockPosZ();
                for (int dx = -1; dx <= 1; ++dx) {
                    for (int dz = -1; dz <= 1; ++dz) {
                        Block block = this.worldObj.getBlock(x + dx, y, z + dz);
                        if (block != Block.fire) continue;
                        this.worldObj.getAsWorldServer().addScheduledBlockOperation(EnumBlockOperation.try_extinguish_by_items, x + dx, y, z + dz, (this.worldObj.getTotalWorldTime() / 10L + 1L) * 10L, false);
                    }
                }
            }
            this.cooking_progress += damage.getAmount() * 3.0f;
            if (this.cooking_progress >= 100.0f) {
                if (cookResult.isEmpty()) {
                    this.setDead();
                    cir.setReturnValue(result.setEntityWasDestroyed());
                    return;
                }
                this.setEntityItemStack(cookResult.get());
            }
            cir.setReturnValue(result.setEntityWasAffected());
        }
    }

}
