package moddedmite.rustedironcore.internal.client.chat;

import net.minecraft.ChatMessageComponent;

import javax.annotation.Nullable;
import java.util.List;

public interface ChatLineAccess {

    void ric$setInteractionSpans(List<ChatInteractionSpan> spans);

    void ric$setSourceComponent(@Nullable ChatMessageComponent component);

    @Nullable
    ChatMessageComponent ric$getSourceComponent();

    @Nullable
    ChatInteractionSpan ric$getInteractionSpan(int characterIndex);

}
