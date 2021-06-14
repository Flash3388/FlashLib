package com.flash3388.flashlib.vision.analysis;

import com.castle.reflect.Types;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class FakeAnalysis implements Analysis {

    private final List<FakeTarget> mTargets;
    private final Map<String, Object> mProperties;

    public FakeAnalysis(List<FakeTarget> targets, Map<String, Object> properties) {
        mTargets = targets;
        mProperties = properties;
    }

    public FakeAnalysis(DataInput dataInput) throws IOException {
        mTargets = new ArrayList<>();
        mProperties = new HashMap<>();

        int targetCount = dataInput.readInt();
        for (int i = 0; i < targetCount; i++) {
            mTargets.add(new FakeTarget(readMap(dataInput)));
        }

        mProperties.putAll(readMap(dataInput));
    }

    @Override
    public List<? extends Target> getDetectedTargets() {
        return mTargets;
    }

    @Override
    public boolean hasProperty(String name) {
        return mProperties.containsKey(name);
    }

    @Override
    public <T> T getProperty(String name, Class<T> type) {
        Object value = mProperties.get(name);
        if (value == null) {
            throw new NoSuchElementException(name);
        }

        return Types.smartCast(value, type);
    }

    @Override
    public void serializeTo(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(mTargets.size());
        for (FakeTarget target : mTargets) {
            writeMap(dataOutput, target.getProperties());
        }

        writeMap(dataOutput, mProperties);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FakeAnalysis analysis = (FakeAnalysis) o;
        return Objects.equals(mTargets, analysis.mTargets) &&
                Objects.equals(mProperties, analysis.mProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mTargets, mProperties);
    }

    private static void writeMap(DataOutput dataOutput, Map<String, Object> map) throws IOException {
        dataOutput.writeInt(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            dataOutput.writeUTF(entry.getKey());

            Object value = entry.getValue();
            if (value instanceof String) {
                dataOutput.writeInt(0);
                dataOutput.writeUTF((String) value);
            } else if (value instanceof Double) {
                dataOutput.writeInt(1);
                dataOutput.writeDouble((Double) value);
            } else if (value instanceof Boolean) {
                dataOutput.writeInt(2);
                dataOutput.writeBoolean((Boolean) value);
            } else if (value instanceof Integer) {
                dataOutput.writeInt(3);
                dataOutput.writeInt((Integer) value);
            }
        }
    }

    private static Map<String, Object> readMap(DataInput dataInput) throws IOException {
        Map<String, Object> map = new HashMap<>();

        int size = dataInput.readInt();
        for (int i = 0; i < size; i++) {
            String key = dataInput.readUTF();

            int type = dataInput.readInt();
            Object value;
            switch (type) {
                case 0:
                    value = dataInput.readUTF();
                    break;
                case 1:
                    value = dataInput.readDouble();
                    break;
                case 2:
                    value = dataInput.readBoolean();
                    break;
                case 3:
                    value = dataInput.readInt();
                    break;
                default: throw new IOException("unknown type " + type);
            }

            map.put(key, value);
        }

        return map;
    }
}
