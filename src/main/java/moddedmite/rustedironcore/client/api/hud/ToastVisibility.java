package moddedmite.rustedironcore.client.api.hud;

import moddedmite.rustedironcore.api.sound.ResourceSoundPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ResourceLocation;

@Environment(EnvType.CLIENT)
public enum ToastVisibility {

    SHOW("in"),
    HIDE("out");

    private final ResourceLocation sound;

    ToastVisibility(String name) {
        sound = new ResourceLocation("ric", "sounds/ui/toast/" + name + ".ogg", false);
    }

    public void playSound() {
        ResourceSoundPlayer.play(sound, 1, 1);
    }
}
