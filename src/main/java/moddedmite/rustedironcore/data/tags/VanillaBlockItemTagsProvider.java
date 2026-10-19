package moddedmite.rustedironcore.data.tags;

import moddedmite.rustedironcore.references.BlockItemIds;
import moddedmite.rustedironcore.tags.BlockItemTagId;
import moddedmite.rustedironcore.tags.BlockItemTags;
import net.minecraft.Block;

import java.util.function.Function;

public class VanillaBlockItemTagsProvider extends BlockItemTagsProvider {
    protected VanillaBlockItemTagsProvider(final Function<BlockItemTagId, CombinedAppender> tagSupplier) {
        super(tagSupplier);
    }

    @Override
    public void run() {
        this.tag(BlockItemTags.WOOL).add(BlockItemIds.from(Block.cloth));
        this.tag(BlockItemTags.PLANKS).add(BlockItemIds.from(Block.planks));
        this.tag(BlockItemTags.STONE_BRICKS).add(BlockItemIds.from(Block.stoneBrick));
        this.tag(BlockItemTags.WOODEN_BUTTONS).add(BlockItemIds.from(Block.woodenButton));
        this.tag(BlockItemTags.STONE_BUTTONS).add(BlockItemIds.from(Block.stoneButton));
        this.tag(BlockItemTags.BUTTONS).addTag(BlockItemTags.WOODEN_BUTTONS).addTag(BlockItemTags.STONE_BUTTONS);
        this.tag(BlockItemTags.WOOL_CARPETS).add(BlockItemIds.from(Block.carpet));
        this.tag(BlockItemTags.WOODEN_DOORS).add(BlockItemIds.from(Block.doorWood));
        this.tag(BlockItemTags.WOODEN_STAIRS).add(
                BlockItemIds.from(Block.stairsWoodOak),
                BlockItemIds.from(Block.stairsWoodSpruce),
                BlockItemIds.from(Block.stairsWoodBirch),
                BlockItemIds.from(Block.stairsWoodJungle)
        );
        this.tag(BlockItemTags.WOODEN_SLABS).add(
                BlockItemIds.from(Block.woodSingleSlab),
                BlockItemIds.from(Block.woodDoubleSlab)
        );
        // TODO
    }
}
