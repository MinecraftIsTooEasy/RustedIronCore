package moddedmite.rustedironcore.api.block;

import net.minecraft.Block;
import net.minecraft.BlockSlab;
import net.minecraft.Icon;
import net.minecraft.Material;

/**
 * Base class for slabs backed by a model block outside the vanilla slab table.
 */
public abstract class SlabBlock extends BlockSlab {

    private final Block modelBlock;

    protected SlabBlock(int id, Material material, Block modelBlock) {
        super(id, material);
        this.modelBlock = modelBlock;
    }

    @Override
    public final Block getModelBlock(int metadata) {
        return this.modelBlock;
    }

    @Override
    public Icon getIcon(int side, int metadata) {
        int modelMetadata = this.getModelMetadata(metadata);
        return this.modelBlock.getIcon(side, modelMetadata);
    }

    @Override
    public float getBlockHardness(int metadata) {
        return this.modelBlock.getBlockHardness(this.getModelMetadata(metadata)) * 0.5F;
    }

    @Override
    public int getMinHarvestLevel(int metadata) {
        return this.modelBlock.getMinHarvestLevel(this.getModelMetadata(metadata));
    }

    protected int getModelMetadata(int slabMetadata) {
        return this.getBlockSubtypeUnchecked(slabMetadata);
    }
}
