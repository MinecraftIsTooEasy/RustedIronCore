package moddedmite.rustedironcore.mixin.client.chat;

import moddedmite.rustedironcore.internal.client.chat.ChatInteractionSpan;
import moddedmite.rustedironcore.internal.client.chat.ChatLineAccess;
import net.minecraft.ChatLine;
import net.minecraft.ChatMessageComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@Mixin(ChatLine.class)
public class ChatLineMixin implements ChatLineAccess {

    @Unique
    private List<ChatInteractionSpan> ric$interactionSpans = Collections.emptyList();

    @Unique
    private ChatMessageComponent ric$sourceComponent;

    @Override
    public void ric$setInteractionSpans(List<ChatInteractionSpan> spans) {
        this.ric$interactionSpans = spans;
    }

    @Override
    public void ric$setSourceComponent(@Nullable ChatMessageComponent component) {
        this.ric$sourceComponent = component;
    }

    @Override
    @Nullable
    public ChatMessageComponent ric$getSourceComponent() {
        return this.ric$sourceComponent;
    }

    @Override
    @Nullable
    public ChatInteractionSpan ric$getInteractionSpan(int characterIndex) {
        for (ChatInteractionSpan span : this.ric$interactionSpans) {
            if (characterIndex >= span.start() && characterIndex < span.end()) {
                return span;
            }
        }
        return null;
    }

}
