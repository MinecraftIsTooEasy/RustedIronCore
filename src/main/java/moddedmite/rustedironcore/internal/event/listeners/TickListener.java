package moddedmite.rustedironcore.internal.event.listeners;

import moddedmite.rustedironcore.api.event.listener.ITickListener;
import moddedmite.rustedironcore.internal.client.gui.Statement;
import moddedmite.rustedironcore.internal.client.gui.TitleCycle;
import net.minecraft.Minecraft;

public class TickListener implements ITickListener {

    @Override
    public void onClientTick(Minecraft client) {
        if (Statement.firstLoginStatementCounter > 0) {
            Statement.firstLoginStatementCounter--;
        }

        TitleCycle.onClientTick();
//        Handlers.TimedTask.onTick();
    }

    @Override
    public void onRenderTick(float partialTick) {
        Statement.onRenderTick();
    }

}
