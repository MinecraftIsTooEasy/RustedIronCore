package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.EntityPlayer;

public interface IPlayerAttributeListener {
    default float onHealthLimitModify(EntityPlayer player, float original) {
        return original;
    }

    default int onLevelLimitModify(int original) {
        return original;
    }

    default int onLevelMinLimitModify(int original) {
        return original;
    }

    default int onHungerLimitModify(EntityPlayer player, int original) {
        return original;
    }

    default int onSaturationLimitModify(EntityPlayer player, int original) {
        return original;
    }

    default int onNutritionLimitModify(EntityPlayer player, int original) {
        return original;
    }

    default int onInsulinResistanceLimitModify(EntityPlayer player, int original) {
        return original;
    }

    default int onNutritionInitModify(EntityPlayer player, int original) {
        return original;
    }
}
