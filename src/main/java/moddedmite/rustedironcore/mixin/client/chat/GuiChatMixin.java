package moddedmite.rustedironcore.mixin.client.chat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.chat.ChatClickEvent;
import moddedmite.rustedironcore.internal.client.chat.ChatClickEventContext;
import moddedmite.rustedironcore.internal.client.chat.ChatClickEventHandler;
import moddedmite.rustedironcore.internal.client.chat.ChatCommandSuggestions;
import moddedmite.rustedironcore.internal.client.chat.ChatCommandSuggestionsAccess;
import moddedmite.rustedironcore.internal.client.chat.ChatGuiAccess;
import moddedmite.rustedironcore.internal.client.chat.ChatInteractionHit;
import moddedmite.rustedironcore.internal.command.CommandSuggestionResult;
import net.minecraft.ChatAllowedCharacters;
import net.minecraft.ChatClickData;
import net.minecraft.GuiChat;
import net.minecraft.GuiConfirmOpenLink;
import net.minecraft.GuiNewChat;
import net.minecraft.GuiScreen;
import net.minecraft.GuiTextField;
import net.minecraft.Minecraft;
import net.minecraft.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;

@Mixin(GuiChat.class)
public abstract class GuiChatMixin extends GuiScreen implements ChatCommandSuggestionsAccess, ChatClickEventContext {

    @Shadow
    private URI clickedURI;

    @Shadow
    protected GuiTextField inputField;

    @Unique
    private ChatCommandSuggestions ric$commandSuggestions;

    @Shadow
    private void func_73896_a(URI uri) {
    }

    @Inject(method = "initGui", at = @At("RETURN"))
    private void extendChatInputLength(CallbackInfo ci) {
        this.inputField.setMaxStringLength(256);
        this.ric$commandSuggestions = new ChatCommandSuggestions(
                (GuiChat) (Object) this, this.inputField, this.mc, this.inputField.getText());
        this.ric$commandSuggestions.refresh();
    }

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    private void handleCommandSuggestionKey(char character, int keyCode, CallbackInfo ci) {
        if (this.ric$commandSuggestions != null && this.ric$commandSuggestions.keyPressed(keyCode)) ci.cancel();
    }

    @WrapOperation(method = "keyTyped", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiTextField;textboxKeyTyped(CI)Z"))
    private boolean refreshCommandSuggestionsOnEdit(GuiTextField input, char character, int keyCode, Operation<Boolean> original) {
        String previousText = input.getText();
        int previousCursor = input.getCursorPosition();
        boolean handled = original.call(input, character, keyCode);
        if (this.ric$commandSuggestions != null
                && (!input.getText().equals(previousText) || input.getCursorPosition() != previousCursor)) {
            this.ric$commandSuggestions.refresh();
        }
        return handled;
    }

    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiTextField;mouseClicked(III)V"))
    private void refreshCommandSuggestionsOnCursorClick(GuiTextField input, int mouseX, int mouseY, int button, Operation<Void> original) {
        int previousCursor = input.getCursorPosition();
        original.call(input, mouseX, mouseY, button);
        if (this.ric$commandSuggestions != null && input.getCursorPosition() != previousCursor) {
            this.ric$commandSuggestions.refresh();
        }
    }

    @Inject(method = "getSentHistory", at = @At("RETURN"))
    private void hideCommandSuggestionsForHistory(int delta, CallbackInfo ci) {
        if (this.ric$commandSuggestions != null) this.ric$commandSuggestions.suppressSuggestionsForCurrentInput();
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void renderCommandSuggestions(int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.ric$commandSuggestions != null) {
            boolean depthTestEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glPushMatrix();
            try {
                GL11.glTranslatef(0.0F, 0.0F, 200.0F);
                this.ric$commandSuggestions.render(mouseX, mouseY);
            } finally {
                GL11.glPopMatrix();
                if (depthTestEnabled) GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void selectCommandSuggestion(int mouseX, int mouseY, int button, CallbackInfo ci) {
        if (this.ric$commandSuggestions != null && this.ric$commandSuggestions.mouseClicked(mouseX, mouseY, button)) {
            ci.cancel();
        }
    }

    @Redirect(method = "handleMouseInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiNewChat;scroll(I)V"))
    private void scrollCommandSuggestionsOrChat(GuiNewChat chat, int amount) {
        if (this.ric$commandSuggestions != null && this.ric$commandSuggestions.isVisible()) {
            ScaledResolution resolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
            int mouseX = Mouse.getX() * resolution.getScaledWidth() / this.mc.displayWidth;
            int mouseY = resolution.getScaledHeight() - Mouse.getY() * resolution.getScaledHeight() / this.mc.displayHeight - 1;
            if (this.ric$commandSuggestions.mouseScrolled(amount, mouseX, mouseY)) return;
        }
        chat.scroll(amount);
    }

    @Override
    public void ric$receiveCommandSuggestions(int requestId, String input, int cursor, CommandSuggestionResult result) {
        if (this.ric$commandSuggestions != null) {
            this.ric$commandSuggestions.receive(requestId, input, cursor, result);
        }
    }

    @Override
    public int ric$getInputTextColor(int inputPosition, int defaultColor) {
        return this.ric$commandSuggestions == null ? defaultColor
                : this.ric$commandSuggestions.inputTextColor(inputPosition, defaultColor);
    }

    @Override
    public String ric$getInputSuggestionSuffix() {
        return this.ric$commandSuggestions == null ? null : this.ric$commandSuggestions.ghostSuffix();
    }

    @Override
    public Minecraft ric$getMinecraft() {
        return this.mc;
    }

    @Override
    public GuiTextField ric$getInputField() {
        return this.inputField;
    }

    @Override
    public void ric$confirmOpenUrl(URI uri, String value) {
        this.clickedURI = uri;
        this.mc.displayGuiScreen(new GuiConfirmOpenLink((GuiChat) (Object) this, value, 0, false));
    }

    @Override
    public void ric$openUrl(URI uri) {
        this.func_73896_a(uri);
    }

    @Override
    public void ric$refreshCommandSuggestions() {
        if (this.ric$commandSuggestions != null) this.ric$commandSuggestions.refresh();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void handleComponentClick(int mouseX, int mouseY, int button, CallbackInfo ci) {
        if (button != 0) {
            return;
        }
        ChatInteractionHit hit = ((ChatGuiAccess) this.mc.ingameGUI.getChatGUI()).ric$getInteractionAt(mouseX, mouseY);
        if (hit == null) {
            return;
        }

        String insertion = hit.span().insertion();
        if (GuiScreen.isShiftKeyDown() && insertion != null) {
            this.inputField.writeText(ChatAllowedCharacters.filerAllowedCharacters(insertion));
            ci.cancel();
            return;
        }

        ChatClickEvent event = hit.span().clickEvent();
        if (event == null || !ChatClickEventHandler.handle(this, event)) {
            return;
        }
        ci.cancel();
    }

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiNewChat;func_73766_a(II)Lnet/minecraft/ChatClickData;"))
    private ChatClickData useStructuredClickHitTesting(GuiNewChat chat, int mouseX, int mouseY) {
        ScaledResolution resolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        int scaledMouseX = mouseX * resolution.getScaledWidth() / this.mc.displayWidth;
        int scaledMouseY = resolution.getScaledHeight()
                - mouseY * resolution.getScaledHeight() / this.mc.displayHeight - 1;
        ChatInteractionHit hit = ((ChatGuiAccess) chat).ric$getInteractionAt(scaledMouseX, scaledMouseY);
        return hit != null && hit.span().clickEvent() != null ? null : chat.func_73766_a(mouseX, mouseY);
    }

}
