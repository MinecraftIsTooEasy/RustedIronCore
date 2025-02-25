package moddedmite.rustedironcore.mixin.dimension.util;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.DimensionHandler;
import moddedmite.rustedironcore.delegate.network.Packet4UpdateTimeDelegate;
import moddedmite.rustedironcore.delegate.network.Packet92UpdateTimeSmallDelegate;
import moddedmite.rustedironcore.network.Network;
import net.minecraft.Packet92UpdateTimeSmall;
import net.minecraft.ServerPlayer;
import net.minecraft.WorldServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Shadow
    public WorldServer[] worldServers;

    @Shadow
    private static MinecraftServer mcServer;

    @ModifyConstant(method = "loadAllWorlds", constant = @Constant(intValue = 4))
    private int addDimensions(int constant) {
        return Handlers.Dimension.getCustomDimensionsCount() + constant;
    }

    @Inject(method = "getWorldIndexForDimensionId", at = @At("HEAD"), cancellable = true)
    private static void delegateGetIndex(int dimension_id, CallbackInfoReturnable<Integer> cir) {
        DimensionHandler handler = Handlers.Dimension;
        handler.parseDimensionId(dimension_id).ifPresent(x -> cir.setReturnValue(handler.getIndex(x)));
    }

    @Inject(method = "getWorldDimensionIdFromIndex", at = @At("HEAD"), cancellable = true)
    private static void delegateGetId(int index, CallbackInfoReturnable<Integer> cir) {
        Handlers.Dimension.parseIndex(index).ifPresent(x -> cir.setReturnValue(x.id()));
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    public void sendWorldAgesToAllClientsInAllDimensions() {
        if (Packet92UpdateTimeSmall.areAllWorldTotalTimesSuitable(this.worldServers)) {
            Network.sendToAllPlayers(new Packet92UpdateTimeSmallDelegate(mcServer));
        } else {
            Network.sendToAllPlayers(new Packet4UpdateTimeDelegate(mcServer));
        }
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    public void sendWorldAgesToClient(ServerPlayer player) {
        if (Packet92UpdateTimeSmall.areAllWorldTotalTimesSuitable(this.worldServers)) {
            Network.sendToClient(player, new Packet92UpdateTimeSmallDelegate(mcServer));
        } else {
            Network.sendToClient(player, new Packet4UpdateTimeDelegate(mcServer));
        }
    }
}
