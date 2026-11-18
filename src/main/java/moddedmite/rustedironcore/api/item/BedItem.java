package moddedmite.rustedironcore.api.item;

import net.minecraft.BlockBed;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemBed;
import net.minecraft.RaycastCollision;

import java.util.function.Supplier;

public class BedItem extends ItemBed {

    private final Supplier<BlockBed> bedBlock;

    public BedItem(int id, Supplier<BlockBed> bedBlock) {
        super(id);
        this.bedBlock = bedBlock;
        setMaxStackSize(1);
    }

    public BlockBed getBlock() {
        return bedBlock.get();
    }

    @Override
    public boolean onItemRightClick(EntityPlayer player, float partialTick, boolean ctrlIsDown) {
        RaycastCollision hit = player.getSelectedObject(partialTick, true);
        return hit != null && hit.isBlock() && player.tryPlaceHeldItemAsBlock(hit, getBlock());
    }
}
