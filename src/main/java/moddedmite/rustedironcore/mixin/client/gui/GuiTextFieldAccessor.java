package moddedmite.rustedironcore.mixin.client.gui;

import net.minecraft.GuiTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiTextField.class)
public interface GuiTextFieldAccessor {

    @Accessor("lineScrollOffset")
    int ric$getLineScrollOffset();

    @Accessor("cursorCounter")
    int ric$getCursorCounter();

    @Accessor("xPos")
    int ric$getXPosition();

    @Accessor("yPos")
    int ric$getYPosition();
}
