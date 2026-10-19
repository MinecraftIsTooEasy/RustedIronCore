package moddedmite.rustedironcore.data.tags;

import moddedmite.rustedironcore.core.HolderLookup;
import moddedmite.rustedironcore.core.registries.Registries;
import moddedmite.rustedironcore.references.BlockItemId;
import moddedmite.rustedironcore.references.ItemIds;
import moddedmite.rustedironcore.resources.ResourceKey;
import moddedmite.rustedironcore.tags.ItemTags;
import moddedmite.rustedironcore.tags.TagKey;
import net.minecraft.Item;

public class VanillaItemTagsProvider extends TagsProvider<Item> {
    public VanillaItemTagsProvider() {
        super(Registries.ITEM);
    }

    protected BlockItemTagAppender<Item> tag(final TagKey<Item> tag) {
        return new BlockItemTagAppender<>(super.tag(tag)) {
            @Override
            protected ResourceKey<Item> convertElement(final BlockItemId element) {
                return element.item();
            }
        };
    }

    @Override
    protected void addTags(final HolderLookup.Provider registries) {
        new VanillaBlockItemTagsProvider(tagId -> BlockItemTagsProvider.wrapForItems(this.tag(tagId.item()))).run();
        this.tag(ItemTags.BOATS).add(ItemIds.from(Item.boat));
        this.tag(ItemTags.EGGS).add(ItemIds.from(Item.egg));
        this.tag(ItemTags.FISHES).add(
                ItemIds.from(Item.fishRaw),
                ItemIds.from(Item.fishCooked),
                ItemIds.from(Item.fishLargeRaw),
                ItemIds.from(Item.fishLargeCooked)
        );
        this.tag(ItemTags.COALS).add(ItemIds.from(Item.coal));
        // TODO
        this.tag(ItemTags.IRON_TOOL_MATERIALS).add(ItemIds.from(Item.ingotIron));
        // TODO
        this.tag(ItemTags.REPAIRS_CHAIN_ARMOR).add(ItemIds.from(Item.ingotIron));
        this.tag(ItemTags.REPAIRS_IRON_ARMOR).add(ItemIds.from(Item.ingotIron));
        // TODO
    }
}
