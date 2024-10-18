package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.EntityTrackerRegisterEvent;
import net.minecraft.Entity;
import net.minecraft.Packet;
import net.minecraft.Packet23VehicleSpawn;
import net.minecraft.WorldClient;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class EntityTrackerHandler extends EventHandler<EntityTrackerRegisterEvent> {
    public Map<Predicate<Entity>, EntityTrackerEntry> trackerEntryMap = new HashMap<>();

    public Map<Predicate<Entity>, Function<Entity, Packet>> packetWriterMap = new HashMap<>();

    public Map<Integer, EntitySupplier> packetReaderMap = new HashMap<>();

    public Optional<EntityTrackerEntry> matchEntityTracker(Entity entity) {
        return this.trackerEntryMap.keySet().stream().filter(x -> x.test(entity))
                .findFirst().map(x -> this.trackerEntryMap.get(x));
    }

    public Optional<Packet> matchEntity(Entity entity) {
        return this.packetWriterMap.keySet().stream().filter(x -> x.test(entity))
                .findFirst().map(x -> this.packetWriterMap.get(x).apply(entity));
    }

    public Optional<EntitySupplier> matchPacket(int type) {
        return Optional.ofNullable(packetReaderMap.get(type));
    }

    @FunctionalInterface
    public interface EntitySupplier {
        Entity getEntity(WorldClient world, double x, double y, double z, Packet23VehicleSpawn packet);
    }

    public record EntityTrackerEntry(int blocksDistanceThreshold, int updateFrequency, boolean sendVelocityUpdates) {
    }
}
