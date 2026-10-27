package moddedmite.rustedironcore.mixin.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.internal.client.chat.ChatCommandSuggestionsAccess;
import moddedmite.rustedironcore.internal.client.chat.ChatInputTextFormatter;
import net.minecraft.FontRenderer;
import net.minecraft.GuiChat;
import net.minecraft.GuiTextField;
import net.minecraft.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiTextField.class)
public abstract class GuiTextFieldMixin {

    @WrapOperation(method = "drawTextBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I", ordinal = 0))
    private int renderCommandInputPrefix(FontRenderer renderer, String text, int x, int y, int color, Operation<Integer> original) {
        GuiTextField input = (GuiTextField) (Object) this;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (!(minecraft.currentScreen instanceof GuiChat chat)
                || !(chat instanceof ChatCommandSuggestionsAccess suggestions)) {
            return original.call(renderer, text, x, y, color);
        }

        int textStart = ((GuiTextFieldAccessor) input).ric$getLineScrollOffset();
        String formattedText = ChatInputTextFormatter.format(suggestions, text, textStart, color);
        int endX = original.call(renderer, formattedText, x, y, color);
        if (input.getCursorPosition() == input.getText().length()) {
            String suffix = suggestions.ric$getInputSuggestionSuffix();
            if (suffix != null) {
                String visibleText = renderer.trimStringToWidth(
                        input.getText().substring(textStart), input.getWidth());
                int availableWidth = Math.max(0, input.getWidth() - renderer.getStringWidth(visibleText) + 1);
                String visibleSuffix = renderer.trimStringToWidth(suffix, availableWidth);
                if (!visibleSuffix.isEmpty()) {
                    renderer.drawStringWithShadow(visibleSuffix, endX - 1, y, 0xFF808080);
                }
            }
        }
        return endX;
    }

    @WrapOperation(method = "drawTextBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I", ordinal = 1))
    private int renderCommandInputSuffix(FontRenderer renderer, String text, int x, int y, int color, Operation<Integer> original) {
        GuiTextField input = (GuiTextField) (Object) this;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (!(minecraft.currentScreen instanceof GuiChat chat)
                || !(chat instanceof ChatCommandSuggestionsAccess suggestions)) {
            return original.call(renderer, text, x, y, color);
        }

        return original.call(renderer,
                ChatInputTextFormatter.format(suggestions, text, input.getCursorPosition(), color), x, y, color);
    }
}
