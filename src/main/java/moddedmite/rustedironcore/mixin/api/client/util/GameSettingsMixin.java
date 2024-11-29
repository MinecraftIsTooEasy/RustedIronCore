package moddedmite.rustedironcore.mixin.api.client.util;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.GameSettings;
import net.minecraft.KeyBinding;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(GameSettings.class)
public class GameSettingsMixin {
    @Shadow
    public KeyBinding[] keyBindings;

    @Inject(method = "initKeybindings", at = @At("RETURN"))
    private void onKeybindingsInit(CallbackInfo ci) {
        List<KeyBinding> list = new ArrayList<>();
        Handlers.Keybinding.onKeybindingRegister(list::add);
        this.keyBindings = ArrayUtils.addAll(this.keyBindings, list.toArray(KeyBinding[]::new));
    }
}
