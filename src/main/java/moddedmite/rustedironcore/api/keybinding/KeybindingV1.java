package moddedmite.rustedironcore.api.keybinding;

import net.minecraft.KeyBinding;
import net.minecraft.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KeybindingV1 extends KeyBinding implements Comparable<KeybindingV1> {
    private final int defaultKey;
    private final Category category;

    public KeybindingV1(String name, int key, Category category) {
        super(name, key);
        this.defaultKey = key;
        this.category = category;
    }

    public String getName() {
        return this.keyDescription;
    }

    public int getKey() {
        return this.keyCode;
    }

    public void setKey(int key) {
        this.keyCode = key;
    }

    public boolean matches(int key) {
        return this.keyCode == key;
    }

    public int getDefaultKey() {
        return this.defaultKey;
    }

    public boolean isDefault() {
        return this.keyCode == this.defaultKey;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isDown() {
        return this.pressed;
    }

    public void setDown(final boolean down) {
        this.pressed = down;
    }

    public boolean consumeClick() {
        if (this.pressTime == 0) {
            return false;
        }

        this.pressTime--;
        return true;
    }

    protected void release() {
        this.pressTime = 0;
        this.setDown(false);
    }

    public int compareTo(final KeybindingV1 o) {
        if (this.category != o.category) {
            return Integer.compare(KeybindingV1.Category.SORT_ORDER.indexOf(this.category), KeybindingV1.Category.SORT_ORDER.indexOf(o.category));
        } else {
            return this.keyDescription.compareTo(o.keyDescription);
        }
    }

    public record Category(ResourceLocation id) {
        // it was private in 26.2, but I guess BGS wants this
        public static final List<Category> SORT_ORDER = new ArrayList<>();
        public static final Category MOVEMENT = register("movement");
        public static final Category MISC = register("misc");
        public static final Category MULTIPLAYER = register("multiplayer");
        public static final Category GAMEPLAY = register("gameplay");
        public static final Category INVENTORY = register("inventory");
        public static final Category DEBUG = register("debug");

        private static Category register(final String name) {
            return register(new ResourceLocation(name));
        }

        public static Category register(final ResourceLocation id) {
            Category category = new Category(id);
            if (SORT_ORDER.contains(category)) {
                throw new IllegalArgumentException(String.format(Locale.ROOT, "Category '%s' is already registered.", id));
            }

            SORT_ORDER.add(category);
            return category;
        }
    }
}
