package moddedmite.rustedironcore.api.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Block;
import net.minecraft.EntityLivingBase;
import net.minecraft.Icon;
import net.minecraft.ItemRenderer;
import net.minecraft.ItemStack;
import net.minecraft.Minecraft;
import net.minecraft.RenderBlocks;
import net.minecraft.ResourceLocation;
import net.minecraft.Tessellator;
import org.lwjgl.opengl.GL11;

/** Opt-in item rendering for custom arm renderers that supply a modern hand matrix. */
@Environment(EnvType.CLIENT)
public final class JsonModernHeldItemRenderer {

    private static final ResourceLocation GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final RenderBlocks BLOCK_RENDERER = new RenderBlocks();

    /** Resolved modern display only, for custom geometry that owns the model-local transform. */
    public static void applyDisplay(EntityLivingBase entity, ItemStack stack, JsonModelDisplayContext context) {
        var model = JsonBlockModelManager.INSTANCE.getItemModel(stack, entity, context);
        if (model.isPresent()) {
            JsonModelTransforms.apply(model.get(), context);
            return;
        }
        JsonModelTransforms.apply(fallbackDisplay(stack).get(context));
    }

    /** Returns the resolved Euler rotation used by this context, including the legacy fallback parent. */
    public static float[] getDisplayRotation(EntityLivingBase entity, ItemStack stack, JsonModelDisplayContext context) {
        var model = JsonBlockModelManager.INSTANCE.getItemModel(stack, entity, context);
        if (model.isPresent()) {
            JsonDisplayTransform transform = model.get().getDisplayTransform(context);
            return transform == null ? new float[]{0.0F, 0.0F, 0.0F} : transform.getRotation().clone();
        }
        JsonDisplayTransform transform = fallbackDisplay(stack).get(context);
        return transform == null ? new float[]{0.0F, 0.0F, 0.0F} : transform.getRotation().clone();
    }

    public static void render(ItemRenderer renderer, EntityLivingBase entity, ItemStack stack,
                              JsonModelDisplayContext context) {
        JsonModelRenderContext.pushModern(context);
        try {
            int passes = stack.getItem().requiresMultipleRenderPasses() ? 2 : 1;
            for (int pass = 0; pass < passes; pass++) {
                renderer.renderItem(entity, stack, pass);
            }
        } finally {
            JsonModelRenderContext.pop();
        }
    }

    /** Used only when the modern caller opted in and no JSON model owns this stack. */
    public static void renderFallback(EntityLivingBase entity, ItemStack stack, int pass,
                                      JsonModelDisplayContext context) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_CURRENT_BIT | GL11.GL_TEXTURE_BIT | GL11.GL_LIGHTING_BIT);
        try {
            Block block = stack.isBlock() ? Block.blocksList[stack.itemID] : null;
            if (block != null && RenderBlocks.renderItemIn3d(block.getRenderType())) {
                JsonModelTransforms.apply(JsonDisplayTransforms.blockDefaults(JsonDisplayTransforms.EMPTY).get(context));
                Minecraft.getMinecraft().getTextureManager().bindTexture(
                        Minecraft.getMinecraft().getTextureManager().getResourceLocation(0));
                BLOCK_RENDERER.renderBlockAsItem(block, stack.getItemSubtype(), 1);
                return;
            }
            Icon icon = ItemRenderer.render_icon_override != null ? ItemRenderer.render_icon_override : entity.getItemIcon(stack, pass);
            if (icon == null) return;
            JsonModelTransforms.apply(fallbackDisplay(stack).get(context));
            GL11.glTranslatef(-0.5f, -0.5f, 0);
            Minecraft.getMinecraft().getTextureManager().bindTexture(
                    Minecraft.getMinecraft().getTextureManager().getResourceLocation(stack.getItemSpriteNumber()));
            int color = stack.getItem().getColorFromItemStack(stack, pass);
            GL11.glColor4f((color >> 16 & 255) / 255f, (color >> 8 & 255) / 255f, (color & 255) / 255f, 1);
            ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(),
                    icon.getIconWidth(), icon.getIconHeight(), 1f / 16);
            if (stack.hasEffect() && pass == 0) renderSpriteGlint();
        } finally {
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    private static JsonDisplayTransforms fallbackDisplay(ItemStack stack) {
        Block block = stack.isBlock() ? Block.blocksList[stack.itemID] : null;
        if (block != null && RenderBlocks.renderItemIn3d(block.getRenderType())) {
            return JsonDisplayTransforms.blockDefaults(JsonDisplayTransforms.EMPTY);
        }
        return stack.getItem().isFull3D()
                ? JsonDisplayTransforms.handheldItemDefaults(JsonDisplayTransforms.EMPTY)
                : JsonDisplayTransforms.generatedItemDefaults(JsonDisplayTransforms.EMPTY);
    }

    private static void renderSpriteGlint() {
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDisable(GL11.GL_LIGHTING);
        Minecraft.getMinecraft().getTextureManager().bindTexture(GLINT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
        GL11.glColor4f(0.38f, 0.19f, 0.608f, 1);
        GL11.glMatrixMode(GL11.GL_TEXTURE);
        try {
            for (int layer = 0; layer < 2; layer++) {
                GL11.glPushMatrix();
                try {
                    GL11.glScalef(0.125f, 0.125f, 0.125f);
                    long period = layer == 0 ? 3000 : 4873;
                    float phase = (Minecraft.getSystemTime() % period) / (float) period * 8;
                    GL11.glTranslatef(layer == 0 ? phase : -phase, 0, 0);
                    GL11.glRotatef(layer == 0 ? -50 : 10, 0, 0, 1);
                    ItemRenderer.renderItemIn2D(Tessellator.instance, 0, 0, 1, 1, 256, 256, 1f / 16);
                } finally {
                    GL11.glPopMatrix();
                }
            }
        } finally {
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        }
    }
}
