package moddedmite.rustedironcore.mixin.client.chat;

import moddedmite.rustedironcore.internal.chat.ChatClickSupport;
import moddedmite.rustedironcore.internal.client.chat.ChatGuiAccess;
import net.minecraft.AbstractClientPlayer;
import net.minecraft.ChatMessageComponent;
import net.minecraft.ClientPlayer;
import net.minecraft.Minecraft;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayer.class)
public abstract class ClientPlayerMixin extends AbstractClientPlayer {

    public ClientPlayerMixin(World world, String username) {
        super(world, username);
    }

    @Inject(method = "sendChatToPlayer", at = @At("HEAD"), cancellable = true)
    private void printComponentWithChatInteraction(ChatMessageComponent component, CallbackInfo ci) {
        if (!ChatClickSupport.hasChatInteraction(component) && !ChatClickSupport.hasTextComponent(component)) {
            return;
        }
        ((ChatGuiAccess) Minecraft.getMinecraft().ingameGUI.getChatGUI())
                .ric$printChatMessage(component);
        ci.cancel();
    }
}
