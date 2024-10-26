package moddedmite.rustedironcore.network.packets;

import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;

public class S2COpenGuiTips implements Packet {
    public static int firstEnterTipCounter = 0;

    @Override
    public void write(PacketByteBuf packetByteBuf) {

    }

    @Override
    public void apply(EntityPlayer entityPlayer) {
//        Handlers.TimedTask.registerTimedTask(20, () -> Minecraft.getMinecraft().currentScreen == null, () -> Minecraft.getMinecraft().displayGuiScreen(new GuiTips()));
        firstEnterTipCounter = 1200;
    }

    @Override
    public ResourceLocation getChannel() {
        return Network.OpenGuiTips;
    }
}
