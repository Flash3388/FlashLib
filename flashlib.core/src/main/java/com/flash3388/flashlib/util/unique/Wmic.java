package com.flash3388.flashlib.util.unique;

import com.castle.util.closeables.Closeables;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class Wmic {

    private Wmic() {}

    public static String getBiosSerialNumber() throws IOException {
        return getWmicOutputLine(
                new String[]{"bios", "get", "serialNumber"},
                "SerialNumber");
    }

    public static String getWmicOutputLine(String[] cmd, String outputLine) throws IOException {
        // https://stackoverflow.com/questions/1986732/how-to-get-a-unique-computer-identifier-in-java-like-disk-id-or-motherboard-id
        String[] cmdFull = new String[cmd.length + 1];
        cmdFull[0] = "wmic";
        System.arraycopy(cmd, 0, cmdFull, 1, cmd.length);

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(cmdFull);
        try {
            process.getOutputStream().close();

            InputStream procOutput = process.getInputStream();
            try {
                Scanner scanner = new Scanner(procOutput);
                while (scanner.hasNext()) {
                    String text = scanner.next();
                    if (text.equals(outputLine)) {
                        if (!scanner.hasNext()) {
                            throw new IOException("found line, but proc output has reached its end");
                        }

                        return scanner.next();
                    }
                }

                throw new IOException("wanted line not found");
            } finally {
                Closeables.silentClose(procOutput);
            }
        } finally {
            Closeables.silentClose(()-> {
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
            });
        }
    }
}
