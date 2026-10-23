package moddedmite.rustedironcore.api.resource;

import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Maps the modern nine-pixel HUD sprites onto the legacy 256-unit icons atlas. */
public final class LegacyHudSprites {

    public record Slot(String sprite, int x, int y) {}

    private static final List<Slot> SLOTS = createSlots();
    private static final String PREFIX = "textures/gui/sprites/hud/";

    public static List<Slot> slots() {
        return SLOTS;
    }

    private static List<Slot> createSlots() {
        List<Slot> slots = new ArrayList<>();
        slots.add(new Slot("armor_empty", 16, 9));
        slots.add(new Slot("armor_half", 25, 9));
        slots.add(new Slot("armor_full", 34, 9));
        slots.add(new Slot("air", 16, 18));
        slots.add(new Slot("air_empty", 25, 18));
        slots.add(new Slot("food_empty", 16, 27));
        slots.add(new Slot("food_full", 52, 27));
        slots.add(new Slot("food_half", 61, 27));
        slots.add(new Slot("food_empty_hunger", 133, 27));
        slots.add(new Slot("food_full_hunger", 88, 27));
        slots.add(new Slot("food_half_hunger", 97, 27));
        slots.add(new Slot("heart/vehicle_container", 52, 9));
        slots.add(new Slot("heart/vehicle_full", 88, 9));
        slots.add(new Slot("heart/vehicle_half", 97, 9));
        for (boolean hardcore : new boolean[]{false, true}) {
            int y = hardcore ? 45 : 0;
            slots.add(new Slot("heart/container" + (hardcore ? "_hardcore" : ""), 16, y));
            slots.add(new Slot("heart/container" + (hardcore ? "_hardcore" : "") + "_blinking", 25, y));
            String[] types = {"", "poisoned_", "withered_"};
            for (int i = 0; i < types.length; i++) {
                String type = "heart/" + types[i] + (hardcore ? "hardcore_" : "");
                slots.add(new Slot(type + "full", 52 + i * 36, y));
                slots.add(new Slot(type + "half", 61 + i * 36, y));
                slots.add(new Slot(type + "full_blinking", 70 + i * 36, y));
                slots.add(new Slot(type + "half_blinking", 79 + i * 36, y));
                // MITE offsets absorption by the active poison/wither column too.
                String absorbing = "heart/absorbing_" + (hardcore ? "hardcore_" : "");
                slots.add(new Slot(absorbing + "full", 160 + i * 36, y));
                slots.add(new Slot(absorbing + "half", 169 + i * 36, y));
            }
        }
        return List.copyOf(slots);
    }

    public static BufferedImage load(ResourceManager manager, ResourceLocation atlas, BufferedImage base)
            throws IOException {
        if (!atlas.getResourceDomain().equals("minecraft") ||
                !(atlas.getResourcePath().equals("textures/gui/icons.png") ||
                        atlas.getResourcePath().equals("textures/gui/icons.enc"))) return base;
        int baseRank = ResourcePackOrder.rank(manager, atlas);
        Map<String, BufferedImage> sprites = new LinkedHashMap<>();
        for (Slot slot : SLOTS) {
            if (sprites.containsKey(slot.sprite())) continue;
            ResourceLocation location = new ResourceLocation("minecraft", PREFIX + slot.sprite() + ".png", false);
            int rank = ResourcePackOrder.rank(manager, location);
            if (rank < 0 || rank < baseRank) continue;
            try (InputStream stream = manager.getResource(location).getInputStream()) {
                BufferedImage sprite = ImageIO.read(stream);
                if (sprite == null) throw new IOException("Invalid HUD sprite: " + location);
                sprites.put(slot.sprite(), sprite);
            } catch (Exception failure) {
                if (failure instanceof IOException io) throw io;
                throw new IOException("Unable to read HUD sprite " + location, failure);
            }
        }
        return compose(base, sprites);
    }

    /** Pure image operation, also used by headless pack audits. Transparent pixels replace old pixels. */
    public static BufferedImage compose(BufferedImage base, Map<String, BufferedImage> sprites) throws IOException {
        if (sprites.isEmpty()) return base;
        if (base == null || base.getWidth() % 256 != 0 || base.getHeight() % 256 != 0) {
            throw new IOException("Legacy HUD atlas must use a whole multiple of 256 pixels");
        }
        int scaleX = base.getWidth() / 256;
        int scaleY = base.getHeight() / 256;
        for (BufferedImage sprite : sprites.values()) {
            if (sprite.getWidth() % 9 != 0 || sprite.getHeight() % 9 != 0) {
                throw new IOException("HUD sprites must use a whole multiple of 9 pixels");
            }
            scaleX = lcm(scaleX, sprite.getWidth() / 9);
            scaleY = lcm(scaleY, sprite.getHeight() / 9);
        }
        BufferedImage result = new BufferedImage(256 * scaleX, 256 * scaleY, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        try {
            graphics.setComposite(AlphaComposite.Src);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            graphics.drawImage(base, 0, 0, result.getWidth(), result.getHeight(), null);
            for (Slot slot : SLOTS) {
                BufferedImage sprite = sprites.get(slot.sprite());
                if (sprite != null) graphics.drawImage(sprite, slot.x() * scaleX, slot.y() * scaleY,
                        9 * scaleX, 9 * scaleY, null);
            }
        } finally {
            graphics.dispose();
        }
        return result;
    }

    private static int lcm(int a, int b) {
        int x = a;
        int y = b;
        while (y != 0) {
            int remainder = x % y;
            x = y;
            y = remainder;
        }
        return a / x * b;
    }
}
