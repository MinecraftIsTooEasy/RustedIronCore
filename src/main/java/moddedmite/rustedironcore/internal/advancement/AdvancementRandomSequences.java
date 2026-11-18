package moddedmite.rustedironcore.internal.advancement;

import net.minecraft.NBTTagCompound;
import net.minecraft.World;
import net.minecraft.WorldSavedData;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AdvancementRandomSequences extends WorldSavedData {

    private static final String KEY = "ric_advancement_random_sequences";
    private final Map<String, AdvancementRandomSequence> sequences = new HashMap<>();

    public AdvancementRandomSequences(String name) {
        super(name);
    }

    public static Random get(World world, String id) {
        AdvancementRandomSequences data = (AdvancementRandomSequences) world.loadItemData(AdvancementRandomSequences.class, KEY);
        if (data == null) {
            data = new AdvancementRandomSequences(KEY);
            world.setItemData(KEY, data);
        }
        return data.sequence(world.getSeed(), id);
    }

    private Random sequence(long seed, String id) {
        return sequences.computeIfAbsent(id, key -> {
            markDirty();
            try {
                byte[] hash = MessageDigest.getInstance("MD5").digest(key.getBytes(StandardCharsets.UTF_8));
                ByteBuffer bits = ByteBuffer.wrap(hash);
                return new AdvancementRandomSequence(seed ^ bits.getLong() ^ bits.getLong(), this::markDirty);
            } catch (NoSuchAlgorithmException exception) {
                throw new IllegalStateException("MD5 is unavailable", exception);
            }
        });
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        sequences.clear();
        for (Object value : data.tagMap.keySet()) {
            String id = (String) value;
            sequences.put(id, new AdvancementRandomSequence(data.getLong(id), this::markDirty));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        sequences.forEach((id, random) -> data.setLong(id, random.state()));
    }
}
