package moddedmite.rustedironcore.mixin.other.client;

import net.minecraft.Gui;
import net.minecraft.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiIngame.class)
public class GuiIngameMixin extends Gui {
//    @Shadow
//    @Final
//    private Minecraft mc;
//
//    @Inject(method = {"renderGameOverlay(FZII)V"},
//            at = {@At(value = "INVOKE",
//                    target = "Lnet/minecraft/Minecraft;inDevMode()Z",
//                    shift = At.Shift.BEFORE)})
//    private void inject(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
//        if (mc.gameSettings.gui_mode == 0 && mc.gameSettings.keyBindPlayerList.pressed) {
//            GuiTipsWindow.updateAchievementWindow(this, "你好", "你好");
//        }
//    }
}
