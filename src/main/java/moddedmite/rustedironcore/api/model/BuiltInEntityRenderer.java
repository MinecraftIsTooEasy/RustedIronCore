package moddedmite.rustedironcore.api.model;

import net.minecraft.EntityLivingBase;
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
 *   <li>The {@link JsonDisplayTransform} from the JSON model's
 *       {@code display} section for the current context</li>
 *   <li>The standard model-center translation used before modern custom item rendering</li>
 * </ol>
 * The renderer should apply any model-specific base pose and issue GL draw calls.
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface BuiltInEntityRenderer {
    /**
     * Render the custom entity model.
     *
     * @param stack    the item stack being rendered
     * @param context  the display context (GUI, GROUND, FIRST_PERSON, etc.)
     */
    void render(ItemStack stack, JsonModelDisplayContext context);

    /**
     * Render with the current holder when one is available. Existing renderers
     * automatically retain the original two-argument behavior.
     */
    default void render(EntityLivingBase entity, ItemStack stack, JsonModelDisplayContext context) {
        this.render(stack, context);
    }
}
