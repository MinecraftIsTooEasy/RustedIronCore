package moddedmite.rustedironcore.api.player;

import net.minecraft.ClientPlayer;
import net.minecraft.EntityClientPlayerMP;

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

    static int getPhytonutrients(ClientPlayer player) {
        return ((ClientPlayerAPI) (EntityClientPlayerMP) player).getPhytonutrients();
    }// this double cast is necessary

    static int getProtein(ClientPlayer player) {
        return ((ClientPlayerAPI) (EntityClientPlayerMP) player).getProtein();
    }// this double cast is necessary

    static int getEssentialFats(ClientPlayer player) {
        return ((ClientPlayerAPI) (EntityClientPlayerMP) player).getEssentialFats();
    }// this double cast is necessary

    default int getNutritionLimit() {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    default void setNutritionLimit(int nutrition_limit) {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    static int getNutritionLimit(ClientPlayer player) {
        return ((ClientPlayerAPI) (EntityClientPlayerMP) player).getNutritionLimit();
    }// this double cast is necessary
}
