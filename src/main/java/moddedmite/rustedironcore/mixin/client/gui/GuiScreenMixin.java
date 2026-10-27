package moddedmite.rustedironcore.mixin.client.gui;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.internal.client.chat.BookScreenAccess;
import net.minecraft.GuiButton;
import net.minecraft.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiScreen.class)
public class GuiScreenMixin {
	@Shadow protected List buttonList;

	@Inject(method = "initGui", at = @At("HEAD"))
	private void onInit(CallbackInfo ci) {
		Handlers.Screen.onInit((GuiScreen) (Object) this, this.buttonList);
	}

	@Inject(method = "onGuiClosed", at = @At("HEAD"))
	private void onClose(CallbackInfo ci) {
		Handlers.Screen.onClose((GuiScreen) (Object) this);
	}

	@Inject(method = "updateScreen", at = @At("HEAD"))
	private void onUpdate(CallbackInfo ci) {
		Handlers.Screen.onUpdate((GuiScreen) (Object) this);
	}

	@Inject(method = "drawScreen", at = @At("TAIL"))
	private void onDraw(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
		Handlers.Screen.onDraw((GuiScreen) (Object) this, mouseX, mouseY, partialTicks);
	}

	@Inject(method = "drawBackground", at = @At("TAIL"))
	private void onDrawBackground(int i, CallbackInfo ci) {
		Handlers.Screen.onDrawBackground((GuiScreen) (Object) this);
	}

	@Inject(method = "actionPerformed", at = @At("HEAD"))
	private void onButtonAction(GuiButton button, CallbackInfo ci) {
		Handlers.Screen.onButtonAction((GuiScreen) (Object) this, this.buttonList, button);
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
	private void onMouseClicked(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
		if (this instanceof BookScreenAccess bookScreenAccess && bookScreenAccess.ric$handleBookComponentClick(mouseX, mouseY, mouseButton)) {
			ci.cancel();
			return;
		}
		Handlers.Screen.onMouseClicked((GuiScreen) (Object) this, mouseX, mouseY, mouseButton);
	}

	@Inject(method = "mouseMovedOrUp", at = @At("HEAD"))
	private void onMouseReleased(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
		Handlers.Screen.onMouseReleased((GuiScreen) (Object) this, mouseX, mouseY, mouseButton);
	}

	@Inject(method = "keyTyped", at = @At("HEAD"))
	private void onKeyTyped(char typedChar, int keyCode, CallbackInfo ci) {
		Handlers.Screen.onKeyTyped((GuiScreen) (Object) this, typedChar, keyCode);
	}

	@Inject(method = "handleMouseInput", at = @At("HEAD"))
	private void onMouseInput(CallbackInfo ci) {
		Handlers.Screen.onMouseInput((GuiScreen) (Object) this);
	}

	@Inject(method = "handleKeyboardInput", at = @At("HEAD"))
	private void onKeyboardInput(CallbackInfo ci) {
		Handlers.Screen.onKeyboardInput((GuiScreen) (Object) this);
	}
}
