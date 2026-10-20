package moddedmite.rustedironcore.core;

import moddedmite.rustedironcore.mixins.interfaces.BlockInstanceImpl;
import moddedmite.rustedironcore.mixins.interfaces.ItemInstanceImpl;
import moddedmite.rustedironcore.resources.ResourceKey;
import moddedmite.rustedironcore.tags.TagKey;
import moddedmite.rustedironcore.world.item.ItemInstance;
import net.minecraft.Block;
import net.minecraft.ItemStack;

import java.util.stream.Stream;

public interface TypedInstance<T> {
    Holder<T> typeHolder();

    default Stream<TagKey<T>> tags() {
        return this.typeHolder().tags();
    }

    default boolean is(final TagKey<T> tag) {
        return this.typeHolder().is(tag);
    }

    default boolean is(final HolderSet<T> set) {
        return set.contains(this.typeHolder());
    }

    default boolean is(final T rawType) {
        return this.typeHolder().value() == rawType;
    }

    default boolean is(final Holder<T> type) {
        return this.typeHolder() == type;
    }

    default boolean is(final ResourceKey<T> type) {
        return this.typeHolder().is(type);
    }

    static ItemInstance castItem(ItemStack stack) {
        return ItemInstanceImpl.cast(stack);
    }

    static TypedInstance<Block> castBlock(Block block) {
        return BlockInstanceImpl.cast(block);
    }
}
