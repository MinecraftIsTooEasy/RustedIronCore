package moddedmite.rustedironcore.network;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.EntityClientPlayerMP;
import net.minecraft.NetClientHandler;
import net.minecraft.NetHandler;
import net.minecraft.NetServerHandler;
import net.minecraft.Packet;

public class S2CUpdateNutrition
        extends Packet {
    private int protein;
    private int phytonutrients;
    private int essential_fats;

    public S2CUpdateNutrition() {
    }

    public S2CUpdateNutrition(int phytonutrients, int protein, int essential_fats) {
        this.phytonutrients = phytonutrients;
        this.protein = protein;
        this.essential_fats = essential_fats;
    }

    public void readPacketData(DataInput dataInput) throws IOException {
        this.protein = dataInput.readInt();
        this.phytonutrients = dataInput.readInt();
        this.essential_fats = dataInput.readInt();
    }

    public void writePacketData(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.protein);
        dataOutput.writeInt(this.phytonutrients);
        dataOutput.writeInt(this.essential_fats);
    }

    public void processPacket(NetHandler netHandler) {
        if (netHandler instanceof NetServerHandler) {
            throw new IllegalCallerException();
        }
        if (netHandler instanceof NetClientHandler) {
            NetClientHandler netClientHandler = (NetClientHandler)netHandler;
            EntityClientPlayerMP clientPlayer = netClientHandler.mc.thePlayer;
            clientPlayer.setPhytonutrients(this.phytonutrients);
            clientPlayer.setProtein(this.protein);
            clientPlayer.setEssentialFats(this.essential_fats);
        }
    }

    public int getPacketSize() {
        return 12;
    }
}

