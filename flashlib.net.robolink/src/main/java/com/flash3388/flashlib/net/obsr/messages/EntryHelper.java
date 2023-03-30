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

    private static void writeRawValueTo(DataOutput output, ValueType type, Value value) throws IOException {
        switch (type) {
            case EMPTY:
                break;
            case RAW: {
                byte[] rawValue = value.getRaw(null);
                output.writeInt(rawValue.length);
                output.write(rawValue);
                break;
            }
            case BOOLEAN: {
                output.writeBoolean(value.getBoolean(false));
                break;
            }
            case INT: {
                output.writeInt(value.getInt(0));
                break;
            }
            case LONG: {
                output.writeLong(value.getLong(0));
                break;
            }
            case DOUBLE: {
                output.writeDouble(value.getDouble(0));
                break;
            }
            case STRING: {
                output.writeUTF(value.getString(null));
                break;
            }
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
            case LONG:
                value = input.readLong();
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
