package moddedmite.rustedironcore.mixin.inventory;

import moddedmite.rustedironcore.mixins.interfaces.RicInventoryCraftingImpl;
import net.minecraft.Container;
import net.minecraft.InventoryCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryCrafting.class)
public class InventoryCraftingMixin implements RicInventoryCraftingImpl {
    @Unique
    private int ric$width;
    @Unique
    private int ric$height;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(Container par1Container, int width, int height, CallbackInfo ci) {
        this.ric$width = width;
        this.ric$height = height;
    }

    @Override
    public int ric$getWidth() {
        return this.ric$width;
    }

    @Override
    public int ric$getHeight() {
        return this.ric$height;
    }
}
