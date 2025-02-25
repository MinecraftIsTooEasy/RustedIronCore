package moddedmite.rustedironcore.mixin.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.CommandRegisterEvent;
import net.minecraft.CommandHandler;
import net.minecraft.IAdminCommand;
import net.minecraft.ServerCommandManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerCommandManager.class)
public abstract class ServerCommandManagerMixin extends CommandHandler implements IAdminCommand {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/CommandBase;setAdminCommander(Lnet/minecraft/IAdminCommand;)V"))
    private void onCommandRegister(IAdminCommand iAdminCommand, Operation<Void> original) {
        Handlers.Command.publish(new CommandRegisterEvent(this::registerCommand));
        original.call(iAdminCommand);
    }
}
