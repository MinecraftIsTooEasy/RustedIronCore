package moddedmite.rustedironcore.api.event.events;

import net.minecraft.MapGenStructureIO;
import net.minecraft.StructureComponent;
import net.minecraft.StructureStart;

public class StructureNBTRegisterEvent {
    public void registerStructureStart(Class<? extends StructureStart> clazz, String name) {
        MapGenStructureIO.func_143034_b(clazz, name);
    }

    public void registerStructureComponent(Class<? extends StructureComponent> clazz, String name) {
        MapGenStructureIO.func_143031_a(clazz, name);
    }
}
