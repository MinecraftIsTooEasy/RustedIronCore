package moddedmite.rustedironcore.api.block;

import net.minecraft.BlockBed;
import net.minecraft.BlockBreakInfo;
import net.minecraft.EntityPlayer;
import net.minecraft.EnumFace;
import net.minecraft.Item;
import net.minecraft.World;

import java.util.function.Supplier;

public class BedBlock extends BlockBed implements Bed {

    private final Supplier<Item> bedItem;

    public BedBlock(int id, Supplier<Item> bedItem) {
        super(id);
        this.bedItem = bedItem;
    }

    public Item getBedItem() {
        return bedItem.get();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float hitX, float hitY, float hitZ) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (!isBlockHeadOfBed(metadata)) {
            int direction = metadata & 3;
            x += footBlockToHeadBlockMap[direction][0];
            z += footBlockToHeadBlockMap[direction][1];
            if (world.getBlockId(x, y, z) != blockID || !isBlockHeadOfBed(world.getBlockMetadata(x, y, z))) return false;
        }
        return isBed(world, x, y, z) && activateBed(world, x, y, z, player);
    }

    @Override
    public int dropBlockAsEntityItem(BlockBreakInfo info) {
        return info.wasExploded() ? super.dropBlockAsEntityItem(info)
                : isBlockHeadOfBed(info.getMetadata()) ? 0 : dropBlockAsEntityItem(info, getBedItem());
    }

    @Override
    public int idPicked(World world, int x, int y, int z) {
        return getBedItem().itemID;
    }
}
