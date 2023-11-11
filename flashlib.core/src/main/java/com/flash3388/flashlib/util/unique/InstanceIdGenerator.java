package com.flash3388.flashlib.util.unique;

import com.castle.util.os.KnownOperatingSystem;
import com.flash3388.flashlib.util.Binary;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.net.NetInterfaces;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

public class InstanceIdGenerator {

    private static final Logger LOGGER = Logging.getMainLogger();

    private InstanceIdGenerator() {}

    public static InstanceId generate(byte[] processIdentifier) {
        byte[] machineId = getMachineId();
        return new InstanceId(machineId, processIdentifier);
    }

    public static InstanceId generate(long processIdentifier) {
        return generate(Binary.longToBytes(processIdentifier));
    }

    public static InstanceId generate() {
        return generate(getPid());
    }

    private static byte[] getMachineId() {
        if (KnownOperatingSystem.LINUX.isCurrent()) {
            Optional<byte[]> optional = getMachineIdLinux();
            if (optional.isPresent()) {
                return optional.get();
            }
        } else if (KnownOperatingSystem.WINDOWS.isCurrent()) {
            Optional<byte[]> optional = getMachineIdWindows();
            if (optional.isPresent()) {
                return optional.get();
            }
        }

        return getMachineIdFallback();
    }

    private static Optional<byte[]> getMachineIdLinux() {
        Path machineIdFile = Paths.get("/etc/machine-id");
        if (Files.exists(machineIdFile)) {
            try {
                LOGGER.debug("Using /etc/machine-id for machine-id");
                byte[] id = Files.readAllBytes(machineIdFile);

                MessageDigest digest = MessageDigest.getInstance("MD5");
                digest.update(id);
                byte[] result = digest.digest();
                return Optional.of(result);
            } catch (IOException | NoSuchAlgorithmException e) {
                // failed to get id from file
                LOGGER.error("Failed retrieving machine id from /etc/machine-id", e);
            }
        } else {
            LOGGER.debug("/etc/machine-id does not exist");
        }

        return Optional.empty();
    }

    private static Optional<byte[]> getMachineIdWindows() {
        try {
            String serialNumber = Wmic.getBiosSerialNumber();
            byte[] id = serialNumber.getBytes(StandardCharsets.UTF_8);
            return Optional.of(id);
        } catch (IOException e) {
            LOGGER.error("Failed retrieving serial number from wmic", e);
        }

        return Optional.empty();
    }

    private static byte[] getMachineIdFallback() {
        try {
            // although not fool-proof, we'll use MAC address from the machine.
            // MAC addresses can actually change, but for now this will do.
            LOGGER.debug("Using MAC address for machine-id");
            return NetInterfaces.getMacAddress();
        } catch (IOException e) {
            throw new IdGenerationException("failed retrieving MAC address", e);
        }
    }

    private static long getPid() {
        if (KnownOperatingSystem.LINUX.isCurrent()) {
            Optional<Long> optionalPid = getPidLinux();
            if (optionalPid.isPresent()) {
                return optionalPid.get();
            }
        }

        return getPidFallback();
    }

    private static Optional<Long> getPidLinux() {
        Path processStat = Paths.get("/proc/self/stat");
        if (Files.exists(processStat)) {
            try {
                LOGGER.debug("Using /proc/self for process-id");

                List<String> lines = Files.readAllLines(processStat);
                String pidString = lines.get(0).split(" ")[0];
                long pid = Long.parseLong(pidString);
                return Optional.of(pid);
            } catch (IOException | NumberFormatException e) {
                // failed to get id from file
                LOGGER.error("Failed retrieving machine id from /proc/self", e);
            }
        } else {
            LOGGER.debug("/proc/self/stat does not exist");
        }

        return Optional.empty();
    }

    private static long getPidFallback() {
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
