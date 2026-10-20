package moddedmite.rustedironcore.world.item.crafting;

import moddedmite.rustedironcore.util.ByIdMap;
import moddedmite.rustedironcore.util.StringRepresentable;

import java.util.function.IntFunction;

public enum CraftingBookCategory implements StringRepresentable {
    BUILDING("building", 0),
    REDSTONE("redstone", 1),
    EQUIPMENT("equipment", 2),
    MISC("misc", 3);

    //	public static final Codec<CraftingBookCategory> CODEC = StringRepresentable.fromEnum(CraftingBookCategory::values);
    public static final IntFunction<CraftingBookCategory> BY_ID = ByIdMap.continuous(CraftingBookCategory::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    //	public static final StreamCodec<ByteBuf, CraftingBookCategory> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, CraftingBookCategory::id);
    private final String name;
    private final int id;

    CraftingBookCategory(final String name, final int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private int id() {
        return this.id;
    }
}
