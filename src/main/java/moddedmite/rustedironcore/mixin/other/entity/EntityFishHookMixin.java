package moddedmite.rustedironcore.mixin.other.entity;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityFishHook.class)
public abstract class EntityFishHookMixin extends Entity {
    public EntityFishHookMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "getFishType", at = @At("HEAD"), cancellable = true)
    private void redirect(CallbackInfoReturnable<Item> cir) {
        ItemStack fishingResult = Handlers.LootTable.getFishingResult(this.rand);
        if (fishingResult.itemID == Item.fishRaw.itemID) return;
        cir.setReturnValue(fishingResult.getItem());
    }
}
