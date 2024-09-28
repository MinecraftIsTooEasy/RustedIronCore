package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.SpawnConditionHandler;
import net.minecraft.Entity;

public class SpawnConditionRegisterEvent {
    public void register(Class<? extends Entity> clazz, SpawnConditionHandler.SpawnCondition predicate) {
        Handlers.SpawnCondition.addCondition(clazz, predicate);
    }
}
