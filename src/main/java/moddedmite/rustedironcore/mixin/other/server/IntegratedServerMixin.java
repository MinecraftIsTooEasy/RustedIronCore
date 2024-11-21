package moddedmite.rustedironcore.mixin.other.server;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {
    @Inject(method = "startServer", at = @At("RETURN"))
    private void onServerStarted(CallbackInfoReturnable<Boolean> cir) {
        Handlers.Initialization.onServerStarted(MinecraftServer.getServer());
    }
}
