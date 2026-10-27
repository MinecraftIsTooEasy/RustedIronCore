package moddedmite.rustedironcore.mixin.network;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.sound.BlockSoundType;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import moddedmite.rustedironcore.network.PacketReader;
import moddedmite.rustedironcore.network.PacketSupplier;
import moddedmite.rustedironcore.internal.server.chat.TextComponentServerResolver;
import net.minecraft.ICommandSender;
import net.minecraft.INetworkManager;
import net.minecraft.NetServerHandler;
import net.minecraft.Packet3Chat;
import net.minecraft.Packet250CustomPayload;
import net.minecraft.ServerPlayer;
import net.minecraft.StepSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

@Mixin(NetServerHandler.class)
public class NetServerHandlerMixin {

    @Shadow
    public ServerPlayer playerEntity;

    @ModifyConstant(method = "handleChat", constant = @Constant(intValue = 100))
    private int extendChatMessageLength(int originalLimit) {
        return 256;
    }

    @WrapOperation(method = "sendPacketToPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/INetworkManager;addToSendQueue(Lnet/minecraft/Packet;)V"))
    private void resolveChatForRecipient(INetworkManager networkManager, net.minecraft.Packet packet, Operation<Void> original) {
        ICommandSender source = TextComponentServerResolver.getCommandSource();
        if (packet instanceof Packet3Chat chat && source != null) {
            original.call(networkManager, TextComponentServerResolver.resolvePacket(chat, source, this.playerEntity));
            return;
        }
        original.call(networkManager, packet);
    }

    @WrapOperation(method = "handleSimpleSignal(Lnet/minecraft/Packet85SimpleSignal;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/StepSound;getStepSound()Ljava/lang/String;"))
    private String useBlockHitSound(StepSound instance, Operation<String> original) {
        String originalSound = original.call(instance);
        return instance instanceof BlockSoundType soundType ? soundType.getHitSound() : originalSound;
    }

    @Inject(method = "handleCustomPayload", at = @At("RETURN"))
    private void handleCustomPayload(Packet250CustomPayload payload, CallbackInfo ci) {
        PacketSupplier packetSupplier = PacketReader.serverReaders.get(payload.channel);
        if (packetSupplier != null) {
            if (payload.data == null) {
                payload.data = new byte[0];
            }
            Packet packet = packetSupplier.readPacket(PacketByteBuf.in(new DataInputStream(new ByteArrayInputStream(payload.data))));
            packet.apply(this.playerEntity);
        }
    }
}
