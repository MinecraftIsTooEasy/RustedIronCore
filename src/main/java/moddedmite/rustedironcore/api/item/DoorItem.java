package moddedmite.rustedironcore.api.item;

import net.minecraft.Block;
import net.minecraft.BlockDoor;
import net.minecraft.ItemDoor;
import net.minecraft.Material;

import java.util.function.Supplier;

public class DoorItem extends ItemDoor {
    private final Supplier<BlockDoor> blockDoor;

    public DoorItem(int par1, Material par2Material, Supplier<BlockDoor> blockDoor) {
        super(par1, par2Material);
        this.blockDoor = blockDoor;
    }// use supplier to avoid init problems

    @Override
    public Block getBlock() {
        return this.blockDoor.get();
    }
}
