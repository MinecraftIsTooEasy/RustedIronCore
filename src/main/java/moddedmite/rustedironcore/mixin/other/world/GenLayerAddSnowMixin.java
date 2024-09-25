package moddedmite.rustedironcore.mixin.other.world;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.BiomeGenBase;
import net.minecraft.GenLayer;
import net.minecraft.GenLayerAddSnow;
import net.minecraft.IntCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(GenLayerAddSnow.class)
public abstract class GenLayerAddSnowMixin extends GenLayer {
    public GenLayerAddSnowMixin(long par1) {
        super(par1);
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    public int[] getInts(int par1, int par2, int par3, int par4, int z) {
        int var5 = par1 - 1;
        int var6 = par2 - 1;
        int var7 = par3 + 2;
        int var8 = par4 + 2;
        int[] var9 = this.parent.getInts(var5, var6, var7, var8, z);
        int[] var10 = IntCache.getIntCache(par3 * par4);
        for (int var11 = 0; var11 < par4; ++var11) {
            for (int var12 = 0; var12 < par3; ++var12) {
                int var13 = var9[var12 + 1 + (var11 + 1) * var7];
                this.initChunkSeed(var12 + par1, var11 + par2);
                if (var13 == 0) {
                    var10[var12 + var11 * par3] = 0;
                    continue;
                }
                int var14 = this.nextInt(5);
                var14 = var14 == 0 ? BiomeGenBase.icePlains.biomeID : 1;
                var14 = Handlers.BiomeGenerate.onLayerAddSnow(this, var14);
                var10[var12 + var11 * par3] = var14;
            }
        }
        return var10;
    }
}
