package com.flash3388.flashlib.util.unique;

import com.castle.util.os.KnownOperatingSystem;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.List;

public class InstanceIdGenerator {

    private static final Logger LOGGER = Logging.getMainLogger();

    private InstanceIdGenerator() {}

    public static InstanceId generate() {
        byte[] machineId = getMachineId();
        byte[] pid = getPidAsBytes();

        return new InstanceId(machineId, pid);
    }

    private static byte[] getMachineId() {
        if (KnownOperatingSystem.LINUX.isCurrent()) {
            Path machineIdFile = Paths.get("/etc/machine-id");
            if (Files.exists(machineIdFile)) {
                try {
                    LOGGER.debug("Using /etc/machine-id for machine-id");
                    byte[] id = Files.readAllBytes(machineIdFile);

                    MessageDigest digest = MessageDigest.getInstance("MD5");
                    digest.update(id);
                    return digest.digest();
                } catch (IOException | NoSuchAlgorithmException e) {
                    // failed to get id from file
                    LOGGER.error("Failed retrieving machine id from /etc/machine-id", e);
                }
            }
        }

        try {
            // although not fool-proof, we'll use MAC address from the machine.
            // MAC addresses can actually change, but for now this will do.
            LOGGER.debug("Using MAC address for machine-id");
            return getMacAddress();
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

    private static byte[] getPidAsBytes() {
        long pid = getPid();
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(pid);
        return buffer.array();
    }

    private static long getPid() {
        if (KnownOperatingSystem.LINUX.isCurrent()) {
            Path processStat = Paths.get("/proc/self/stat");
            if (Files.exists(processStat)) {
                try {
                    LOGGER.debug("Using /proc/self for process-id");
                    List<String> lines = Files.readAllLines(processStat);
                    String pidString = lines.get(0).split(" ")[0];
                    return Long.parseLong(pidString);
                } catch (IOException | NumberFormatException e) {
                    // failed to get id from file
                    LOGGER.error("Failed retrieving machine id from /proc/self", e);
                }
            }
        }

        try {
            LOGGER.debug("Using ManagementFactory.getRuntimeMXBean() for process-id");

            String jvmName = ManagementFactory.getRuntimeMXBean().getName();
            if (!jvmName.contains("@")) {
                throw new IdGenerationException("ManagementFactory.getRuntimeMXBean().getName() " +
                        "returned an unexpected value");
            }

            String[] splitName = jvmName.split("@");
            return Long.parseLong(splitName[0]);
        } catch (NumberFormatException e) {
            throw new IdGenerationException("ManagementFactory.getRuntimeMXBean().getName() " +
                    "returned an unexpected value", e);
        }
    }
}
