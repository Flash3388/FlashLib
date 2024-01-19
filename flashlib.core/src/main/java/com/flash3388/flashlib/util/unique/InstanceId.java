package com.flash3388.flashlib.util.unique;

import com.flash3388.flashlib.util.Binary;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class InstanceId {

    private static final int MACHINE_ID_SIZE = Long.BYTES * 2;
    private static final int PROCESS_ID_SIZE = Long.BYTES;
    public static final int BYTES = MACHINE_ID_SIZE + PROCESS_ID_SIZE;

    private final byte[] mMachineId;
    private final byte[] mProcessId;

    public InstanceId(byte[] machineId, byte[] processId) {
        Objects.requireNonNull(machineId, "machineId");
        Objects.requireNonNull(processId, "processId");

        machineId = padId(machineId, MACHINE_ID_SIZE);
        processId = padId(processId, PROCESS_ID_SIZE);

        if (machineId.length != MACHINE_ID_SIZE) {
            throw new IllegalArgumentException("machineId bad size");
        }

        if (processId.length != PROCESS_ID_SIZE) {
            throw new IllegalArgumentException("processId bad size");
        }

        mMachineId = machineId;
        mProcessId = processId;
    }

    public InstanceId(String machineId, String processId) {
        this(Binary.hexToBytes(machineId), Binary.hexToBytes(processId));
    }

    public static InstanceId createFrom(DataInput dataInput) throws IOException {
        byte[] machineId = new byte[MACHINE_ID_SIZE];
        dataInput.readFully(machineId);
        byte[] processId = new byte[PROCESS_ID_SIZE];
        dataInput.readFully(processId);

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
        dataOutput.write(mMachineId);
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
        return String.format("{0x%s-0x%s}",
                Binary.bytesToHex(mMachineId),
                Binary.bytesToHex(mProcessId));
    }

    private static byte[] padId(byte[] bytes, int wantedLength) {
        if (bytes.length >= wantedLength) {
            return bytes;
        }

        return Arrays.copyOf(bytes, wantedLength);
    }
}
