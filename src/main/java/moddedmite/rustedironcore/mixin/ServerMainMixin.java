package moddedmite.rustedironcore.mixin;

import moddedmite.rustedironcore.internal.event.Hooks;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = 0)// expect: invoke before fml entrypoints
public class ServerMainMixin {
    @Inject(method = "main", at = @At("HEAD"))
    private static void onServerLaunch(String[] par0ArrayOfStr, CallbackInfo ci) {
        Hooks.preServerLaunch();
    }
}
