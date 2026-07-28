package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.KeybindingRegisterEvent;
import moddedmite.rustedironcore.api.keybinding.KeybindingV1;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class KeybindingHandler extends EventHandler<KeybindingRegisterEvent> {

    private final Map<String, KeybindingV1> all = new HashMap<>();

    public void registerKeybinding(KeybindingV1 keybinding) {
        all.put(keybinding.getName(), keybinding);
    }

    public Stream<KeybindingV1> streamKeybindings() {
        return this.all.values().stream();
    }

    @Nullable
    public KeybindingV1 getKeybinding(String name) {
        return this.all.get(name);
    }
}
