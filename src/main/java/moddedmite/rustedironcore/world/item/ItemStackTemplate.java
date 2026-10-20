package moddedmite.rustedironcore.world.item;

import moddedmite.rustedironcore.annotations.NotFinished;
import moddedmite.rustedironcore.annotations.NotVanilla;
import moddedmite.rustedironcore.core.Holder;
import moddedmite.rustedironcore.core.TypedInstance;
import moddedmite.rustedironcore.mixins.interfaces.RicItemImpl;
import net.minecraft.Item;
import net.minecraft.ItemStack;

@NotFinished
@NotVanilla
public record ItemStackTemplate(Holder<Item> item, int count, int meta) implements ItemInstance {
    public ItemStackTemplate(final Item item) {
        this((RicItemImpl.cast(item)).builtInRegistryHolder(), 1, 0);
    }

    public ItemStackTemplate(final Item item, final int count) {
        this((RicItemImpl.cast(item)).builtInRegistryHolder(), count, 0);
    }

    public ItemStackTemplate(final Item item, final int count, final int meta) {
        this((RicItemImpl.cast(item)).builtInRegistryHolder(), count, meta);
    }

    public ItemStackTemplate(Holder<Item> item, int count, int meta) {
        if (count != 0) {
            this.item = item;
            this.count = count;
            this.meta = meta;
        } else {
            throw new IllegalStateException("Item must be non-empty");
        }
    }

    public static ItemStackTemplate fromNonEmptyStack(final ItemStack itemStack) {
        if (itemStack == null) {
            throw new IllegalStateException("Stack must be non-empty");
        } else {
            return fromStack(itemStack);
        }
    }

    public static ItemStackTemplate fromStack(final ItemStack itemStack) {
        ItemInstance instance = TypedInstance.castItem(itemStack);
        return new ItemStackTemplate(instance.typeHolder(), instance.count(), itemStack.getItemSubtype());
    }

    public static ItemStackTemplate fromNonEmptyStack(final ItemStack itemStack, final int newCount) {
        if (itemStack != null && newCount > 0) {
            return new ItemStackTemplate(TypedInstance.castItem(itemStack).typeHolder(), newCount, itemStack.getItemSubtype());
        } else {
            throw new IllegalStateException("Stack must be non-empty");
        }
    }

    public ItemStackTemplate withCount(final int count) {
        return this.count == count ? this : new ItemStackTemplate(this.item, count, this.meta);
    }

    public ItemStack create() {
        return this.validate(new ItemStack(this.item.value(), this.count, this.meta));
    }

    @NotVanilla
    private ItemStack validate(final ItemStack result) {
//        Optional<Error<ItemStack>> error = ItemStack.validateStrict(result).error();
//        if (error.isPresent()) {
//            LOGGER.warn("Can't create item stack with properties {}, error: {}", this, error.get().message());
//            return ItemStack.EMPTY;
//        } else {
//            return result;
//        }
        return result;
    }
//
//    public ItemStack apply(final DataComponentPatch additionalPatch) {
//        return this.apply(this.count, additionalPatch);
//    }
//
//    public ItemStack apply(final int count, final DataComponentPatch additionalPatch) {
//        ItemStack result = new ItemStack(this.item, count, additionalPatch);
//        result.applyComponents(this.components);
//        return this.validate(result);
//    }

    @Override
    public Holder<Item> typeHolder() {
        return this.item;
    }

//    @Override
//    public <T> @Nullable T get(final DataComponentType<? extends T> type) {
//        return this.components.get(this.item.components(), type);
//    }

    @Override
    public int getMaxStackSize() {
        return this.item.value().getItemStackLimit(this.meta, 0);
    }
}
