package moddedmite.rustedironcore.mixin.network;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.EntityTrackerHandler;
import moddedmite.rustedironcore.network.PacketByteBuf;
import moddedmite.rustedironcore.network.PacketReader;
import moddedmite.rustedironcore.network.PacketSupplier;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.Optional;

@Mixin(NetClientHandler.class)
public abstract class NetClientHandlerMixin {
    @Shadow
    public Minecraft mc;

    @Shadow
    private WorldClient worldClient;

    @Shadow
    protected abstract Entity getEntityByID(int par1);

    @Inject(method = "handleCustomPayload", at = @At("RETURN"))
    private void handleCustomPayload(Packet250CustomPayload payload, CallbackInfo ci) {
        PacketSupplier packetSupplier = PacketReader.clientReaders.get(payload.channel);
        if (packetSupplier != null) {
            if (payload.data == null) {
                payload.data = new byte[0];
            }
            packetSupplier.readPacket(PacketByteBuf.in(new DataInputStream(new ByteArrayInputStream(payload.data)))).apply(this.mc.thePlayer);
        }
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void onClientLoggedIn(Packet1Login par1Packet1Login, CallbackInfo ci) {
        Handlers.Connection.onClientLoggedIn((NetClientHandler) (Object) this, par1Packet1Login);
    }

    @Inject(method = "quitWithPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/INetworkManager;addToSendQueue(Lnet/minecraft/Packet;)V"))
    private void onClientQuit(Packet par1Packet, CallbackInfo ci) {
        Handlers.Connection.onClientQuit((NetClientHandler) (Object) this, (Packet255KickDisconnect) par1Packet);
    }

    @Inject(method = "handleVehicleSpawn", at = @At("HEAD"), cancellable = true)
    private void addEntityReaders(Packet23VehicleSpawn par1Packet23VehicleSpawn, CallbackInfo ci) {
        Optional<EntityTrackerHandler.EntitySupplier> match = Handlers.EntityTracker.matchPacket(par1Packet23VehicleSpawn.type);
        if (match.isEmpty()) return;
        ci.cancel();
        double var2;
        double var4;
        double var6;
        if (par1Packet23VehicleSpawn.position_set_using_unscaled_integers) {
            var2 = par1Packet23VehicleSpawn.unscaled_pos_x;
            var4 = par1Packet23VehicleSpawn.unscaled_pos_y;
            var6 = par1Packet23VehicleSpawn.unscaled_pos_z;
        } else {
            var2 = SpatialScaler.getPosX(par1Packet23VehicleSpawn.scaled_pos_x);
            var4 = SpatialScaler.getPosY(par1Packet23VehicleSpawn.scaled_pos_y);
            var6 = SpatialScaler.getPosZ(par1Packet23VehicleSpawn.scaled_pos_z);
        }

        Entity var8 = match.get().getEntity(this.worldClient, var2, var4, var6, par1Packet23VehicleSpawn);

        var8.rotationYaw = SpatialScaler.getRotation(par1Packet23VehicleSpawn.scaled_yaw);
        var8.rotationPitch = SpatialScaler.getRotation(par1Packet23VehicleSpawn.scaled_pitch);
        if (var8 instanceof EntityBoat) {
            var8.setPositionAndRotation2(var8.posX, var8.posY, var8.posZ, var8.rotationYaw, var8.rotationPitch, 3);
            var8.prevRotationYaw = var8.rotationYaw;
        }

        Entity[] var12 = var8.getParts();
        if (var12 != null) {
            int var10 = par1Packet23VehicleSpawn.entityId - var8.entityId;

            for (int var11 = 0; var11 < var12.length; ++var11) {
                var12[var11].entityId += var10;
            }
        }

        var8.entityId = par1Packet23VehicleSpawn.entityId;
        this.worldClient.addEntityToWorld(par1Packet23VehicleSpawn.entityId, var8);
        if (par1Packet23VehicleSpawn.throwerEntityId > 0) {
            if (par1Packet23VehicleSpawn.type == 60) {
                Entity var13 = getEntityByID(par1Packet23VehicleSpawn.throwerEntityId);
                if (var13 instanceof EntityLivingBase) {
                    EntityArrow var14 = (EntityArrow) var8;
                    var14.shootingEntity = var13;
                }

                var8.setVelocity(par1Packet23VehicleSpawn.exact_motion_x, par1Packet23VehicleSpawn.exact_motion_y, par1Packet23VehicleSpawn.exact_motion_z);
                return;
            }

            var8.setVelocity(par1Packet23VehicleSpawn.approx_motion_x, par1Packet23VehicleSpawn.approx_motion_y, par1Packet23VehicleSpawn.approx_motion_z);
        }

    }


    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    public void handleTileEntityData(Packet132TileEntityData par1Packet132TileEntityData) {
        TileEntity var2;
        if (this.mc.theWorld.blockExists(par1Packet132TileEntityData.xPosition, par1Packet132TileEntityData.yPosition, par1Packet132TileEntityData.zPosition) && (var2 = this.mc.theWorld.getBlockTileEntity(par1Packet132TileEntityData.xPosition, par1Packet132TileEntityData.yPosition, par1Packet132TileEntityData.zPosition)) != null) {
            if (par1Packet132TileEntityData.actionType == 1 && var2 instanceof TileEntityMobSpawner) {
                var2.readFromNBT(par1Packet132TileEntityData.data);
            } else if (par1Packet132TileEntityData.actionType == 2 && var2 instanceof TileEntityCommandBlock) {
                var2.readFromNBT(par1Packet132TileEntityData.data);
            } else if (par1Packet132TileEntityData.actionType == 3 && var2 instanceof TileEntityBeacon) {
                var2.readFromNBT(par1Packet132TileEntityData.data);
            } else if (par1Packet132TileEntityData.actionType == 4 && var2 instanceof TileEntitySkull) {
                var2.readFromNBT(par1Packet132TileEntityData.data);
            } else if (Handlers.TileEntityData.shouldRead(par1Packet132TileEntityData.actionType, var2))
                var2.readFromNBT(par1Packet132TileEntityData.data);
        }
    }
}
