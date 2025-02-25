package moddedmite.rustedironcore.mixin.client.gui;

import net.minecraft.EnumChatFormatting;
import net.minecraft.GuiMainMenu;
import net.minecraft.I18n;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class GuiMainMenuMixin {
    @Mutable
    @Shadow
    @Final
    public static String field_96138_a_MITE;

    @Unique
    private static final String HOMEPAGE = "https://minecraftistooeasy.github.io/";

    @ModifyConstant(method = "confirmClicked", constant = @Constant(stringValue = "http://minecraft-is-too-easy.com"))
    private String modifyURI(String constant) {
        return HOMEPAGE;
    }

    @ModifyConstant(method = "mouseClicked", constant = @Constant(stringValue = "http://minecraft-is-too-easy.com"))
    private String modifyURI_1(String constant) {
        return HOMEPAGE;
    }

    @ModifyConstant(method = "drawScreen", constant = @Constant(stringValue = "MITE Resource Pack 1.6.4 needs to be installed!"))
    private String translate(String constant) {
        return I18n.getString("ric.gui.resourcepacks.info");
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void translate_1(CallbackInfo ci) {
        field_96138_a_MITE = I18n.getString("ric.gui.resourcepacks.info.website.1") + " " + (EnumChatFormatting.UNDERLINE) + "https://minecraftistooeasy.github.io/" + (EnumChatFormatting.RESET) + " " + I18n.getString("ric.gui.resourcepacks.info.website.2");
    }
}
