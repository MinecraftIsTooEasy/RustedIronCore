package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.keybinding.KeybindingV1;
import net.minecraft.ResourceLocation;

public class KeybindingRegisterEvent {
    public void register(KeybindingV1 keybinding) {
        Handlers.Keybinding.registerKeybinding(keybinding);
    }

    public KeybindingV1.Category registerCategory(ResourceLocation id) {
        return KeybindingV1.Category.register(id);
    }
}
