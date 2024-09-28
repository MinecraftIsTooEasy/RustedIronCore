package moddedmite.rustedironcore.api.event.listener;

import moddedmite.rustedironcore.property.FloatProperty;
import net.minecraft.Material;

public interface IArrowRegisterListener {
    void onRegister(FloatProperty<Material> arrowRecoveryChance);
}
