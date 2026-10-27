package moddedmite.rustedironcore.client.api.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ResourceLocation;

@Environment(EnvType.CLIENT)
public enum TutorialToastIcon {

    MOVEMENT_KEYS("movement_keys"),
    MOUSE("mouse"),
    TREE("tree"),
    RECIPE_BOOK("recipe_book"),
    WOODEN_PLANKS("wooden_planks"),
    SOCIAL_INTERACTIONS("social_interactions"),
    RIGHT_CLICK("right_click");

    private final ResourceLocation texture;

    TutorialToastIcon(String name) {
        texture = new ResourceLocation("ric", "textures/gui/sprites/toast/" + name + ".png", false);
    }

    public void render(HudGraphics graphics, int x, int y) {
        graphics.blit(texture, x, y, 0, 0, 20, 20, 20, 20);
    }
}
