package moddedmite.rustedironcore.world.item.crafting;

import moddedmite.rustedironcore.annotations.NotFinished;
import moddedmite.rustedironcore.annotations.NotVanilla;
import moddedmite.rustedironcore.core.Holder;
import moddedmite.rustedironcore.core.HolderSet;
import moddedmite.rustedironcore.core.TypedInstance;
import moddedmite.rustedironcore.mixins.interfaces.RicItemImpl;
import moddedmite.rustedironcore.world.entity.player.StackedContents;
import moddedmite.rustedironcore.world.level.ItemLike;
import net.minecraft.Block;
import net.minecraft.Item;
import net.minecraft.ItemStack;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@NotFinished
@NotVanilla
public final class Ingredient implements Predicate<ItemStack>, StackedContents.IngredientInfo<Holder<Item>> {
    private final HolderSet<Item> values;

    private Ingredient(final HolderSet<Item> values) {
//        values.unwrap().ifRight(directValues -> {
//            if (directValues.isEmpty()) {
//                throw new UnsupportedOperationException("Ingredients can't be empty");
//            }
//
//            if (directValues.contains(Items.AIR.builtInRegistryHolder())) {
//                throw new UnsupportedOperationException("Ingredient can't contain air");
//            }
//        });
        this.values = values;
    }

    public static boolean testOptionalIngredient(final Optional<Ingredient> ingredient, final ItemStack stack) {
        return ingredient.<Boolean>map(value -> value.test(stack)).orElseGet(() -> stack == null);
    }

    @Deprecated
    public Stream<Holder<Item>> items() {
        return this.values.stream();
    }

    public boolean isEmpty() {
        return this.values.size() == 0;
    }

    @NotVanilla
    public boolean test(final ItemStack input) {
        return TypedInstance.castItem(input).is(this.values);
    }

    @Override
    public boolean acceptsItem(final Holder<Item> item) {
        return this.values.contains(item);
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof Ingredient other ? Objects.equals(this.values, other.values) : false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.values);
    }

    @NotVanilla
    public static Ingredient of(final Block block) {
        return of(Item.getItem(block));
    }

    @NotVanilla
    public static Ingredient of(final Item item) {
        return new Ingredient(HolderSet.direct(RicItemImpl.cast(item).builtInRegistryHolder()));
    }

    public static Ingredient of(final ItemLike itemLike) {
        return new Ingredient(HolderSet.direct(RicItemImpl.cast(itemLike.asItem()).builtInRegistryHolder()));
    }

    public static Ingredient of(final ItemLike... items) {
        return of(Arrays.stream(items));
    }

    public static Ingredient of(final Stream<? extends ItemLike> stream) {
        return new Ingredient(HolderSet.direct(stream.map(e -> RicItemImpl.cast(e.asItem()).builtInRegistryHolder()).toList()));
    }

    public static Ingredient of(final HolderSet<Item> tag) {
        return new Ingredient(tag);
    }

//    public SlotDisplay display() {
//        return new SlotDisplay.TagSlotDisplay(this.values);
//    }
//
//    public static SlotDisplay optionalIngredientToDisplay(final Optional<Ingredient> ingredient) {
//        return ingredient.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE);
//    }
//
//    private static SlotDisplay displayForSingleItem(final Holder<Item> item) {
//        SlotDisplay inputDisplay = new SlotDisplay.ItemSlotDisplay(item);
//        ItemStackTemplate remainderStack = item.value().getCraftingRemainder();
//        if (remainderStack != null) {
//            SlotDisplay remainderDisplay = new SlotDisplay.ItemStackSlotDisplay(remainderStack);
//            return new SlotDisplay.WithRemainder(inputDisplay, remainderDisplay);
//        } else {
//            return inputDisplay;
//        }
//    }

    public Optional<Holder<Item>> getSingleItem() {
        return this.values.size() != 1 ? Optional.empty() : Optional.of(this.values.get(0));
    }
}
