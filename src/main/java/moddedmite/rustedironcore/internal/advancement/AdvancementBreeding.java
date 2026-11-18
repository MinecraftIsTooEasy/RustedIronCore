package moddedmite.rustedironcore.internal.advancement;

import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import net.minecraft.Entity;
import net.minecraft.EntityAnimal;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;

public class AdvancementBreeding {

    public static void born(EntityAnimal parent, EntityAnimal partner, Entity child) {
        if (parent.worldObj.isRemote) return;
        AdvancementActor first = (AdvancementActor) parent;
        AdvancementActor second = (AdvancementActor) partner;
        String name = first.ric$getAdvancementActor();
        if (name.isEmpty()) name = second.ric$getAdvancementActor();
        if (!name.isEmpty()) {
            ServerPlayer player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(name);
            if (player != null) CriteriaTriggers.bredAnimals(player, parent, partner, child);
        }
        first.ric$setAdvancementActor("");
        second.ric$setAdvancementActor("");
    }
}
