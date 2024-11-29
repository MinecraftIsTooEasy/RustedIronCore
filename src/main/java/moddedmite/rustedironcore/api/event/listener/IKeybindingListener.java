package moddedmite.rustedironcore.api.event.listener;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.KeyBinding;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public interface IKeybindingListener {
    default void onKeybindingRegister(Consumer<KeyBinding> registry) {
    }
}
