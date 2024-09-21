package moddedmite.rustedironcore.api.player;

public interface ClientPlayerAPI {
    default int getPhytonutrients() {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    default void setPhytonutrients(int phytonutrients) {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    default int getProtein() {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    default void setProtein(int protein) {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    default int getEssentialFats() {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    default void setEssentialFats(int essential_fats) {
        throw new IllegalStateException("Should be implemented in Mixin");
    }
}
