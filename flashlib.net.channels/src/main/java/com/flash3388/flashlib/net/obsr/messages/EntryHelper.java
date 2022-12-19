package com.flash3388.flashlib.net.obsr.messages;

import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.net.obsr.ValueType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EntryHelper {

    private EntryHelper() {}

    public static void writeValueTo(DataOutput output, Value value) throws IOException {
        writeTypeTo(output, value.getType());
        writeRawValueTo(output, value.getType(), value);
    }

    public static Value readValueFrom(DataInput input) throws IOException {
        ValueType type = readTypeFrom(input);
        Object rawValue = readRawValueFrom(input, type);
        return new Value(type, rawValue);
    }

    private static void writeTypeTo(DataOutput output, ValueType type) throws IOException {
        output.writeInt(type.ordinal());
    }

    private static void writeRawValueTo(DataOutput output, ValueType type, Object value) throws IOException {
        switch (type) {
            case EMPTY:
                break;
            case RAW:
                output.writeInt(((byte[]) value).length);
                output.write((byte[]) value);
                break;
            case BOOLEAN:
                output.writeBoolean((Boolean) value);
                break;
            case INT:
                output.writeInt((Integer) value);
                break;
            case DOUBLE:
                output.writeDouble((Double) value);
                break;
            case STRING:
                output.writeUTF((String) value);
                break;
            default:
                throw new IllegalArgumentException("unsupported type: " + type.name());
        }
    }

    private static ValueType readTypeFrom(DataInput input) throws IOException {
        int typeInt = input.readInt();
        return ValueType.values()[typeInt];
    }

    private static Object readRawValueFrom(DataInput input, ValueType type) throws IOException {
        Object value;
        switch (type) {
            case EMPTY:
                value = null;
                break;
            case RAW:
                int length = input.readInt();
                value = new byte[length];
                input.readFully((byte[]) value);
                break;
            case BOOLEAN:
                value = input.readBoolean();
                break;
            case INT:
                value = input.readInt();
                break;
            case DOUBLE:
                value = input.readDouble();
                break;
            case STRING:
                value = input.readUTF();
                break;
            default:
                throw new IllegalArgumentException("unsupported type: " + type.name());
        }

        return value;
    }
}
