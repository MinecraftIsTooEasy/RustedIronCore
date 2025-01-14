package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.util.IdUtilExtra;
import net.minecraft.TileEntity;

public class TileEntityDataTypeRegisterEvent {
    public TileEntityDataTypeRegisterEvent() {
    }

    // keep your id, so you can construct the Packet132TileEntityData with your type
    public int register(Class<? extends TileEntity> clazz) {
        return register(IdUtilExtra.getNextTileEntityDataType(), clazz);
    }

    public int register(int type, Class<? extends TileEntity> clazz) {
        Handlers.TileEntityData.CLASS_MAP.put(type, clazz);
        return type;
    }
}
