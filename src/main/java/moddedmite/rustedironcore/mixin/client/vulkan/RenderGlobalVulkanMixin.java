package moddedmite.rustedironcore.mixin.client.vulkan;

import moddedmite.rustedironcore.internal.compat.NativeGlShim;
import net.minecraft.AxisAlignedBB;
import net.minecraft.Block;
import net.minecraft.EntityPlayer;
import net.minecraft.RaycastCollision;
import net.minecraft.RenderGlobal;
import net.minecraft.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * FML 4.x native route of {@code RenderGlobalMixin}: FML's native {@code drawSelectionBox} no longer calls
 * {@code drawOutlinedBoundingBox}, so a block with several collision boxes is outlined here, with MITE's selection box state.
 */
@Mixin(RenderGlobal.class)
public abstract class RenderGlobalVulkanMixin {

    @Shadow
    private WorldClient theWorld;

    @Invoker("drawOutlinedBoundingBox")
    protected abstract void rusted_iron_core$drawOutlinedBoundingBox(AxisAlignedBB bounds);

    @Inject(method = "drawSelectionBox", at = @At("HEAD"), cancellable = true)
    private void drawSelectionBounds(EntityPlayer player, RaycastCollision rc, int pass, float partialTicks, CallbackInfo ci) {
        if (pass != 0 || !rc.isBlock()) return;
        Block block = rc.getBlockHit();
        Object collisionBounds = block.getCollisionBounds(this.theWorld, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, null);
        if (!(collisionBounds instanceof AxisAlignedBB[] bounds)) return;

        NativeGlShim.enable(3042);
        NativeGlShim.blendFunc(770, 771);
        NativeGlShim.color4f(0.0F, 0.0F, 0.0F, 0.4F);
        NativeGlShim.lineWidth(2.0F);
        NativeGlShim.disable(3553);
        NativeGlShim.depthMask(false);
        double cameraX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double cameraY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double cameraZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        double epsilon = 0.002D;
        for (AxisAlignedBB bound : bounds) {
            if (bound == null) continue;
            this.rusted_iron_core$drawOutlinedBoundingBox(bound.expand(epsilon, epsilon, epsilon).getOffsetBoundingBox(rc.block_hit_x - cameraX, rc.block_hit_y - cameraY, rc.block_hit_z - cameraZ));
        }
        NativeGlShim.depthMask(true);
        NativeGlShim.enable(3553);
        NativeGlShim.disable(3042);
        ci.cancel();
    }
}
