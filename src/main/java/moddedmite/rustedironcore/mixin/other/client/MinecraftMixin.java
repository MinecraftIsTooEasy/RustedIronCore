package moddedmite.rustedironcore.mixin.other.client;

import moddedmite.rustedironcore.api.gui.GuiTipsWindow;
import net.minecraft.Minecraft;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow public static Minecraft theMinecraft;
    @Unique
    public GuiTipsWindow guiTipsWindow;

    @Inject(method = "startGame", at = @At("RETURN"))
    private void addGui(CallbackInfo ci) {
        this.guiTipsWindow = new GuiTipsWindow(ReflectHelper.dyCast(this));
    }

}
