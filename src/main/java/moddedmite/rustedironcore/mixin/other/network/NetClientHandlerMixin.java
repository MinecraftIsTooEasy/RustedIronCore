package moddedmite.rustedironcore.mixin.other.network;

import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import moddedmite.rustedironcore.network.PacketReader;
import moddedmite.rustedironcore.network.PacketSupplier;
import net.minecraft.Minecraft;
import net.minecraft.NetClientHandler;
import net.minecraft.Packet250CustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

@Mixin(NetClientHandler.class)
public class NetClientHandlerMixin {
    @Shadow
    public Minecraft mc;

    @Inject(method = "handleCustomPayload", at = @At("RETURN"))
    private void handleCustomPayload(Packet250CustomPayload par1Packet250CustomPayload, CallbackInfo ci) {
        PacketSupplier packetSupplier = PacketReader.clientReaders.get(par1Packet250CustomPayload.channel);
        if (packetSupplier != null) {
            Packet packet = packetSupplier.readPacket(PacketByteBuf.in(new DataInputStream(new ByteArrayInputStream(par1Packet250CustomPayload.data))));
            packet.apply(this.mc.thePlayer);
        }
    }
}
