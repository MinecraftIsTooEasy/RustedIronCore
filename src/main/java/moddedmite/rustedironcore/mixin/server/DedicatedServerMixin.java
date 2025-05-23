package moddedmite.rustedironcore.mixin.server;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedOutEvent;
import moddedmite.rustedironcore.api.player.PlayerAPI;
import net.minecraft.DedicatedServer;
import net.minecraft.EntityPlayer;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {
    @Inject(method = "playerLoggedIn", at = @At("RETURN"))
    private void onPlayerLoggedIn(ServerPlayer player, CallbackInfo ci) {
        boolean firstLogin = (((PlayerAPI) (EntityPlayer) player)).ric$IsFirstLogin();
        Handlers.PlayerEvent.onPlayerLoggedIn(new PlayerLoggedInEvent(player, firstLogin));
        if (firstLogin) {
            ((PlayerAPI) ((EntityPlayer) player)).ric$SetFirstLogin(false);
        }
    }

    @Inject(method = "playerLoggedOut", at = @At("RETURN"))
    private void onPlayerLoggedOut(ServerPlayer player, CallbackInfo ci) {
        Handlers.PlayerEvent.onPlayerLoggedOut(new PlayerLoggedOutEvent(player));
    }

    @Inject(method = "startServer", at = @At(value = "RETURN", ordinal = 1))
    private void onServerStarted(CallbackInfoReturnable<Boolean> cir) {
        Handlers.Initialization.onServerStarted(MinecraftServer.getServer());
    }
}
