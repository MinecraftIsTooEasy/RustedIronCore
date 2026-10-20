package moddedmite.rustedironcore.world.inventory;

import moddedmite.rustedironcore.world.entity.player.StackedItemContents;

@FunctionalInterface
public interface StackedContentsCompatible {
    void fillStackedContents(StackedItemContents contents);
}
