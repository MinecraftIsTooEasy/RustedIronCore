package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IArmorModelListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ItemArmor;
import net.minecraft.ModelBiped;
import net.minecraft.ResourceLocation;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ArmorModelHandler extends AbstractHandler<IArmorModelListener> {
    public Optional<ResourceLocation> getArmorTexture(ItemArmor itemArmor, int slotIndex) {
        return this.listeners.stream().map(x -> x.getArmorTexture(itemArmor, slotIndex))
                .filter(Objects::nonNull)
                .findFirst();
    }

    public void onArmorModelRegister(Consumer<ModelBiped> registry) {
        this.listeners.forEach(x -> x.onArmorModelRegister(registry));
    }

    public Optional<ModelBiped> getArmorModel(ItemArmor itemArmor, int slotIndex) {
        return this.listeners.stream().map(x -> x.getArmorModel(itemArmor, slotIndex))
                .filter(Objects::nonNull)
                .findFirst();
    }
}
