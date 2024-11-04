package moddedmite.rustedironcore.api.event.listener;

import huix.glacier.api.extension.material.IArrowMaterial;
import moddedmite.rustedironcore.property.FloatProperty;
import net.minecraft.Material;

import java.util.function.Consumer;

public interface IArrowRegisterListener {
    void onRegister(FloatProperty<Material> arrowRecoveryChance);

    default <T extends Material & IArrowMaterial> void onRegister(Consumer<T> registry) {
    }
}
