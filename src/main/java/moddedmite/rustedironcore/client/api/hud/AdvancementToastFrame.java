package moddedmite.rustedironcore.client.api.hud;

import moddedmite.rustedironcore.api.chat.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public enum AdvancementToastFrame {

    TASK,
    GOAL,
    CHALLENGE;

    public TextComponent getDisplayName() {
        return TextComponent.translatable("ric.hud.advancement." + name().toLowerCase(Locale.ROOT));
    }
}
