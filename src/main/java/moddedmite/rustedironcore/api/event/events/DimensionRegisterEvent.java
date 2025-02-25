package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.handler.DimensionHandler;
import moddedmite.rustedironcore.api.util.IdUtilExtra;
import moddedmite.rustedironcore.api.world.Dimension;
import moddedmite.rustedironcore.api.world.DimensionContext;

public record DimensionRegisterEvent(DimensionHandler handler) {
    public int getNextDimensionID() {
        return IdUtilExtra.getNextDimensionID();
    }

    /**
     * Keep the return value, that's the key you use to access your dimension.
     */
    public Dimension register(Dimension dimension, DimensionContext context) {
        Dimension.register(dimension);
        this.handler.registerDimensionInternal(dimension, context);
        return dimension;
    }
}
