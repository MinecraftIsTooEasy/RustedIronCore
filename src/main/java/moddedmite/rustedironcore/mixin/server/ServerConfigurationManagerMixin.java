package moddedmite.rustedironcore.mixin.server;

import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.PlayerCrossDimensionEvent;
import moddedmite.rustedironcore.api.event.events.PlayerRespawnEvent;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.ServerConfigurationManager;
import net.minecraft.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerConfigurationManager.class)
public class ServerConfigurationManagerMixin {
    @Inject(method = "respawnPlayer", at = @At("RETURN"))
    private void onPlayerRespawn(ServerPlayer par1EntityPlayerMP, int par2, boolean par3, CallbackInfoReturnable<ServerPlayer> cir) {
        Handlers.PlayerEvent.onPlayerRespawn(new PlayerRespawnEvent(par1EntityPlayerMP, Dimension.fromId(par2)));
    }

    @Inject(method = "transferPlayerToDimension", at = @At("RETURN"))
    private void onPlayerCrossDimension(ServerPlayer par1EntityPlayerMP, int to, CallbackInfo ci, @Local(ordinal = 1) int from) {
        Handlers.PlayerEvent.onPlayerCrossDimension(
                new PlayerCrossDimensionEvent(
                        par1EntityPlayerMP,
                        Dimension.fromId(from),
                        Dimension.fromId(to)
                )
        );
    }
}
