package moddedmite.rustedironcore.mixin.other.server;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedOutEvent;
import moddedmite.rustedironcore.api.player.PlayerAPI;
import net.minecraft.DedicatedServer;
import net.minecraft.EntityPlayer;
import net.minecraft.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {
    @Inject(method = "playerLoggedIn", at = @At("RETURN"))
    private void onPlayerLoggedIn(ServerPlayer player, CallbackInfo ci) {
        System.out.println("RIC: dedicated server calling");
        boolean firstLogin = (((PlayerAPI) (EntityPlayer) player)).firstLogin();
        Handlers.PlayerEvent.onPlayerLoggedIn(new PlayerLoggedInEvent(player, firstLogin));
        if (firstLogin) {
            ((PlayerAPI) ((EntityPlayer) player)).setFirstLogin(false);
        }
    }

    @Inject(method = "playerLoggedOut", at = @At("RETURN"))
    private void onPlayerLoggedOut(ServerPlayer player, CallbackInfo ci) {
        Handlers.PlayerEvent.onPlayerLoggedOut(new PlayerLoggedOutEvent(player));
    }
}
