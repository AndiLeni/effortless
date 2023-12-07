package dev.huskuraft.effortless.networking;

import dev.huskuraft.effortless.core.ItemStack;
import dev.huskuraft.effortless.tag.TagRecord;
import dev.huskuraft.effortless.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public abstract class Buffer {

    public <T> T readNullable(BufferReader<T> reader) {
        if (readBoolean()) return read(reader);
        return null;
    }

    public abstract UUID readUUID();

    public abstract <T extends Enum<T>> T readEnum(Class<T> clazz);

    public abstract String readString();

    public abstract Text readText();

    public abstract boolean readBoolean();

    public abstract byte readByte();

    public abstract short readShort();

    public abstract int readInt();

    public abstract int readVarInt();

    public abstract long readLong();

    public abstract long readVarLong();

    public abstract float readFloat();

    public abstract double readDouble();

//    public abstract boolean[] readBooleanArray();
//
//    public abstract byte[] readByteArray();
//
//    public abstract short[] readShortArray();
//
//    public abstract int[] readIntArray();
//
//    public abstract long[] readLongArray();
//
//    public abstract float[] readFloatArray();
//
//    public abstract double[] readDoubleArray();

    public abstract ItemStack readItemStack();

    public abstract TagRecord readTagRecord();

    public <T> T read(BufferReader<T> reader) {
        return reader.read(this);
    }

    public <T> List<T> readCollection(BufferReader<T> reader) {
        var i = readInt();
        var collection = new ArrayList<T>();

        for (int j = 0; j < i; ++j) {
            collection.add(read(reader));
        }
        return collection;
    }

    public <T> void writeNullable(T value, BufferWriter<T> writer) {
        writeBoolean(value != null);
        if (value != null) write(value, writer);
    }

    public abstract void writeUUID(UUID uuid);

    public abstract <T extends Enum<T>> void writeEnum(Enum<T> value);

    public abstract void writeString(String value);

    public abstract void writeText(Text value);

    public abstract void writeBoolean(boolean value);

    public abstract void writeByte(byte value);

    public abstract void writeShort(short value);

    public abstract void writeInt(int value);

    public abstract void writeVarInt(int value);

    public abstract void writeLong(long value);

    public abstract void writeVarLong(long value);

    public abstract void writeFloat(float value);

    public abstract void writeDouble(double value);

//    public abstract void writeBooleanArray(boolean[] value);
//
//    public abstract void writeByteArray(byte[] value);
//
//    public abstract void writeShortArray(short[] value);
//
//    public abstract void writeIntArray(int[] value);
//
//    public abstract void writeLongArray(long[] value);
//
//    public abstract void writeFloatArray(float[] value);
//
//    public abstract void writeDoubleArray(double[] value);

    // TODO: 7/12/23 extract
    public abstract void writeItemStack(ItemStack value);

    public abstract void writeTagRecord(TagRecord value);

    public <T> void write(T value, BufferWriter<T> writer) {
        writer.write(this, value);
    }

    public <T> void writeCollection(Collection<T> collection, BufferWriter<T> writer) {
        writeInt(collection.size());
        for (var object : collection) {
            write(object, writer);
        }
    }


}