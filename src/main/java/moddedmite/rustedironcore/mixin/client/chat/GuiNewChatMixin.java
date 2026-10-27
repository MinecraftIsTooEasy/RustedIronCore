package moddedmite.rustedironcore.mixin.client.chat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.internal.chat.ChatClickSupport;
import moddedmite.rustedironcore.internal.chat.ChatClickComponentAccess;
import moddedmite.rustedironcore.internal.chat.LegacyTextFormatting;
import moddedmite.rustedironcore.internal.client.chat.ChatInteractionHit;
import moddedmite.rustedironcore.internal.client.chat.ChatInteractionSpan;
import moddedmite.rustedironcore.internal.client.chat.ChatGuiAccess;
import moddedmite.rustedironcore.internal.client.chat.ChatLineAccess;
import moddedmite.rustedironcore.internal.client.chat.ChatInteractionRenderer;
import moddedmite.rustedironcore.internal.client.chat.MiteTextComponentAdapter;
import net.minecraft.ChatLine;
import net.minecraft.ChatMessageComponent;
import net.minecraft.GuiNewChat;
import net.minecraft.MathHelper;
import net.minecraft.Minecraft;
import net.minecraft.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

@Mixin(GuiNewChat.class)
public abstract class GuiNewChatMixin implements ChatGuiAccess {

    @Shadow
    @Final
    private Minecraft mc;

    @Shadow
    @Final
    private List<ChatLine> field_96134_d;

    @Shadow
    private int field_73768_d;

    @Shadow
    public abstract int func_96126_f();

    @Shadow
    public abstract int func_96127_i();

    @Shadow
    public abstract float func_96131_h();

    @Shadow
    public abstract boolean getChatOpen();

    @Shadow
    public abstract void printChatMessage(String message);

    @Unique
    private Deque<List<ChatInteractionSpan>> ric$pendingLineInteractionSpans;

    @Unique
    private ChatMessageComponent ric$pendingSourceComponent;

    @Override
    public void ric$printChatMessage(ChatMessageComponent component) {
        ChatMessageComponent displayComponent = component;
        String formattedMessage;
        TextComponent textComponent = ((ChatClickComponentAccess) component).ric$getTextComponent();
        if (textComponent != null) {
            try {
                displayComponent = MiteTextComponentAdapter.toMiteComponent(textComponent);
                formattedMessage = MiteTextComponentAdapter.toFormattedString(textComponent);
            } catch (UnsupportedOperationException exception) {
                displayComponent = component;
                formattedMessage = component.toStringWithFormatting(true);
            }
        } else {
            formattedMessage = component.toStringWithFormatting(true);
        }
        int width = MathHelper.floor_float((float) this.func_96126_f() / this.func_96131_h());
        List<?> wrappedLines = this.mc.fontRenderer.listFormattedStringToWidth(formattedMessage, width);
        this.ric$pendingLineInteractionSpans = ChatClickSupport.createWrappedLineInteractionSpans(displayComponent, wrappedLines);
        this.ric$pendingSourceComponent = component;
        try {
            this.printChatMessage(formattedMessage);
        } finally {
            this.ric$pendingLineInteractionSpans = null;
            this.ric$pendingSourceComponent = null;
        }
    }

