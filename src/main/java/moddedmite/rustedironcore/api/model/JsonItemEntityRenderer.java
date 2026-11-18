package moddedmite.rustedironcore.api.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Block;
import net.minecraft.EntityItem;
import net.minecraft.ItemStack;
import net.minecraft.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.List;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class JsonItemEntityRenderer {

    private static final Random RANDOM = new Random();

    public static boolean render(EntityItem entity, double x, double y, double z, float partialTick) {
        ItemStack stack = entity.getEntityItem();
        if (stack == null || stack.getItem() == null) return false;

        JsonBlockModelManager manager = JsonBlockModelManager.INSTANCE;
        BuiltInEntityRenderer builtInRenderer = manager.getBuiltInRenderer(stack);
        List<BlockModelSelection> selections = manager.getItemModelSelections(stack, JsonModelDisplayContext.GROUND)
                .stream().filter(selection -> canRender(selection.model(), builtInRenderer)).toList();
        if (selections.isEmpty()) return false;

        JsonBlockModel model = selections.get(0).model();
        JsonDisplayTransform ground = model.getDisplayTransform(JsonModelDisplayContext.GROUND);
        float scaleY = ground == null ? 1.0F : ground.getScale()[1];
        boolean threeDimensional = !model.isGeneratedItem();
        int count = getRenderAmount(stack.stackSize);
        float age = entity.age + partialTick;
        float bob = MathHelper.sin(age / 10.0F + entity.hoverStart) * 0.1F + 0.1F;
        RANDOM.setSeed(stack.itemID + stack.getItemDamage());

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_TEXTURE_BIT);
        GL11.glPushMatrix();
        JsonModelRenderContext.push(JsonModelDisplayContext.GROUND);
        try {
            GL11.glTranslated(x, y + bob + 0.25F * scaleY, z);
            GL11.glRotatef((age / 20.0F + entity.hoverStart) * (180.0F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (!threeDimensional) GL11.glTranslatef(0.0F, 0.0F, -0.09375F * (count - 1) * 0.5F);

            Block block = stack.isBlock() ? stack.getItemAsBlock().getBlock() : null;
            for (int index = 0; index < count; index++) {
                GL11.glPushMatrix();
                try {
                    if (index > 0) {
                        float offsetX = (RANDOM.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float offsetY = (RANDOM.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        if (threeDimensional) {
                            float offsetZ = (RANDOM.nextFloat() * 2.0F - 1.0F) * 0.15F;
                            GL11.glTranslatef(offsetX, offsetY, offsetZ);
                        } else {
                            GL11.glTranslatef(offsetX * 0.5F, offsetY * 0.5F, 0.0F);
                        }
                    }
                    for (BlockModelSelection selection : selections) {
                        renderModel(selection, stack, block, builtInRenderer);
                    }
                } finally {
                    GL11.glPopMatrix();
                }
                if (!threeDimensional) GL11.glTranslatef(0.0F, 0.0F, 0.09375F);
            }
        } finally {
            JsonModelRenderContext.pop();
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
        return true;
    }

    private static boolean canRender(JsonBlockModel model, BuiltInEntityRenderer builtInRenderer) {
        return model.isBuiltInEntity() ? builtInRenderer != null
                : model.isGeneratedItem() || !model.getElements().isEmpty();
    }

    private static void renderModel(BlockModelSelection selection, ItemStack stack, Block block,
                                    BuiltInEntityRenderer builtInRenderer) {
        JsonBlockModel model = selection.model();
        if (model.isBuiltInEntity()) {
            JsonBlockModelRenderer.renderBuiltInEntityAsBlockItem(model, stack, builtInRenderer, JsonModelDisplayContext.GROUND);
        } else if (block != null && !model.isGeneratedItem()) {
            JsonBlockModelRenderer.renderBlockAsItem(model, selection.variant(), block, stack, 1.0F, JsonModelDisplayContext.GROUND);
        } else {
            JsonBlockModelRenderer.renderItem(model, stack, stack.getIconIndex(), JsonModelDisplayContext.GROUND);
        }
    }

    private static int getRenderAmount(int count) {
        if (count > 48) return 5;
        if (count > 32) return 4;
        if (count > 16) return 3;
        return count > 1 ? 2 : 1;
    }
}
