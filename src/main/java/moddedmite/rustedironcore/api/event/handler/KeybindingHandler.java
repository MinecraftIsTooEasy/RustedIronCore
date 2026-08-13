package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.KeybindingRegisterEvent;
import moddedmite.rustedironcore.api.keybinding.KeybindingV1;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.KeyBinding;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class KeybindingHandler extends EventHandler<KeybindingRegisterEvent> {

    private final Map<String, KeyBinding> all = new LinkedHashMap<>();

    public void registerKeybinding(KeybindingV1 keybinding) {
        this.all.put(keybinding.getName(), keybinding);
    }

    /**
     * @deprecated Use {@link #registerKeybinding(KeybindingV1)}.
     */
    @Deprecated
    public void registerLegacyKeybinding(KeyBinding keybinding) {
        this.all.put(keybinding.keyDescription, keybinding);
    }

    public Stream<KeybindingV1> streamKeybindings() {
        return this.all.values().stream()
                .filter(KeybindingV1.class::isInstance)
                .map(KeybindingV1.class::cast);
    }

    public Stream<KeyBinding> streamAllKeybindings() {
        return this.all.values().stream();
    }

    @Nullable
    public KeybindingV1 getKeybinding(String name) {
        KeyBinding keybinding = this.all.get(name);
        return keybinding instanceof KeybindingV1 modern ? modern : null;
    }

    /**
     * @deprecated Register a {@code Consumer<KeybindingRegisterEvent>} instead.
     */
    @Deprecated
    public void onKeybindingRegister(Consumer<KeyBinding> registry) {
        this.publish(new KeybindingRegisterEvent(registry));
    }
}
