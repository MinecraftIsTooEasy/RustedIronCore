package moddedmite.rustedironcore.api.block;

import net.minecraft.Block;
import net.minecraft.BlockWall;
import net.minecraft.Icon;

public class WallBlock extends BlockWall {
    Block block;

    public WallBlock(int par1, Block par2Block) {
        super(par1, par2Block);
        this.block = par2Block;
    }

    public Icon getIcon(int par1, int par2) {
        return this.block.getBlockTextureFromSide(par1);
    }

    public int getBlockSubtypeUnchecked(int metadata) {
        return 0;
    }
}
