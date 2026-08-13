package moddedmite.rustedironcore.mixin.client.util;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.KeybindingRegisterEvent;
import moddedmite.rustedironcore.api.event.handler.KeybindingHandler;
import moddedmite.rustedironcore.api.keybinding.KeybindingV1;
import net.minecraft.GameSettings;
import net.minecraft.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSettings.class)
public class GameSettingsMixin {
    @Shadow
    public KeyBinding keyBindForward;
    @Shadow
    public KeyBinding keyBindLeft;
    @Shadow
    public KeyBinding keyBindBack;
    @Shadow
    public KeyBinding keyBindRight;
    @Shadow
    public KeyBinding keyBindJump;
    @Shadow
    public KeyBinding keyBindInventory;
    @Shadow
    public KeyBinding keyBindDrop;
    @Shadow
    public KeyBinding keyBindChat;
    @Shadow
    public KeyBinding keyBindSneak;
    @Shadow
    public KeyBinding keyBindAttack;
    @Shadow
    public KeyBinding keyBindUseItem;
    @Shadow
    public KeyBinding keyBindPlayerList;
    @Shadow
    public KeyBinding keyBindPickBlock;
    @Shadow
    public KeyBinding keyBindCommand;
    @Shadow
    public KeyBinding keyBindToggleRun;
    @Shadow
    public KeyBinding keyBindZoom;
    @Shadow
    public KeyBinding keyBindRedrawChunks;

    @Shadow
    public KeyBinding[] keyBindings;

    @Inject(method = "initKeybindings", at = @At("RETURN"))
    private void onKeybindingsInit(CallbackInfo ci) {
        this.keyBindForward = new KeybindingV1("key.forward", 17, KeybindingV1.Category.MOVEMENT);
        this.keyBindLeft = new KeybindingV1("key.left", 30, KeybindingV1.Category.MOVEMENT);
        this.keyBindBack = new KeybindingV1("key.back", 31, KeybindingV1.Category.MOVEMENT);
        this.keyBindRight = new KeybindingV1("key.right", 32, KeybindingV1.Category.MOVEMENT);
        this.keyBindJump = new KeybindingV1("key.jump", 57, KeybindingV1.Category.MOVEMENT);
        this.keyBindInventory = new KeybindingV1("key.inventory", 18, KeybindingV1.Category.INVENTORY);
        this.keyBindDrop = new KeybindingV1("key.drop", 16, KeybindingV1.Category.GAMEPLAY);
        this.keyBindChat = new KeybindingV1("key.chat", 20, KeybindingV1.Category.MULTIPLAYER);
        this.keyBindSneak = new KeybindingV1("key.sneak", 42, KeybindingV1.Category.MOVEMENT);
        this.keyBindAttack = new KeybindingV1("key.attack", -100, KeybindingV1.Category.GAMEPLAY);
        this.keyBindUseItem = new KeybindingV1("key.use", -99, KeybindingV1.Category.GAMEPLAY);
        this.keyBindPlayerList = new KeybindingV1("key.playerlist", 41, KeybindingV1.Category.MULTIPLAYER);
        this.keyBindPickBlock = new KeybindingV1("key.pickItem", -98, KeybindingV1.Category.GAMEPLAY);
        this.keyBindCommand = new KeybindingV1("key.command", 53, KeybindingV1.Category.MULTIPLAYER);
        this.keyBindToggleRun = new KeybindingV1("key.toggleRun", 15, KeybindingV1.Category.MOVEMENT);
        this.keyBindZoom = new KeybindingV1("key.zoom", 44, KeybindingV1.Category.GAMEPLAY);
        this.keyBindRedrawChunks = new KeybindingV1("key.redrawChunks", 19, KeybindingV1.Category.DEBUG);

        KeyBinding[] array = new KeyBinding[]{
                this.keyBindAttack,
                this.keyBindUseItem,
                this.keyBindForward,
                this.keyBindLeft,
                this.keyBindBack,
                this.keyBindRight,
                this.keyBindJump,
                this.keyBindSneak,
                this.keyBindDrop,
                this.keyBindInventory,
                this.keyBindChat,
                this.keyBindPlayerList,
                this.keyBindPickBlock,
                this.keyBindCommand,
                this.keyBindToggleRun,
                this.keyBindZoom,
                this.keyBindRedrawChunks
        };

        KeybindingHandler handler = Handlers.Keybinding;
        KeybindingRegisterEvent event = new KeybindingRegisterEvent();

        for (KeyBinding keyBinding : array) {
            event.register(((KeybindingV1) keyBinding));
        }

        handler.publish(event);

        this.keyBindings = handler.streamAllKeybindings().toArray(KeyBinding[]::new);
    }
}
