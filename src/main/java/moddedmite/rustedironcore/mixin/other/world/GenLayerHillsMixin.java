package moddedmite.rustedironcore.mixin.other.world;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.BiomeGenBase;
import net.minecraft.GenLayer;
import net.minecraft.GenLayerHills;
import net.minecraft.IntCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(GenLayerHills.class)
public abstract class GenLayerHillsMixin extends GenLayer {
    public GenLayerHillsMixin(long par1) {
        super(par1);
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    public int[] getInts(int par1, int par2, int par3, int par4, int z) {
        int[] var5 = this.parent.getInts(par1 - 1, par2 - 1, par3 + 2, par4 + 2, z);
        int[] var6 = IntCache.getIntCache(par3 * par4);
        int par3_plus_2 = par3 + 2;
        for (int var7 = 0; var7 < par4; ++var7) {
            for (int var8 = 0; var8 < par3; ++var8) {
                long par1_1 = var8 + par1;
                long par3_1 = var7 + par2;
                this.chunkSeed = this.worldGenSeed;
                this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
                this.chunkSeed += par1_1;
                this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
                this.chunkSeed += par3_1;
                this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
                this.chunkSeed += par1_1;
                this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
                this.chunkSeed += par3_1;
                int var9 = var5[var8 + 1 + (var7 + 1) * par3_plus_2];
                if ((this.chunkSeed >> 24) % 3L == 0L) {
                    int var10 = var9;
                    if (var9 == BiomeGenBase.desert.biomeID) {
                        var10 = BiomeGenBase.desertHills.biomeID;
                    } else if (var9 == BiomeGenBase.forest.biomeID) {
                        var10 = BiomeGenBase.forestHills.biomeID;
                    } else if (var9 == BiomeGenBase.taiga.biomeID) {
                        var10 = BiomeGenBase.taigaHills.biomeID;
                    } else if (var9 == BiomeGenBase.plains.biomeID) {
                        var10 = BiomeGenBase.forest.biomeID;
                    } else if (var9 == BiomeGenBase.icePlains.biomeID) {
                        var10 = BiomeGenBase.iceMountains.biomeID;
                    } else if (var9 == BiomeGenBase.jungle.biomeID) {
                        var10 = BiomeGenBase.jungleHills.biomeID;
                    }
                    int i = Handlers.BiomeGenerate.onLayerHills(this, var9);
                    if (i != var9) {
                        var10 = i;
                    }
                    if (var10 == var9) {
                        var6[var8 + var7 * par3] = var9;
                    } else {
                        int var11 = var5[var8 + 1 + (var7 + 1 - 1) * par3_plus_2];
                        int var12 = var5[var8 + 1 + 1 + (var7 + 1) * par3_plus_2];
                        int var13 = var5[var8 + 1 - 1 + (var7 + 1) * par3_plus_2];
                        int var14 = var5[var8 + 1 + (var7 + 1 + 1) * par3_plus_2];
                        var6[var8 + var7 * par3] = var11 == var9 && var12 == var9 && var13 == var9 && var14 == var9 ? var10 : var9;
                    }
                } else {
                    var6[var8 + var7 * par3] = var9;
                }
                this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
                this.chunkSeed += this.worldGenSeed;
            }
        }
        return var6;
    }
}