    @WrapOperation(method = "func_96129_a", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 0))
    private void attachInteractionSpans(List<?> instance, int index, Object value, Operation<Void> original) {
        if (value instanceof ChatLine chatLine) {
            List<ChatInteractionSpan> spans = this.ric$pendingLineInteractionSpans == null
                    || this.ric$pendingLineInteractionSpans.isEmpty()
                    ? Collections.emptyList()
                    : this.ric$pendingLineInteractionSpans.removeFirst();
            List<ChatInteractionSpan> textSpans = ChatClickSupport.createPlainTextInteractionSpans(chatLine.getChatLineString());
            if (!textSpans.isEmpty()) {
                List<ChatInteractionSpan> combined = new ArrayList<>(spans.size() + textSpans.size());
                combined.addAll(spans);
                combined.addAll(textSpans);
                spans = List.copyOf(combined);
            }
            ((ChatLineAccess) chatLine).ric$setInteractionSpans(spans);
        }
        original.call(instance, index, value);
    }

    @WrapOperation(method = "func_96129_a", at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 1))
    private void preserveSourceComponent(List<?> instance, int index, Object value, Operation<Void> original) {
        if (value instanceof ChatLine chatLine && this.ric$pendingSourceComponent != null) {
            ((ChatLineAccess) chatLine).ric$setSourceComponent(this.ric$pendingSourceComponent);
        }
        original.call(instance, index, value);
    }

    @WrapOperation(method = "func_96132_b", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private Object prepareInteractionSpansForRefresh(List<?> instance, int index, Operation<Object> original) {
        Object value = original.call(instance, index);
        if (value instanceof ChatLine chatLine) {
            ChatMessageComponent component = ((ChatLineAccess) chatLine).ric$getSourceComponent();
            if (component != null) {
                int width = MathHelper.floor_float((float) this.func_96126_f() / this.func_96131_h());
                List<?> wrappedLines = this.mc.fontRenderer.listFormattedStringToWidth(chatLine.getChatLineString(), width);
                TextComponent structured = ((ChatClickComponentAccess) component).ric$getTextComponent();
                ChatMessageComponent display = structured == null ? component : MiteTextComponentAdapter.toMiteComponent(structured);
                this.ric$pendingLineInteractionSpans = ChatClickSupport.createWrappedLineInteractionSpans(display, wrappedLines);
            } else {
                this.ric$pendingLineInteractionSpans = null;
            }
        }
        return value;
    }

    @Inject(method = "func_96132_b", at = @At("RETURN"))
    private void clearRefreshState(CallbackInfo ci) {
        this.ric$pendingLineInteractionSpans = null;
    }

    @Inject(method = "drawChat", at = @At("RETURN"))
    private void highlightHoveredClickSpan(int updateCounter, CallbackInfo ci) {
        if (!this.getChatOpen() || this.mc.gameSettings.gui_mode != 0 || this.mc.gameSettings.chatVisibility == 2) {
            return;
        }

        ScaledResolution resolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int mouseX = Mouse.getX() * resolution.getScaledWidth() / this.mc.displayWidth;
        int mouseY = resolution.getScaledHeight()
                - Mouse.getY() * resolution.getScaledHeight() / this.mc.displayHeight - 1;
        ChatInteractionHit hit = this.ric$getInteractionAt(mouseX, mouseY);
        if (hit == null) {
            return;
        }

        if (hit.span().clickEvent() != null) {
            ChatLine line = this.field_96134_d.get(hit.lineIndex());
            ChatInteractionRenderer.renderClickHighlight(this.mc.fontRenderer, line.getChatLineString(), hit.span(),
                    hit.lineIndex() - this.field_73768_d, this.func_96131_h());
        }

        ChatInteractionRenderer.renderHoverTooltip(this.mc.fontRenderer, hit.span().hoverEvent(), mouseX, mouseY,
                resolution.getScaledWidth(), resolution.getScaledHeight());
    }

    @Override
    public ChatInteractionHit ric$getInteractionAt(int mouseX, int mouseY) {
        if (!this.getChatOpen() || this.mc.gameSettings.gui_mode != 0 || this.mc.gameSettings.chatVisibility == 2) {
            return null;
        }

        ScaledResolution resolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        float chatScale = this.func_96131_h();
        int chatX = MathHelper.floor_float((mouseX - 2.0F) / chatScale);
        float mouseYFromBottom = resolution.getScaledHeight() - mouseY;
        int chatY = MathHelper.floor_float((mouseYFromBottom - 28.0F) / chatScale - 18.0F);
        if (chatX < 0 || chatY < 0) {
            return null;
        }

        int visibleLines = Math.min(this.func_96127_i(), this.field_96134_d.size());
        int maxWidth = MathHelper.floor_float((float) this.func_96126_f() / chatScale);
        int lineHeight = this.mc.fontRenderer.FONT_HEIGHT;
        if (chatX > maxWidth || chatY >= lineHeight * visibleLines) {
            return null;
        }

        int lineIndex = chatY / lineHeight + this.field_73768_d;
        if (lineIndex < 0 || lineIndex >= this.field_96134_d.size()) {
            return null;
        }
        ChatLine line = this.field_96134_d.get(lineIndex);
        String prefix = this.mc.fontRenderer.trimStringToWidth(line.getChatLineString(), chatX);
        int characterIndex = LegacyTextFormatting.strip(prefix).length();
        ChatInteractionSpan span = ((ChatLineAccess) line).ric$getInteractionSpan(characterIndex);
        return span == null ? null : new ChatInteractionHit(lineIndex, span);
    }
}
