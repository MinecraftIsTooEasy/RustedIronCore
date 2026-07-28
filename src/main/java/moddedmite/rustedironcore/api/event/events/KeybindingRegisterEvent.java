package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.keybinding.KeybindingV1;

public class KeybindingRegisterEvent {
    public void register(KeybindingV1 keybinding) {
        Handlers.Keybinding.registerKeybinding(keybinding);
    }
}
