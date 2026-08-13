package moddedmite.rustedironcore.keybinding;

import moddedmite.rustedironcore.api.keybinding.KeybindingV1;
import net.minecraft.I18n;
import net.minecraft.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * @deprecated Use {@link KeybindingV1}.
 */
@Deprecated
public class KeyBindingExtra extends KeybindingV1 {
    private static final Map<String, Category> LEGACY_CATEGORIES = new HashMap<>();

    static {
        LEGACY_CATEGORIES.put("key.categories.movement", Category.MOVEMENT);
        LEGACY_CATEGORIES.put("key.categories.misc", Category.MISC);
        LEGACY_CATEGORIES.put("key.categories.multiplayer", Category.MULTIPLAYER);
        LEGACY_CATEGORIES.put("key.categories.gameplay", Category.GAMEPLAY);
        LEGACY_CATEGORIES.put("key.categories.inventory", Category.INVENTORY);
        LEGACY_CATEGORIES.put("key.categories.util", Category.DEBUG);
        LEGACY_CATEGORIES.put("key.categories.debug", Category.DEBUG);
    }

    private final String keyCategory;

    public KeyBindingExtra(String keyDescription, int keyCode, String category) {
        super(keyDescription, keyCode, resolveCategory(category));
        this.keyCategory = category;
    }

    public int getKeyCodeDefault() {
        return this.getDefaultKey();
    }

    public String getKeyCategory() {
        return this.keyCategory;
    }

    public String getKeyDescription() {
        return this.getName();
    }

    public int getKeyCode() {
        return this.getKey();
    }

    public void setKeyCode(int keyCode) {
        this.setKey(keyCode);
    }

    public int compareTo(KeyBindingExtra other) {
        int categoryComparison = I18n.getStringParams(this.keyCategory, new Object[0])
                .compareTo(I18n.getStringParams(other.keyCategory, new Object[0]));
        return categoryComparison != 0
                ? categoryComparison
                : I18n.getStringParams(this.keyDescription, new Object[0])
                .compareTo(I18n.getStringParams(other.keyDescription, new Object[0]));
    }

    private static Category resolveCategory(String category) {
        return LEGACY_CATEGORIES.computeIfAbsent(category, key -> {
            ResourceLocation id = new ResourceLocation(key);
            return Category.SORT_ORDER.stream()
                    .filter(existing -> existing.id().equals(id))
                    .findFirst()
                    .orElseGet(() -> Category.register(id));
        });
    }
}
