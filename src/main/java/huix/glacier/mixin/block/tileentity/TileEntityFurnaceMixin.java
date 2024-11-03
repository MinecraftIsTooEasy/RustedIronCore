package huix.glacier.mixin.block.tileentity;

import huix.glacier.api.registry.MinecraftRegistry;
import net.minecraft.TileEntityFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( TileEntityFurnace.class )
public class TileEntityFurnaceMixin {

    @Inject(method = "getHeatLevelRequired", at = @At("HEAD"), cancellable = true)
    private static void getHeatLevelRequired(int item_id, CallbackInfoReturnable<Integer> cir) {
        if (MinecraftRegistry.instance != null && MinecraftRegistry.instance.itemHeatLevelMap.containsKey(item_id)) {
            cir.setReturnValue(MinecraftRegistry.instance.itemHeatLevelMap.get(item_id));
        }
    }
}
