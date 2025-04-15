package moddedmite.rustedironcore.internal.delegate.network;

import net.minecraft.Packet23VehicleSpawn;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet23VehicleSpawnDelegate {
    public static void write(Packet23VehicleSpawn packet, DataOutput par1DataOutput) throws IOException {
        par1DataOutput.writeInt(packet.entityId);
        par1DataOutput.writeInt(packet.type);
        par1DataOutput.writeBoolean(packet.position_set_using_unscaled_integers);
        if (packet.position_set_using_unscaled_integers) {
            par1DataOutput.writeInt(packet.unscaled_pos_x);
            par1DataOutput.writeInt(packet.unscaled_pos_y);
            par1DataOutput.writeInt(packet.unscaled_pos_z);
        } else {
            par1DataOutput.writeInt(packet.scaled_pos_x);
            par1DataOutput.writeInt(packet.scaled_pos_y);
            par1DataOutput.writeInt(packet.scaled_pos_z);
        }
        par1DataOutput.writeByte(packet.scaled_pitch);
        par1DataOutput.writeByte(packet.scaled_yaw);
        par1DataOutput.writeInt(packet.throwerEntityId);
        if (packet.throwerEntityId > 0) {
            if (packet.type == 60) {
                par1DataOutput.writeShort(packet.arrow_item_id);
                par1DataOutput.writeBoolean(packet.launcher_was_enchanted);
                par1DataOutput.writeBoolean(packet.arrow_stuck_in_block);
                par1DataOutput.writeInt(packet.arrow_tile_x);
                par1DataOutput.writeInt(packet.arrow_tile_y);
                par1DataOutput.writeInt(packet.arrow_tile_z);
                par1DataOutput.writeByte(packet.arrow_in_tile);
                par1DataOutput.writeByte(packet.arrow_in_data);
                par1DataOutput.writeDouble(packet.exact_pos_x);
                par1DataOutput.writeDouble(packet.exact_pos_y);
                par1DataOutput.writeDouble(packet.exact_pos_z);
                par1DataOutput.writeDouble(packet.exact_motion_x);
                par1DataOutput.writeDouble(packet.exact_motion_y);
                par1DataOutput.writeDouble(packet.exact_motion_z);
            } else {
                par1DataOutput.writeFloat(packet.approx_motion_x);
                par1DataOutput.writeFloat(packet.approx_motion_y);
                par1DataOutput.writeFloat(packet.approx_motion_z);
            }
        }
    }

    public static void read(Packet23VehicleSpawn packet, DataInput par1DataInput) throws IOException {
        packet.entityId = par1DataInput.readInt();
        packet.type = par1DataInput.readInt();
        packet.position_set_using_unscaled_integers = par1DataInput.readBoolean();
        if (packet.position_set_using_unscaled_integers) {
            packet.unscaled_pos_x = par1DataInput.readInt();
            packet.unscaled_pos_y = par1DataInput.readInt();
            packet.unscaled_pos_z = par1DataInput.readInt();
        } else {
            packet.scaled_pos_x = par1DataInput.readInt();
            packet.scaled_pos_y = par1DataInput.readInt();
            packet.scaled_pos_z = par1DataInput.readInt();
        }
        packet.scaled_pitch = par1DataInput.readByte();
        packet.scaled_yaw = par1DataInput.readByte();
        packet.throwerEntityId = par1DataInput.readInt();
        if (packet.throwerEntityId > 0) {
            if (packet.type == 60) {
                packet.arrow_item_id = par1DataInput.readShort();
                packet.launcher_was_enchanted = par1DataInput.readBoolean();
                packet.arrow_stuck_in_block = par1DataInput.readBoolean();
                packet.arrow_tile_x = par1DataInput.readInt();
                packet.arrow_tile_y = par1DataInput.readInt();
                packet.arrow_tile_z = par1DataInput.readInt();
                packet.arrow_in_tile = par1DataInput.readUnsignedByte();
                packet.arrow_in_data = par1DataInput.readUnsignedByte();
                packet.exact_pos_x = par1DataInput.readDouble();
                packet.exact_pos_y = par1DataInput.readDouble();
                packet.exact_pos_z = par1DataInput.readDouble();
                packet.exact_motion_x = par1DataInput.readDouble();
                packet.exact_motion_y = par1DataInput.readDouble();
                packet.exact_motion_z = par1DataInput.readDouble();
                packet.approx_motion_x = (float) packet.exact_motion_x;
                packet.approx_motion_y = (float) packet.exact_motion_y;
                packet.approx_motion_z = (float) packet.exact_motion_z;
            } else {
                packet.approx_motion_x = par1DataInput.readFloat();
                packet.approx_motion_y = par1DataInput.readFloat();
                packet.approx_motion_z = par1DataInput.readFloat();
            }
        }
    }

    public static int getSize(Packet23VehicleSpawn packet) {
        int size = 27;// it was 24, but I changed the type to an int, so larger
        if (packet.type == 60) {
            size += 66;
        } else if (packet.throwerEntityId > 0) {
            size += 12;
        }
        return size;
    }
}
