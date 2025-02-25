package moddedmite.rustedironcore.mixin.network;

import moddedmite.rustedironcore.delegate.network.Packet23VehicleSpawnDelegate;
import net.minecraft.Packet23VehicleSpawn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@Mixin(Packet23VehicleSpawn.class)
public class Packet23VehicleSpawnMixin {
    @Unique
    private final Packet23VehicleSpawn instance = (((Packet23VehicleSpawn) (Object) this));

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    public void writePacketData(DataOutput par1DataOutput) throws IOException {
        Packet23VehicleSpawnDelegate.write(instance, par1DataOutput);
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    public void readPacketData(DataInput par1DataInput) throws IOException {
        Packet23VehicleSpawnDelegate.read(instance, par1DataInput);
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    public int getPacketSize() {
        return Packet23VehicleSpawnDelegate.getSize(instance);
    }


}
