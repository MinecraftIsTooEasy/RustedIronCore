package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IKeybindingListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.KeyBinding;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class KeybindingHandler extends AbstractHandler<IKeybindingListener> {
    public void onKeybindingRegister(Consumer<KeyBinding> registry) {
        this.listeners.forEach(x -> x.onKeybindingRegister(registry));
    }
}
