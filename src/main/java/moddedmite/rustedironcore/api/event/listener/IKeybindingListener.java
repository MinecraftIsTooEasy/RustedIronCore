package moddedmite.rustedironcore.api.event.listener;

import moddedmite.rustedironcore.api.event.events.KeybindingRegisterEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.KeyBinding;

import java.util.function.Consumer;

/**
 * @deprecated Register a {@code Consumer<KeybindingRegisterEvent>} with
 * {@code Handlers.Keybinding} instead.
 */
@Deprecated
@Environment(EnvType.CLIENT)
public interface IKeybindingListener extends Consumer<KeybindingRegisterEvent> {
    default void onKeybindingRegister(Consumer<KeyBinding> registry) {
    }

    @Override
    default void accept(KeybindingRegisterEvent event) {
        this.onKeybindingRegister(event::register);
    }
}
