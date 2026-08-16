package moddedmite.rustedironcore.mixin.client.gui;

import net.minecraft.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Gui.class)
public interface IGuiMixin {
    @Invoker
    void invokeDrawGradientRect(int x1, int y1, int x2, int y2, int color1, int color2);
}
