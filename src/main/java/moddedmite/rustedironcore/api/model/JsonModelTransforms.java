package moddedmite.rustedironcore.api.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;

/** Applies and reverses the resolved {@code display} transform of a JSON model. */
@Environment(EnvType.CLIENT)
public final class JsonModelTransforms {

    private static final float PIXEL_SCALE = 1.0F / 16.0F;

    public static void apply(JsonBlockModel model, JsonModelDisplayContext context) {
        JsonDisplayTransform transform = model.getDisplayTransform(context);
        if (transform == null) {
            applyDefaultTransform(context);
            return;
        }

        apply(transform);
    }

    /** Applies a resolved transform, including the standard fallback parent transforms. */
    public static void apply(JsonDisplayTransform transform) {
        float[] translation = transform.getTranslation();
        GL11.glTranslatef(
                translation[0] * PIXEL_SCALE,
                translation[1] * PIXEL_SCALE,
                translation[2] * PIXEL_SCALE);

        applyRotation(transform.getRotation());

        float[] scale = transform.getScale();
        GL11.glScalef(scale[0], scale[1], scale[2]);

        applyRotation(transform.getRightRotation());
    }

    /** Applies the exact inverse of {@link #apply(JsonBlockModel, JsonModelDisplayContext)}. */
    public static void applyInverse(JsonBlockModel model, JsonModelDisplayContext context) {
        JsonDisplayTransform transform = model.getDisplayTransform(context);
        if (transform == null) {
            applyInverseDefaultTransform(context);
            return;
        }

        float[] scale = transform.getScale();
        if (scale[0] == 0.0F || scale[1] == 0.0F || scale[2] == 0.0F) {
            throw new IllegalArgumentException("Cannot invert a JSON display transform with a zero scale component");
        }

        applyInverseRotation(transform.getRightRotation());
        GL11.glScalef(1.0F / scale[0], 1.0F / scale[1], 1.0F / scale[2]);
        applyInverseRotation(transform.getRotation());

        float[] translation = transform.getTranslation();
        GL11.glTranslatef(
                -translation[0] * PIXEL_SCALE,
                -translation[1] * PIXEL_SCALE,
                -translation[2] * PIXEL_SCALE);
    }

    private static void applyDefaultTransform(JsonModelDisplayContext context) {
        if (context == JsonModelDisplayContext.GUI) {
            GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(0.625F, 0.625F, 0.625F);
        }
    }

    private static void applyInverseDefaultTransform(JsonModelDisplayContext context) {
        if (context == JsonModelDisplayContext.GUI) {
            GL11.glScalef(1.6F, 1.6F, 1.6F);
            GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
        }
    }

    private static void applyRotation(float[] rotation) {
        GL11.glRotatef(rotation[0], 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(rotation[1], 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotation[2], 0.0F, 0.0F, 1.0F);
    }

    private static void applyInverseRotation(float[] rotation) {
        GL11.glRotatef(-rotation[2], 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-rotation[1], 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-rotation[0], 1.0F, 0.0F, 0.0F);
    }
}

