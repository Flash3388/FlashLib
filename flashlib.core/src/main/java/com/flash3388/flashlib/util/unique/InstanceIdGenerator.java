package com.flash3388.flashlib.util.unique;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.util.Enumeration;

public class InstanceIdGenerator {

    private InstanceIdGenerator() {}

    public static InstanceId generate() {
        // although not fool-proof, we'll use MAC address from the machine.
        // MAC addresses can actually change, but for now this will do.
        try {
            byte[] macAddress = getMacAddress();
            byte[] machineId = new byte[8];
            System.arraycopy(macAddress, 0, machineId, 0, macAddress.length);

            byte[] pid = getPid();

            return new InstanceId(machineId, pid);
        } catch (IOException e) {
            throw new IdGenerationException("failed retrieving MAC address", e);
        }
    }

    private static byte[] getMacAddress() throws IOException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        if (!interfaces.hasMoreElements()) {
            throw new IOException("no network interfaces available");
        }

        NetworkInterface networkInterface = interfaces.nextElement();
        return networkInterface.getHardwareAddress();
    }

    private static byte[] getPid() {
        try {
            String jvmName = ManagementFactory.getRuntimeMXBean().getName();
            if (!jvmName.contains("@")) {
                throw new IdGenerationException("ManagementFactory.getRuntimeMXBean().getName() " +
                        "returned an unexpected value");
            }

            String[] splitName = jvmName.split("@");
            long pid = Long.parseLong(splitName[0]);

            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.putLong(pid);
            return buffer.array();
        } catch (NumberFormatException e) {
            throw new IdGenerationException("ManagementFactory.getRuntimeMXBean().getName() " +
                    "returned an unexpected value", e);
        }
    }
}
