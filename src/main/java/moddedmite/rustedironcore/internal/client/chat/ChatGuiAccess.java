package moddedmite.rustedironcore.internal.client.chat;

import net.minecraft.ChatMessageComponent;

import javax.annotation.Nullable;

public interface ChatGuiAccess {

    void ric$printChatMessage(ChatMessageComponent component);

    @Nullable
    ChatInteractionHit ric$getInteractionAt(int mouseX, int mouseY);
}
