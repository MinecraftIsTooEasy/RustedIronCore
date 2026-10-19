package moddedmite.rustedironcore.data.tags;

import moddedmite.rustedironcore.core.HolderLookup;
import moddedmite.rustedironcore.core.registries.Registries;
import moddedmite.rustedironcore.references.BlockItemId;
import moddedmite.rustedironcore.references.BlockItemIds;
import moddedmite.rustedironcore.resources.ResourceKey;
import moddedmite.rustedironcore.tags.BlockTags;
import moddedmite.rustedironcore.tags.TagKey;
import net.minecraft.Block;

public class VanillaBlockTagsProvider extends TagsProvider<Block> {
    public VanillaBlockTagsProvider() {
        super(Registries.BLOCK);
    }

    protected BlockItemTagAppender<Block> tag(final TagKey<Block> tag) {
        return new BlockItemTagAppender<>(super.tag(tag)) {
            @Override
            protected ResourceKey<Block> convertElement(final BlockItemId element) {
                return element.block();
            }
        };
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        new VanillaBlockItemTagsProvider(tagId -> BlockItemTagsProvider.wrapForBlocks(this.tag(tagId.block()))).run();
        this.tag(BlockTags.MOB_INTERACTABLE_DOORS).addTag(BlockTags.WOODEN_DOORS);
        this.tag(BlockTags.OVERWORLD_NATURAL_LOGS).add(BlockItemIds.from(Block.wood));
        this.tag(BlockTags.ENDERMAN_HOLDABLE)
                .addTag(BlockTags.SMALL_FLOWERS)
                .addTag(BlockTags.DIRT)
                .addTag(BlockTags.MUD)
                .addTag(BlockTags.MOSS_BLOCKS)
                .addTag(BlockTags.GRASS_BLOCKS)
                .add(
                        BlockItemIds.from(Block.sand),
                        BlockItemIds.from(Block.gravel),
                        BlockItemIds.from(Block.mushroomBrown),
                        BlockItemIds.from(Block.mushroomRed),
                        BlockItemIds.from(Block.tnt),
                        BlockItemIds.from(Block.cactus),
                        BlockItemIds.from(Block.blockClay),
                        BlockItemIds.from(Block.pumpkin),
                        BlockItemIds.from(Block.melon)
                );

        this.tag(BlockTags.FLOWER_POTS)
                .add(BlockItemIds.from(Block.flowerPot))
                .add(BlockItemIds.from(Block.flowerPotMulti));
        // TODO
    }
}
