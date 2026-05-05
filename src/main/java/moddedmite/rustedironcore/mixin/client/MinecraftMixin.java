package moddedmite.rustedironcore.mixin.client;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.gui.GuiTips;
import moddedmite.rustedironcore.api.gui.GuiTipsWindow;
import moddedmite.rustedironcore.api.model.JsonBlockModelManager;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public WorldClient theWorld;

    @Shadow
    public abstract NetClientHandler getNetHandler();

    @Shadow
    public abstract ResourceManager getResourceManager();

    @Shadow
    private IntegratedServer theIntegratedServer;
    @Unique
    public GuiTipsWindow guiTipsWindow;
    @Unique
    public GuiTips guiTips;

    @Inject(method = "startGame", at = @At("RETURN"))
    private void addGui(CallbackInfo ci) {
        Handlers.Initialization.onClientStarted(Minecraft.getMinecraft());
        ((ReloadableResourceManager) this.getResourceManager()).registerReloadListener(JsonBlockModelManager.INSTANCE);
        this.guiTipsWindow = new GuiTipsWindow((Minecraft) (Object) this);
        this.guiTips = new GuiTips();
    }

    @Inject(method = "runTick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        Handlers.Tick.onClientTick(Minecraft.getMinecraft());
    }

    @Inject(method = "launchIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/LoadingScreenRenderer;displayProgressMessage(Ljava/lang/String;)V"))
    private void onIntegratedServerLaunch(String par1Str, String par2Str, WorldSettings par3WorldSettings, CallbackInfo ci) {
        Handlers.Connection.onIntegratedConnection(this.getNetHandler(), this.theIntegratedServer);
    }

    @Inject(method = "loadWorld(Lnet/minecraft/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void onWorldLoadStart(WorldClient par1WorldClient, String par2Str, CallbackInfo ci) {
        if (this.theWorld != null) {
            Handlers.WorldLoad.onWorldUnload(this.theWorld);
        }
    }

    @Inject(method = "loadWorld(Lnet/minecraft/WorldClient;Ljava/lang/String;)V", at = @At("RETURN"))
    private void onWorldLoadEnd(WorldClient par1WorldClient, String par2Str, CallbackInfo ci) {
        if (par1WorldClient != null) {
            Handlers.WorldLoad.onWorldLoad(par1WorldClient);
        }
    }
}
