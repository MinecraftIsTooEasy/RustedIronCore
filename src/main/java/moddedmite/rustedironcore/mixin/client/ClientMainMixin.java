package moddedmite.rustedironcore.mixin.client;

import moddedmite.rustedironcore.internal.event.Hooks;
import net.fabricmc.api.EnvType;
import net.minecraft.client.main.Main;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Main.class, priority = 0)
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
    private static void preEntrypoints(String[] par0ArrayOfStr, CallbackInfo ci) {
        Hooks.preEntrypoints(EnvType.CLIENT);
    }

    @Inject(
            method = "main",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/Session;IIZZLjava/io/File;Ljava/io/File;Ljava/io/File;Ljava/net/Proxy;Ljava/lang/String;)Lnet/minecraft/Minecraft;"
            )
    )
    private static void postEntrypoints(String[] par0ArrayOfStr, CallbackInfo ci) {
        Hooks.postEntrypoints(EnvType.CLIENT);
    }
}
