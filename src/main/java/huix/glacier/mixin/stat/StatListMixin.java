package huix.glacier.mixin.stat;

import huix.glacier.api.entrypoint.IGameRegistry;
import net.minecraft.StatList;
import net.xiaoyu233.fml.FishModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( StatList.class )
public class StatListMixin {

    @Inject(method = "initStats", at = @At("RETURN"))
    private static void injectGameRegistry(CallbackInfo ci) {
        FishModLoader.invokeEntrypoints("registry", IGameRegistry.class, IGameRegistry::onGameRegistry);
    }
}
