package moddedmite.rustedironcore.mixin;

import moddedmite.rustedironcore.api.chat.ChatClickEvent;
import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.chat.TextHoverEvent;
import moddedmite.rustedironcore.api.chat.TextFonts;
import moddedmite.rustedironcore.internal.chat.ChatClickComponentAccess;
import net.minecraft.ChatMessageComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ChatMessageComponent.class)
public abstract class ChatMessageComponentMixin implements ChatClickComponentAccess {

    @Unique
    private ChatClickEvent ric$clickEvent;

    @Unique
    private String ric$insertion;

    @Unique
    private TextHoverEvent ric$hoverEvent;

    @Unique
    private String ric$font;

    @Unique
    private TextComponent ric$textComponent;

    @Shadow
    protected abstract String getText();

    @Shadow
    protected abstract String getTranslationKey();

    @Shadow
    protected abstract List<ChatMessageComponent> getSubComponents();

    @Inject(method = "<init>(Lnet/minecraft/ChatMessageComponent;)V", at = @At("RETURN"))
    private void copyChatInteraction(ChatMessageComponent original, CallbackInfo ci) {
        ChatClickComponentAccess access = (ChatClickComponentAccess) original;
        this.ric$clickEvent = access.ric$getClickEvent();
        this.ric$insertion = access.ric$getInsertion();
        this.ric$hoverEvent = access.ric$getHoverEvent();
        this.ric$font = access.ric$getFont();
        TextComponent textComponent = access.ric$getTextComponent();
        this.ric$textComponent = textComponent == null ? null : textComponent.copy();
    }

    @Override
    @Nullable
    public ChatClickEvent ric$getClickEvent() {
        return this.ric$clickEvent;
    }

    @Override
    public void ric$setClickEvent(@Nullable ChatClickEvent event) {
        this.ric$clickEvent = event;
        if (this.ric$textComponent != null) {
            this.ric$textComponent.withStyle(style -> style.withClickEvent(event));
        }
    }

    @Override
    @Nullable
    public String ric$getInsertion() {
        return this.ric$insertion;
    }

    @Override
    public void ric$setInsertion(@Nullable String insertion) {
        this.ric$insertion = insertion;
        if (this.ric$textComponent != null) {
            this.ric$textComponent.withStyle(style -> style.withInsertion(insertion));
        }
    }

    @Override
    @Nullable
    public TextHoverEvent ric$getHoverEvent() {
        return this.ric$hoverEvent;
    }

    @Override
    public void ric$setHoverEvent(@Nullable TextHoverEvent event) {
        this.ric$hoverEvent = event;
        if (this.ric$textComponent != null) {
            this.ric$textComponent.withStyle(style -> style.withHoverEvent(event));
        }
    }

    @Override
    @Nullable
    public String ric$getFont() {
        return this.ric$textComponent == null ? this.ric$font : this.ric$textComponent.getStyle().getFont();
    }

    @Override
    public void ric$setFont(@Nullable String font) {
        this.ric$font = TextFonts.normalize(font);
        if (this.ric$textComponent != null) {
            this.ric$textComponent.withStyle(style -> style.withFont(this.ric$font));
        }
    }

    @Override
    @Nullable
    public TextComponent ric$getTextComponent() {
        return this.ric$textComponent;
    }

    @Override
    public void ric$setTextComponent(@Nullable TextComponent component) {
        this.ric$textComponent = component;
        if (component != null) {
            if (this.ric$clickEvent != null) component.withStyle(style -> style.withClickEvent(this.ric$clickEvent));
            if (this.ric$insertion != null) component.withStyle(style -> style.withInsertion(this.ric$insertion));
            if (this.ric$font != null) component.withStyle(style -> style.withFont(this.ric$font));
        }
    }

    @Override
    public String ric$getText() {
        return this.getText();
    }

    @Override
    public String ric$getTranslationKey() {
        return this.getTranslationKey();
    }

    @Override
    public List<?> ric$getChildren() {
        return this.getSubComponents();
    }
}
