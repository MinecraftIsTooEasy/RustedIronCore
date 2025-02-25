package moddedmite.rustedironcore.internal.event.listeners;

import moddedmite.rustedironcore.api.event.listener.IBiomeGenerateListener;
import net.minecraft.BiomeGenBase;

import java.util.List;

public class BiomeListener implements IBiomeGenerateListener {
    @Override
    public void onUnderworldBiomesRegister(List<BiomeGenBase> original) {
        original.add(BiomeGenBase.plains);
    }
}
