package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IPlayerAttributeListener;
import net.minecraft.EntityPlayer;

public class PlayerAttributeHandler extends AbstractHandler<IPlayerAttributeListener> implements IPlayerAttributeListener {

    @Override
    public float onHealthLimitModify(EntityPlayer player, float original) {
        for (IPlayerAttributeListener listener : this.listeners) {
            original = listener.onHealthLimitModify(player, original);
        }
        return original;
    }

    @Override
    public int onLevelLimitModify(int original) {
        for (IPlayerAttributeListener listener : this.listeners) {
            original = listener.onLevelLimitModify(original);
        }
        return original;
    }

    @Override
    public int onHungerLimitModify(EntityPlayer player, int original) {
        for (IPlayerAttributeListener listener : this.listeners) {
            original = listener.onHungerLimitModify(player, original);
        }
        return original;
    }

    @Override
    public int onSaturationLimitModify(EntityPlayer player, int original) {
        for (IPlayerAttributeListener listener : this.listeners) {
            original = listener.onSaturationLimitModify(player, original);
        }
        return original;
    }

    @Override
    public int onNutritionLimitModify(EntityPlayer player, int original) {
        for (IPlayerAttributeListener listener : this.listeners) {
            original = listener.onNutritionLimitModify(player, original);
        }
        return original;
    }

    @Override
    public int onInsulinResistanceLimitModify(EntityPlayer player, int original) {
        for (IPlayerAttributeListener listener : this.listeners) {
            original = listener.onInsulinResistanceLimitModify(player, original);
        }
        return original;
    }

    @Override
    public int onNutritionInitModify(EntityPlayer player, int original) {
        for (IPlayerAttributeListener listener : this.listeners) {
            original = listener.onNutritionInitModify(player, original);
        }
        return original;
    }
}
