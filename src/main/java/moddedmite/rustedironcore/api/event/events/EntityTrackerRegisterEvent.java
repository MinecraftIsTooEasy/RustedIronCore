package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.EntityTrackerHandler;
import moddedmite.rustedironcore.api.util.IdUtilExtra;
import net.minecraft.Entity;
import net.minecraft.Packet;
import net.minecraft.Packet23VehicleSpawn;

import java.util.function.Function;
import java.util.function.Predicate;

public class EntityTrackerRegisterEvent {
    public void registerEntityTracker(Predicate<Entity> predicate, int blocksDistanceThreshold, int updateFrequency) {
        registerEntityTracker(predicate, blocksDistanceThreshold, updateFrequency, false);
    }

    public void registerEntityTracker(Predicate<Entity> predicate, int blocksDistanceThreshold, int updateFrequency, boolean sendVelocityUpdates) {
        Handlers.EntityTracker.trackerEntryMap.put(predicate, new EntityTrackerHandler.EntityTrackerEntry(blocksDistanceThreshold, updateFrequency, sendVelocityUpdates));
    }

    public void registerEntityPacket(Predicate<Entity> predicate, EntityTrackerHandler.EntitySupplier supplier) {
        int type = IdUtilExtra.getNextPacket23Type();
        registerEntityPacket(predicate, type, x -> new Packet23VehicleSpawn(x, type), supplier);
    }

    public void registerEntityPacket(Predicate<Entity> predicate, int type, Function<Entity, Packet> packetSupplier, EntityTrackerHandler.EntitySupplier supplier) {
        Handlers.EntityTracker.packetWriterMap.put(predicate, packetSupplier);
        Handlers.EntityTracker.packetReaderMap.put(type, supplier);
    }

}
