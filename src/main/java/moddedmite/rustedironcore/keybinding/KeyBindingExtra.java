package moddedmite.rustedironcore.keybinding;

import net.minecraft.I18n;
import net.minecraft.KeyBinding;
import org.spongepowered.asm.mixin.Shadow;

public class KeyBindingExtra extends KeyBinding {
    private final int keyCodeDefault;
    private final String keyCategory;

    public KeyBindingExtra(String keyDescription, int keyCode, String category) {
        super(keyDescription, keyCode);
        this.keyCodeDefault = keyCode;
        this.keyCategory = category;
    }

//    public static int getKeyCodeDefault(String keyDescription) {
//        Iterator var0 = keybindArray.iterator();
//        while (var0.hasNext()) {
//            KeyBinding keyBinding = (KeyBinding) var0.next();
//            return switch (keyDescription) {
//                case "key.forward" -> 17;
//                case "key.left" -> 30;
//                case "key.back" -> 31;
//                case "key.right" -> 32;
//                case "key.jump" -> 57;
//                case "key.inventory" -> 18;
//                case "key.drop" -> 16;
//                case "key.chat" -> 20;
//                case "key.sneak" -> 42;
//                case "key.attack" -> -100;
//                case "key.use" -> -99;
//                case "key.playerlist" -> 41;
//                case "key.pickItem" -> -98;
//                case "key.command" -> 53;
//                case "key.toggleRun" -> 15;
//                case "key.zoom" -> 44;
//                case "key.redrawChunks" -> 19;
//                default -> ((IKeyBinding) keyBinding).getDefaultKeyCode();
//            };
//        }
//        return 0;
//    }
//
//    public static String getKeyCategory(String keyDescription) {
//        return switch (keyDescription) {
//            case "key.forward", "key.jump", "key.right", "key.back", "key.left", "key.sneak", "key.toggleRun" ->
//                    I18n.getString("key.categories.movement");
//            case "key.inventory" -> I18n.getString("key.categories.inventory");
//            case "key.drop", "key.attack", "key.use", "key.zoom", "key.pickItem" ->
//                    I18n.getString("key.categories.gameplay");
//            case "key.chat", "key.command", "key.playerlist" -> I18n.getString("key.categories.multiplayer");
//            case "key.redrawChunks" -> I18n.getString("key.categories.util");
//            default -> I18n.getString("key.categories.uncategorized");
//        };
//    }

    public int getKeyCodeDefault() {
        return keyCodeDefault;
    }

    public String getKeyCategory() {
        return keyCategory;
    }

    public String getKeyDescription() {
        return this.keyDescription;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public int compareTo(KeyBindingExtra keyBindingExtra) {
        int i = I18n.getStringParams(this.keyCategory, new Object[0]).compareTo(I18n.getStringParams(keyBindingExtra.keyCategory, new Object[0]));

        if (i == 0) {
            i = I18n.getStringParams(this.keyDescription, new Object[0]).compareTo(I18n.getStringParams(keyBindingExtra.keyDescription, new Object[0]));
        }

        return i;
    }
//    @Shadow
//    public KeyBinding keyBindForward;
//    @Shadow public KeyBinding keyBindLeft;
//    @Shadow public KeyBinding keyBindBack;
//    @Shadow public KeyBinding keyBindRight;
//    @Shadow public KeyBinding keyBindJump;
//    @Shadow public KeyBinding keyBindInventory;
//    @Shadow public KeyBinding keyBindDrop;
//    @Shadow public KeyBinding keyBindChat;
//    @Shadow public KeyBinding keyBindSneak;
//    @Shadow public KeyBinding keyBindAttack;
//    @Shadow public KeyBinding keyBindUseItem;
//    @Shadow public KeyBinding keyBindPlayerList;
//    @Shadow public KeyBinding keyBindPickBlock;
//    @Shadow public KeyBinding keyBindCommand;
//    @Shadow public KeyBinding keyBindToggleRun;
//    @Shadow public KeyBinding keyBindZoom;
//    @Shadow public KeyBinding keyBindRedrawChunks;
//
//    keyBindForward = new KeyBindingExtra("key.forward", 17, "key.categories.movement");
//    keyBindLeft = new KeyBindingExtra("key.left", 30, "key.categories.movement");
//    keyBindBack = new KeyBindingExtra("key.back", 31, "key.categories.movement");
//    keyBindRight = new KeyBindingExtra("key.right", 32, "key.categories.movement");
//    keyBindJump = new KeyBindingExtra("key.jump", 57, "key.categories.movement");
//    keyBindInventory = new KeyBindingExtra("key.inventory", 18, "key.categories.inventory");
//    keyBindDrop = new KeyBindingExtra("key.drop", 16, "key.categories.gameplay");
//    keyBindChat = new KeyBindingExtra("key.chat", 20, "key.categories.multiplayer");
//    keyBindSneak = new KeyBindingExtra("key.sneak", 42, "key.categories.movement");
//    keyBindAttack = new KeyBindingExtra("key.attack", -100, "key.categories.gameplay");
//    keyBindUseItem = new KeyBindingExtra("key.use", -99, "key.categories.gameplay");
//    keyBindPlayerList = new KeyBindingExtra("key.playerlist", 41, "key.categories.multiplayer");
//    keyBindPickBlock = new KeyBindingExtra("key.pickItem", -98, "key.categories.gameplay");
//    keyBindCommand = new KeyBindingExtra("key.command", 53, "key.categories.multiplayer");
//    keyBindToggleRun = new KeyBindingExtra("key.toggleRun", 15, "key.categories.movement");
//    keyBindZoom = new KeyBindingExtra("key.zoom", 44, "key.categories.gameplay");
//    keyBindRedrawChunks = new KeyBindingExtra("key.redrawChunks", 19, "key.categories.util");
}
