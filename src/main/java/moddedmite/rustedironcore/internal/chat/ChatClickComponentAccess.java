package moddedmite.rustedironcore.internal.chat;

import moddedmite.rustedironcore.api.chat.ChatClickEvent;
import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.chat.TextHoverEvent;

import javax.annotation.Nullable;
import java.util.List;

public interface ChatClickComponentAccess {

    @Nullable
    ChatClickEvent ric$getClickEvent();

    void ric$setClickEvent(@Nullable ChatClickEvent event);

    @Nullable
    String ric$getInsertion();

    void ric$setInsertion(@Nullable String insertion);

    @Nullable
    TextHoverEvent ric$getHoverEvent();

    void ric$setHoverEvent(@Nullable TextHoverEvent event);

    @Nullable
    String ric$getFont();

    void ric$setFont(@Nullable String font);

    @Nullable
    TextComponent ric$getTextComponent();

    void ric$setTextComponent(@Nullable TextComponent component);

    @Nullable
    String ric$getText();

    @Nullable
    String ric$getTranslationKey();

    @Nullable
    List<?> ric$getChildren();
}
