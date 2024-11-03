package huix.glacier.mixin.entity;

import huix.glacier.api.registry.sync.RegistryRemapperAccess;
import huix.glacier.api.registry.sync.ServerRegistryRemapper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.*;
import net.minecraft.server.MinecraftServer;
import net.xiaoyu233.fml.FishModLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( ServerConfigurationManager.class )
public class ServerConfigurationManagerMixin {

    @Shadow
    @Final
    private MinecraftServer mcServer;

    @Inject(at = @At(value = "INVOKE", target ="Lnet/minecraft/NetServerHandler;sendPacketToPlayer(Lnet/minecraft/Packet;)V", ordinal = 2), method = "initializeConnectionToPlayer")
    public void playerConnect(INetworkManager par1INetworkManager, ServerPlayer par2EntityPlayerMP, CallbackInfo ci) {
        if (fabric_shouldSend()) {
            par2EntityPlayerMP.sendPacket(new Packet250CustomPayload(RegistryRemapperAccess.PACKET_ID.getNamespace(), ServerRegistryRemapper.getInstance().createBuf()));
        }
    }

    @Unique
    private boolean fabric_shouldSend() {
        boolean published = false;

        if (FishModLoader.getEnvironmentType() == EnvType.CLIENT) {
            published = fabric_isPublished();
        }

        return this.mcServer.isDedicatedServer() || published;
    }

    @Environment(EnvType.CLIENT)
    @Unique
    private boolean fabric_isPublished() {
        return ((IntegratedServer) this.mcServer).getPublic();
    }
}
