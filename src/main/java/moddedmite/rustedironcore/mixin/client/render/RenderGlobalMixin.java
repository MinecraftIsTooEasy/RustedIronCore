package moddedmite.rustedironcore.mixin.client.render;

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
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin {

    @Shadow
    private WorldClient theWorld;

    @Invoker("drawOutlinedBoundingBox")
    protected abstract void rusted_iron_core$drawOutlinedBoundingBox(AxisAlignedBB bounds);

    @Redirect(method = "drawSelectionBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/RenderGlobal;drawOutlinedBoundingBox(Lnet/minecraft/AxisAlignedBB;)V"))
    private void rusted_iron_core$drawSelectionBounds(RenderGlobal instance, AxisAlignedBB par1AxisAlignedBB, EntityPlayer par1EntityPlayer, RaycastCollision rc, int par3, float par4) {
        Block block = rc.getBlockHit();
        Object collisionBounds = block.getCollisionBounds(this.theWorld, rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, null);
        if (!(collisionBounds instanceof AxisAlignedBB[] bounds)) {
            this.rusted_iron_core$drawOutlinedBoundingBox(par1AxisAlignedBB);
            return;
        }

        double cameraX = par1EntityPlayer.lastTickPosX + (par1EntityPlayer.posX - par1EntityPlayer.lastTickPosX) * par4;
        double cameraY = par1EntityPlayer.lastTickPosY + (par1EntityPlayer.posY - par1EntityPlayer.lastTickPosY) * par4;
        double cameraZ = par1EntityPlayer.lastTickPosZ + (par1EntityPlayer.posZ - par1EntityPlayer.lastTickPosZ) * par4;
        double epsilon = 0.002D;
        for (AxisAlignedBB bound : bounds) {
            if (bound == null) continue;
            this.rusted_iron_core$drawOutlinedBoundingBox(bound.expand(epsilon, epsilon, epsilon).getOffsetBoundingBox(rc.block_hit_x - cameraX, rc.block_hit_y - cameraY, rc.block_hit_z - cameraZ));
        }
    }
}
