package moddedmite.rustedironcore.core;

public interface HolderOwner<T> {
	default boolean canSerialize(final HolderOwner<T> owner) {
		return owner == this;
	}
}
