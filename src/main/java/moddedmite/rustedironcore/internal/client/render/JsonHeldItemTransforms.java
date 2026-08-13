package moddedmite.rustedironcore.internal.client.render;

import moddedmite.rustedironcore.api.model.JsonBlockModel;
import net.minecraft.AbstractClientPlayer;
import net.minecraft.Block;
import net.minecraft.EntityLivingBase;
import net.minecraft.EnumItemInUseAction;
import net.minecraft.Item;
import net.minecraft.ItemBlock;
import net.minecraft.ItemBow;
import net.minecraft.ItemStack;
import net.minecraft.RenderBlocks;
import org.lwjgl.opengl.GL11;

/** Converts MITE's held-item suffix into the modern item-display pipeline. */
public final class JsonHeldItemTransforms {

    public static void apply(EntityLivingBase entity, ItemStack stack, JsonBlockModel.RenderContext context) {
        if (stack == null || stack.getItem() == null) {
            return;
        }
        if (context == JsonBlockModel.RenderContext.FIRST_PERSON_RIGHT_HAND) {
            replaceFirstPersonSuffix(stack);
        } else if (context == JsonBlockModel.RenderContext.THIRD_PERSON_RIGHT_HAND) {
            replaceThirdPersonSuffix(entity, stack);
        }
    }

    private static void replaceFirstPersonSuffix(ItemStack stack) {
        Item item = stack.getItem();
        if (item.shouldRotateAroundWhenRendering()) {
            GL11.glRotatef(-180.0F, 0.0F, 1.0F, 0.0F);
        }

        // MITE ends its arm/attack transform with S(0.4). Modern rendering ends
        // the same transform with Ry(-45), then applies the JSON display matrix.
        GL11.glScalef(2.5F, 2.5F, 2.5F);
        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
    }

    private static void replaceThirdPersonSuffix(EntityLivingBase entity, ItemStack stack) {
        undoThirdPersonItemSuffix(entity, stack);

        // Undo the legacy offset after bipedRightArm.postRender.
        GL11.glTranslatef(1.0F / 16.0F, -7.0F / 16.0F, -1.0F / 16.0F);

        // Modern ItemInHandLayer suffix before the JSON display transform.
        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(1.0F / 16.0F, 2.0F / 16.0F, -10.0F / 16.0F);
    }

    private static void undoThirdPersonItemSuffix(EntityLivingBase entity, ItemStack stack) {
        Item item = stack.getItem();
        Block block = item instanceof ItemBlock
                ? ((ItemBlock) item).getBlock()
                : stack.itemID >= 0 && stack.itemID < Block.blocksList.length
                ? Block.blocksList[stack.itemID] : null;

        if (block != null && RenderBlocks.renderItemIn3d(block.getRenderType())) {
            undoBlockItemSuffix();
        } else if (item instanceof ItemBow) {
            undoBowSuffix();
        } else if (item.isFull3D()) {
            undoFull3dSuffix(entity, stack);
        } else {
            undoOrdinaryItemSuffix();
        }
    }

    private static void undoBlockItemSuffix() {
        // Inverse of T(0,3/16,-5/16) * Rx(20) * Ry(45) * S(-3/8,-3/8,3/8).
        GL11.glScalef(-8.0F / 3.0F, -8.0F / 3.0F, 8.0F / 3.0F);
        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(0.0F, -3.0F / 16.0F, 5.0F / 16.0F);
    }

    private static void undoBowSuffix() {
        // Inverse of T(0,2/16,5/16) * Ry(-20) * S(5/8,-5/8,5/8)
        // * Rx(-100) * Ry(45).
        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(100.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(1.6F, -1.6F, 1.6F);
        GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, -2.0F / 16.0F, -5.0F / 16.0F);
    }

    private static void undoFull3dSuffix(EntityLivingBase entity, ItemStack stack) {
        // Inverse of T(0,3/16,0) * S(5/8,-5/8,5/8) * Rx(-100) * Ry(45).
        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(100.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(1.6F, -1.6F, 1.6F);
        GL11.glTranslatef(0.0F, -3.0F / 16.0F, 0.0F);

        if (usesPlayerBlockPose(entity, stack)) {
            // Inverse of T(.05,0,-.1) * Ry(-50) * Rx(-10) * Rz(-60).
            GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(10.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(-0.05F, 0.0F, 0.1F);
        }

        if (stack.getItem().shouldRotateAroundWhenRendering()) {
            // Inverse of Rz(180) * T(0,-2/16,0).
            GL11.glTranslatef(0.0F, 2.0F / 16.0F, 0.0F);
            GL11.glRotatef(-180.0F, 0.0F, 0.0F, 1.0F);
        }
    }

    private static boolean usesPlayerBlockPose(EntityLivingBase entity, ItemStack stack) {
        if (!(entity instanceof AbstractClientPlayer player) || player.getItemInUseCount() <= 0) {
            return false;
        }
        return stack.getItemInUseAction(player) == EnumItemInUseAction.BLOCK;
    }

    private static void undoOrdinaryItemSuffix() {
        // Inverse of T(4/16,3/16,-3/16) * S(3/8) * Rz(60) * Rx(-90) * Rz(20).
        GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
        GL11.glScalef(8.0F / 3.0F, 8.0F / 3.0F, 8.0F / 3.0F);
        GL11.glTranslatef(-4.0F / 16.0F, -3.0F / 16.0F, 3.0F / 16.0F);
    }
}
