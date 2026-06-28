package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.GuiButton;
import net.minecraft.GuiScreen;

import java.util.List;

public interface IScreenListener {
	default void onInit(GuiScreen screen, List<GuiButton> buttonList) {
	}

	default void onClose(GuiScreen screen) {
	}

	default void onUpdate(GuiScreen screen) {
	}

	default void onDraw(GuiScreen screen, int mouseX, int mouseY, float partialTicks) {
	}

	default void onDrawBackground(GuiScreen screen) {
	}

	default void onButtonAction(GuiScreen screen, List<GuiButton> buttonList, GuiButton button) {
	}
	
	default void onMouseClicked(GuiScreen screen, int mouseX, int mouseY, int button) {
	}
	
	default void onMouseReleased(GuiScreen screen, int mouseX, int mouseY, int button) {
	}
	
	default void onKeyTyped(GuiScreen screen, char typedChar, int code) {
	}
	
	default void onMouseInput(GuiScreen screen) {
	}
	
	default void onKeyboardInput(GuiScreen screen) {
	}
}
