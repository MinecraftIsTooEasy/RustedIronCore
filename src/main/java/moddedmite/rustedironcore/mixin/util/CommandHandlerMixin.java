package moddedmite.rustedironcore.mixin.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.internal.server.chat.TextComponentServerResolver;
import net.minecraft.CommandHandler;
import net.minecraft.ICommand;
import net.minecraft.ICommandSender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CommandHandler.class)
public class CommandHandlerMixin {

    @WrapOperation(method = "executeCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/ICommand;processCommand(Lnet/minecraft/ICommandSender;[Ljava/lang/String;)V"))
    private void retainCommandSource(ICommand command, ICommandSender sender, String[] arguments, Operation<Void> original) {
        TextComponentServerResolver.withCommandSource(sender, () -> original.call(command, sender, arguments));
    }
}
