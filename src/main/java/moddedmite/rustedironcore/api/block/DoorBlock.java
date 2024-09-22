package moddedmite.rustedironcore.api.block;

import net.minecraft.BlockDoor;
import net.minecraft.Item;
import net.minecraft.Material;

import java.util.function.Supplier;

public class DoorBlock extends BlockDoor {
    private final Supplier<Item> doorItem;

    public DoorBlock(int par1, Material par2Material, Supplier<Item> doorItem) {
        super(par1, par2Material);
        this.doorItem = doorItem;
    }// use supplier to avoid initialize problems

    @Override
    public Item getDoorItem() {
        return this.doorItem.get();
    }
}
