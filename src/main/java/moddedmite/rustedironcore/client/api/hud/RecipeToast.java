package moddedmite.rustedironcore.client.api.hud;

import moddedmite.rustedironcore.api.chat.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class RecipeToast implements Toast {

    private static final ResourceLocation BACKGROUND = new ResourceLocation("ric", "textures/gui/sprites/toast/recipe.png", false);
    private static final TextComponent TITLE = TextComponent.translatable("ric.hud.recipe.title");
    private static final TextComponent DESCRIPTION = TextComponent.translatable("ric.hud.recipe.description");
    private final List<RecipeToastEntry> recipes = new ArrayList<>();
    private long lastChanged;
    private boolean changed;

    public RecipeToast(ItemStack symbol, ItemStack result) {
        recipes.add(new RecipeToastEntry(symbol, result));
    }

    public void addRecipe(ItemStack symbol, ItemStack result) {
        recipes.add(new RecipeToastEntry(symbol, result));
        changed = true;
    }

    @Override
    public boolean update(Toast incoming) {
        if (!(incoming instanceof RecipeToast recipeToast)) return false;
        recipes.addAll(recipeToast.recipes);
        changed = true;
        return true;
    }

    @Override
    public ToastVisibility render(HudGraphics graphics, ToastManager manager, long visibleMillis) {
        if (changed) {
            lastChanged = visibleMillis;
            changed = false;
        }
        graphics.blit(BACKGROUND, 0, 0, 0, 0, width(), height(), 160, 32);
        graphics.drawString(TITLE, 30, 7, -11534256, false);
        graphics.drawString(DESCRIPTION, 30, 18, 0xFF000000, false);
        double duration = 5000 * manager.getNotificationDisplayTimeMultiplier();
        RecipeToastEntry recipe = recipes.get((int) (visibleMillis / Math.max(1, duration / recipes.size()) % recipes.size()));
        graphics.pushPose();
        graphics.scale(0.6F, 0.6F, 1);
        graphics.renderItem(recipe.symbol(), 3, 3);
        graphics.popPose();
        graphics.renderItem(recipe.result(), 8, 8);
        return visibleMillis - lastChanged >= duration ? ToastVisibility.HIDE : ToastVisibility.SHOW;
    }
}
