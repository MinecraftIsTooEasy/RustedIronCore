package moddedmite.rustedironcore.mixin.client.gui;

import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiConnecting.class)
public class GuiConnectingMixin {
    @Shadow
    private NetClientHandler clientHandler;

    @Inject(method = "<init>(Lnet/minecraft/GuiScreen;Lnet/minecraft/Minecraft;Lnet/minecraft/ServerData;)V", at = @At("RETURN"))
    private void connectionOpened(GuiScreen par1GuiScreen, Minecraft par2Minecraft, ServerData par3ServerData, CallbackInfo ci, @Local ServerAddress var4) {
        Handlers.Connection.onClientConnection(this.clientHandler, par3ServerData.serverName, var4.getPort());
    }
}
