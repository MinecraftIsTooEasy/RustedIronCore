package moddedmite.rustedironcore.network;

import net.minecraft.ItemStack;
import net.minecraft.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public interface PacketByteBuf {
    byte readByte();

    ItemStack readItemStack();

    String readString();

    int readInt();

    int readVarInt();

    boolean readBoolean();

    float readFloat();

    void writeByte(int paramInt);

    void writeItemStack(ItemStack paramItemStack);

    void writeString(String paramString);

    void writeInt(int paramInt);

    void writeVarInt(int paramInt);

    void writeBoolean(boolean paramBoolean);

    void writeFloat(float paramFloat);

    static PacketByteBuf out(final DataOutputStream out) {
        return new PacketByteBuf() {
            public void writeVarInt(int value) {
                while ((value & 0xFFFFFF80) != 0) {
                    writeByte(value & 0x7F | 0x80);
                    value >>>= 7;
                }
                writeByte(value);
            }

            public void writeString(String str) {
                byte[] bys = str.getBytes(StandardCharsets.UTF_8);
                writeVarInt(bys.length);
                try {
                    out.write(bys);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public void writeItemStack(ItemStack stack) {
                try {
                    Packet.writeItemStack(stack, out);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public void writeInt(int i) {
                try {
                    out.writeInt(i);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public void writeByte(int b) {
                try {
                    out.writeByte(b);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public void writeFloat(float f) {
                try {
                    out.writeFloat(f);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public void writeBoolean(boolean b) {
                writeByte(b ? 1 : 0);
            }

            public int readVarInt() {
                throw new UnsupportedOperationException();
            }

            public String readString() {
                throw new UnsupportedOperationException();
            }

            public ItemStack readItemStack() {
                throw new UnsupportedOperationException();
            }

            public int readInt() {
                throw new UnsupportedOperationException();
            }

            public byte readByte() {
                throw new UnsupportedOperationException();
            }

            public boolean readBoolean() {
                throw new UnsupportedOperationException();
            }

            public float readFloat() {
                throw new UnsupportedOperationException();
            }
        };
    }

    static PacketByteBuf in(final DataInputStream in) {
        return new PacketByteBuf() {
            public int readVarInt() {
                byte b;
                int i = 0;
                int j = 0;
                do {
                    b = readByte();
                    i |= (b & Byte.MAX_VALUE) << j++ * 7;
                    if (j > 5)
                        throw new RuntimeException("VarInt too big");
                } while ((b & 0x80) == 128);
                return i;
            }

            public String readString() {
                int len = readVarInt();
                byte[] bys = new byte[len];
                try {
                    in.readFully(bys);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                return new String(bys, StandardCharsets.UTF_8);
            }

            public ItemStack readItemStack() {
                try {
                    return Packet.readItemStack(in);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public int readInt() {
                try {
                    return in.readInt();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public byte readByte() {
                try {
                    return in.readByte();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public boolean readBoolean() {
                try {
                    return (in.readByte() != 0);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public float readFloat() {
                try {
                    return in.readFloat();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            public void writeVarInt(int value) {
                throw new UnsupportedOperationException();
            }

            public void writeString(String str) {
                throw new UnsupportedOperationException();
            }

            public void writeItemStack(ItemStack stack) {
                throw new UnsupportedOperationException();
            }

            public void writeInt(int i) {
                throw new UnsupportedOperationException();
            }

            public void writeByte(int b) {
                throw new UnsupportedOperationException();
            }

            public void writeBoolean(boolean b) {
                throw new UnsupportedOperationException();
            }

            public void writeFloat(float f) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
