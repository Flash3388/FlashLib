package com.flash3388.flashlib.util.unique;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class InstanceId {

    private static final int MACHINE_ID_SIZE = Long.BYTES;
    private static final int PROCESS_ID_SIZE = Long.BYTES;

    private final byte[] mInstanceId;

    InstanceId(byte[] machineId, byte[] processId) {
        assert MACHINE_ID_SIZE == machineId.length;
        assert PROCESS_ID_SIZE == processId.length;

        mInstanceId = new byte[machineId.length + processId.length];
        System.arraycopy(machineId, 0, mInstanceId, 0, machineId.length);
        System.arraycopy(processId, 0, mInstanceId, machineId.length, processId.length);
    }

    private InstanceId(byte[] instanceId) {
        assert instanceId.length == MACHINE_ID_SIZE + PROCESS_ID_SIZE;
        mInstanceId = instanceId;
    }

    public static InstanceId createFrom(DataInput dataInput) throws IOException {
        byte[] data = new byte[MACHINE_ID_SIZE + PROCESS_ID_SIZE];
        dataInput.readFully(data);

        return new InstanceId(data);
    }

    public byte[] get() {
        return mInstanceId;
    }

    private byte[] getMachineId() {
        return Arrays.copyOfRange(mInstanceId, 0, MACHINE_ID_SIZE);
    }

    private byte[] getProcessId() {
        return Arrays.copyOfRange(mInstanceId, MACHINE_ID_SIZE, mInstanceId.length);
    }

    public void writeTo(DataOutput dataOutput) throws IOException {
        dataOutput.write(mInstanceId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanceId that = (InstanceId) o;
        return Arrays.equals(mInstanceId, that.mInstanceId);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(mInstanceId);
    }

    @Override
    public String toString() {
        long machineId = ByteBuffer.wrap(getMachineId()).getLong();
        long processId = ByteBuffer.wrap(getProcessId()).getLong();

        return String.format("{%d-%d}", machineId, processId);
    }
}
