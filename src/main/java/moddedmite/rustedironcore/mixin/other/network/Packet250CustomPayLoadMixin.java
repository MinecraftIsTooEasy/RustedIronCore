package moddedmite.rustedironcore.mixin.other.network;

import net.minecraft.Packet250CustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Packet250CustomPayload.class)
public class Packet250CustomPayLoadMixin {
    @ModifyArg(method = "readPacketData", at = @At(value = "INVOKE", target = "Lnet/minecraft/Packet250CustomPayload;readString(Ljava/io/DataInput;I)Ljava/lang/String;"), index = 1)
    private int fix(int par2) {
        return 32767;
    }
}
