package moddedmite.rustedironcore.api.event.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.TileEntityDataTypeRegisterEvent;
import net.minecraft.TileEntity;

public class TileEntityDataHandler extends EventHandler<TileEntityDataTypeRegisterEvent> {
    public final BiMap<Integer, Class<? extends TileEntity>> CLASS_MAP = HashBiMap.create();
    private final BiMap<Class<? extends TileEntity>, Integer> TYPE_MAP = CLASS_MAP.inverse();

    public boolean shouldRead(int type, TileEntity tileEntity) {
        return CLASS_MAP.containsKey(type) && tileEntity.getClass() == CLASS_MAP.get(type);
    }


    /**
     * You can still get your type here, to construct the Packet132TileEntityData
     */
    public int getType(TileEntity tileEntity) {
        return TYPE_MAP.get(tileEntity.getClass());
    }
}
