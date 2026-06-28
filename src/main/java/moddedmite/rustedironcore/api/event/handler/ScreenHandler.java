package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IScreenListener;
import net.minecraft.GuiButton;
import net.minecraft.GuiScreen;

import java.util.List;

public class ScreenHandler extends AbstractHandler<IScreenListener> {
	public void onInit(GuiScreen screen, List<GuiButton> buttonList) {
		this.listeners.forEach(x -> x.onInit(screen, buttonList));
	}
	
	public void onClose(GuiScreen screen) {
		this.listeners.forEach(x -> x.onClose(screen));
	}
	
	public void onUpdate(GuiScreen screen) {
		this.listeners.forEach(x -> x.onUpdate(screen));
	}
	
	public void onDraw(GuiScreen screen, int mouseX, int mouseY, float partialTicks) {
		this.listeners.forEach(x -> x.onDraw(screen, mouseX, mouseY, partialTicks));
	}
	
	public void onDrawBackground(GuiScreen screen) {
		this.listeners.forEach(x -> x.onDrawBackground(screen));
	}
	
	public void onButtonAction(GuiScreen screen, List<GuiButton> buttonList, GuiButton button) {
		this.listeners.forEach(x -> x.onButtonAction(screen, buttonList, button));
	}
	
	public void onMouseClicked(GuiScreen screen, int mouseX, int mouseY, int button) {
		this.listeners.forEach(x -> x.onMouseClicked(screen, mouseX, mouseY, button));
	}
	
	public void onMouseReleased(GuiScreen screen, int mouseX, int mouseY, int button) {
		this.listeners.forEach(x -> x.onMouseReleased(screen, mouseX, mouseY, button));
	}
	
	public void onKeyTyped(GuiScreen screen, char typedChar, int keyCode) {
		this.listeners.forEach(x -> x.onKeyTyped(screen, typedChar, keyCode));
	}
	
	public void onMouseInput(GuiScreen screen) {
		this.listeners.forEach(x -> x.onMouseInput(screen));
	}
	
	public void onKeyboardInput(GuiScreen screen) {
		this.listeners.forEach(x -> x.onKeyboardInput(screen));
	}
}
