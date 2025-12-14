package moddedmite.rustedironcore.network;

import net.minecraft.ItemStack;
import net.minecraft.MerchantRecipeList;
import net.minecraft.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public interface PacketByteBuf {
    DataInputStream getInputStream();

    DataOutputStream getOutputStream();

    byte readByte();

    short readShort();

    ItemStack readItemStack();

    String readString();

    int readInt();

    int readVarInt();

    boolean readBoolean();

    float readFloat();

    MerchantRecipeList readMerchantRecipeList();

    double readDouble();

    long readLong();

    String readUTF();

    int readUnsignedByte();

    int readUnsignedShort();

    int read(byte[] b);

    int read(byte[] b, int off, int len);

    void readFully(byte[] b);

    void readFully(byte[] b, int off, int len);

    void writeByte(int paramInt);

    void writeShort(int paramInt);

    void writeItemStack(ItemStack paramItemStack);

    void writeString(String paramString);

    void writeInt(int paramInt);

    void writeVarInt(int paramInt);

    void writeBoolean(boolean paramBoolean);

    void writeFloat(float paramFloat);

    void writeMerchantRecipeList(MerchantRecipeList list);

    void writeDouble(double paramDouble);

    void writeLong(long paramLong);

    void writeUTF(String paramUTF);

    void write(int b);

    void write(byte[] b, int off, int len);


    static PacketByteBuf out(final DataOutputStream out) {
        return new PacketByteBuf() {
            @Override
            public DataInputStream getInputStream() {
                throw new UnsupportedOperationException();
            }

            @Override
            public DataOutputStream getOutputStream() {
                return out;
            }

            @Override
            public void writeShort(int paramInt) {
                try {
                    out.writeShort(paramInt);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

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

            public void writeMerchantRecipeList(MerchantRecipeList list) {
                try {
                    list.writeRecipiesToStream(out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void writeDouble(double paramDouble) {
                try {
                    out.writeDouble(paramDouble);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public void writeLong(long paramLong) {
                try {
                    out.writeLong(paramLong);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public void writeUTF(String paramUTF) {
                try {
                    out.writeUTF(paramUTF);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public void write(int b) {
                try {
                    out.write(b);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public void write(byte[] b, int off, int len) {
                try {
                    out.write(b);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public short readShort() {
                throw new UnsupportedOperationException();
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

            public MerchantRecipeList readMerchantRecipeList() {
                throw new UnsupportedOperationException();
            }

            @Override
            public double readDouble() {
                throw new UnsupportedOperationException();
            }

            @Override
            public long readLong() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String readUTF() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int readUnsignedByte() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int readUnsignedShort() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read(byte[] b) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read(byte[] b, int off, int len) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readFully(byte[] b) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readFully(byte[] b, int off, int len) {
                throw new UnsupportedOperationException();
            }
        };
    }

    static PacketByteBuf in(final DataInputStream in) {
        return new PacketByteBuf() {
            @Override
            public DataInputStream getInputStream() {
                return in;
            }

            @Override
            public DataOutputStream getOutputStream() {
                throw new UnsupportedOperationException();
            }

            @Override
            public short readShort() {
                try {
                    return in.readShort();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

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

            public MerchantRecipeList readMerchantRecipeList() {
                try {
                    return MerchantRecipeList.readRecipiesFromStream(in);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public double readDouble() {
                try {
                    return in.readDouble();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public long readLong() {
                try {
                    return in.readLong();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public String readUTF() {
                try {
                    return in.readUTF();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public int readUnsignedByte() {
                try {
                    return in.readUnsignedByte();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public int readUnsignedShort() {
                try {
                    return in.readUnsignedShort();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public int read(byte[] b) {
                try {
                    return in.read(b);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public int read(byte[] b, int off, int len) {
                try {
                    return in.read(b, off, len);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public void readFully(byte[] b) {
                try {
                    in.readFully(b);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public void readFully(byte[] b, int off, int len) {
                try {
                    in.readFully(b, off, len);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public void writeShort(int paramInt) {
                throw new UnsupportedOperationException();
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

            public void writeMerchantRecipeList(MerchantRecipeList list) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void writeDouble(double paramDouble) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void writeLong(long paramLong) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void writeUTF(String paramUTF) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void write(int b) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void write(byte[] b, int off, int len) {
                throw new UnsupportedOperationException();
            }
        };
    }
}
