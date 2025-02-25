package moddedmite.rustedironcore.internal.event.listeners;

import moddedmite.rustedironcore.api.event.events.DimensionRegisterEvent;
import moddedmite.rustedironcore.api.world.Dimension;
import moddedmite.rustedironcore.api.world.DimensionContext;
import moddedmite.rustedironcore.internal.world.WorldProviderRIC;

import java.util.function.Consumer;

public class DimensionRegistry implements Consumer<DimensionRegisterEvent> {
    @Override
    public void accept(DimensionRegisterEvent event) {
        int dimensionID = event.getNextDimensionID();
        event.register(new Dimension("test", dimensionID), new DimensionContext(
                () -> new WorldProviderRIC(dimensionID, "test"),
                true,
                false
        ));
    }
}
