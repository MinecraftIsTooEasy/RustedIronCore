package moddedmite.rustedironcore.mixin;

import moddedmite.rustedironcore.internal.event.Hooks;
import net.fabricmc.api.EnvType;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = 0)
public class ServerMainMixin {
    @Inject(method = "main", at = @At("HEAD"))
    private static void preEntrypoints(String[] par0ArrayOfStr, CallbackInfo ci) {
        Hooks.preEntrypoints(EnvType.SERVER);
    }

    @Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/StatList;nopInit()V"))
    private static void postEntrypoints(String[] par0ArrayOfStr, CallbackInfo ci) {
        Hooks.postEntrypoints(EnvType.SERVER);
    }
}
