package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.handler.OreGenerationHandler;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.WorldGenMinable;

public record OreGenerationRegisterEvent(OreGenerationHandler handler) {

    public void register(Dimension dimension, WorldGenMinable ore, int frequency) {
        register(dimension, ore, frequency, false);
    }

    public void register(Dimension dimension, WorldGenMinable ore, int frequency, boolean increasesWithDepth) {
        this.handler.registerOre(dimension, ore, frequency, increasesWithDepth);
    }

    /**
     * Only available for those registered through RIC, can not cancel the ores from MITE.
     */
    public void unregister(Dimension dimension, int mineId) {
        this.handler.unregisterOre(dimension, mineId);
    }

}
