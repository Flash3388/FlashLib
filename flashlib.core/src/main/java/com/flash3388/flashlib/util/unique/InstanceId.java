package com.flash3388.flashlib.util.unique;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class InstanceId {
    private static final int PROCESS_ID_SIZE = Long.BYTES;

    private final byte[] mMachineId;
    private final byte[] mProcessId;

    InstanceId(byte[] machineId, byte[] processId) {
        assert machineId.length > 1;
        assert PROCESS_ID_SIZE == processId.length;

        mMachineId = machineId;
        mProcessId = processId;
    }

    public static InstanceId createFrom(DataInput dataInput) throws IOException {
        int size = dataInput.readInt();
        byte[] machineId = new byte[size];
        dataInput.readFully(machineId);
        size = dataInput.readInt();
        byte[] processId = new byte[size];
        dataInput.readFully(machineId);

        return new InstanceId(machineId, processId);
    }

    public byte[] get() {
        byte[] instanceId = new byte[mMachineId.length + mProcessId.length];
        System.arraycopy(mMachineId, 0, instanceId, 0, mMachineId.length);
        System.arraycopy(mProcessId, 0, instanceId, mMachineId.length, mProcessId.length);

        return instanceId;
    }

    private byte[] getMachineId() {
        return Arrays.copyOf(mMachineId, mMachineId.length);
    }

    private byte[] getProcessId() {
        return Arrays.copyOf(mProcessId, mProcessId.length);
    }

    public void writeTo(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(mMachineId.length);
        dataOutput.write(mMachineId);
        dataOutput.writeInt(mProcessId.length);
        dataOutput.write(mProcessId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstanceId that = (InstanceId) o;
        return Arrays.equals(mMachineId, that.mMachineId) &&
                Arrays.equals(mProcessId, that.mProcessId);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(mMachineId);
        result = 31 * result + Arrays.hashCode(mProcessId);
        return result;
    }

    @Override
    public String toString() {
        long processId = ByteBuffer.wrap(mProcessId).getLong();

        return String.format("{%s-%s}",
                Arrays.toString(mMachineId),
                Long.toHexString(processId));
    }
}
