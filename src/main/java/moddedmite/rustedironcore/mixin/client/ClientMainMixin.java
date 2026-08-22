package moddedmite.rustedironcore.mixin.client;

import moddedmite.rustedironcore.internal.event.Hooks;
import net.minecraft.client.main.Main;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Main.class, priority = 0)// expect: invoke before fml entrypoints
public class ClientMainMixin {
    @Inject(
            method = "main",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/main/Main;is_MITE_DS:Z",
                    opcode = Opcodes.GETSTATIC,
                    ordinal = 0
            )
    )
    private static void onClientLaunch(String[] par0ArrayOfStr, CallbackInfo ci) {
        Hooks.preClientLaunch();
    }
}
