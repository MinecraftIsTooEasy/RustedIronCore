package moddedmite.rustedironcore.mixin.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.internal.Bootstrap;
import net.minecraft.StatList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StatList.class)
public class StatListMixin {
    @WrapOperation(
            method = {"initBreakableStats", "initStats"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/StatList;initCraftableStats()V")
    )
    private static void postpone(Operation<Void> original) {
        if (Bootstrap.preloadGameObjects()) return;
        original.call();
    }
}
