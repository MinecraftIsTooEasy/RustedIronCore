package moddedmite.rustedironcore.api.player;

import net.minecraft.ClientPlayer;
import net.minecraft.EntityClientPlayerMP;

public interface ClientPlayerAPI {
    default int ric$GetPhytonutrients() {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    default void ric$SetPhytonutrients(int phytonutrients) {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    default int ric$GetProtein() {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    default void ric$SetProtein(int protein) {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    default int ric$GetEssentialFats() {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    default void ric$SetEssentialFats(int essential_fats) {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    static int getPhytonutrients(ClientPlayer player) {
        return ((ClientPlayerAPI) (EntityClientPlayerMP) player).ric$GetPhytonutrients();
    }// this double cast is necessary

    static int getProtein(ClientPlayer player) {
        return ((ClientPlayerAPI) (EntityClientPlayerMP) player).ric$GetProtein();
    }// this double cast is necessary

    static int getEssentialFats(ClientPlayer player) {
        return ((ClientPlayerAPI) (EntityClientPlayerMP) player).ric$GetEssentialFats();
    }// this double cast is necessary

    default int ric$GetNutritionLimit() {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    default void ric$SetNutritionLimit(int nutrition_limit) {
        throw new IllegalStateException("Should be implemented in Mixin");
    }

    static int getNutritionLimit(ClientPlayer player) {
        return ((ClientPlayerAPI) (EntityClientPlayerMP) player).ric$GetNutritionLimit();
    }// this double cast is necessary
}
