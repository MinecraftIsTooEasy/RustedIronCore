package moddedmite.rustedironcore.api.model;

import net.minecraft.ItemStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Custom renderer for items that use {@code "parent": "builtin/entity"} in their JSON model.
 * <p>
 * When a model has no cuboid elements and matches the builtin/entity parent,
 * RIC calls this renderer instead of the normal element-based renderer.
 * The framework has already applied:
 * <ol>
 *   <li>Standard positioning transforms (slot center, item scale, etc.)</li>
 *   <li>The {@link JsonBlockModel.DisplayTransform} from the JSON model's
 *       {@code display} section for the current context</li>
 * </ol>
 * The renderer should apply any model-specific base pose and issue GL draw calls.
 */
@Environment(EnvType.CLIENT)
public interface BuiltInEntityRenderer {
    /**
     * Render the custom entity model.
     *
     * @param stack    the item stack being rendered
     * @param context  the display context (GUI, GROUND, FIRST_PERSON, etc.)
     */
    void render(ItemStack stack, JsonBlockModel.RenderContext context);
}
