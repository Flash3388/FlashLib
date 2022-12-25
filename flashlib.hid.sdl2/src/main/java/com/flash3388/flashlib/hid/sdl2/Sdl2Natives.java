package com.flash3388.flashlib.hid.sdl2;

import com.castle.nio.temp.TempPath;
import com.castle.nio.temp.TempPathGenerator;
import com.castle.util.os.KnownOperatingSystem;
import com.castle.util.os.System;
import sdl2.SDL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Sdl2Natives {

    private Sdl2Natives() {}

    private static final String SDLJNI_LIBNAME = "libsdl2_jni";

    public static void loadNatives() {
        if (KnownOperatingSystem.WINDOWS.isCurrent()) {
            // windows has some problem with loading dependencies
            java.lang.System.loadLibrary("KERNEL32");
            java.lang.System.loadLibrary("msvcrt");
            java.lang.System.loadLibrary("SDL2");
        }

        try {
            loadSdl();
        } catch (Exception e) {
            throw new Error("Error loading natives for jsdl2", e);
        }
    }

    private static void loadSdl() throws Exception {
        if (KnownOperatingSystem.WINDOWS.isCurrent()) {
            Path sdlExtractPath = Paths.get(java.lang.System.getProperty("user.dir"))
                    .resolve(SDLJNI_LIBNAME + ".dll");
            if (!Files.exists(sdlExtractPath)) {
                // not extracted
                extractSdl(sdlExtractPath);
            }

            java.lang.System.load(sdlExtractPath.toAbsolutePath().toString());
        } else if (KnownOperatingSystem.LINUX.isCurrent()) {
            try (TempPath tempPath = new TempPathGenerator().generateFile()) {
                extractSdl(tempPath.originalPath());
                java.lang.System.load(tempPath.originalPath().toAbsolutePath().toString());
            }
        } else {
            throw new Error("Current operating system isn't support by jsdl2");
        }
    }

    private static void extractSdl(Path destination) throws IOException {
        String libPath = "/" + SDLJNI_LIBNAME + System.operatingSystem().nativeLibraryExtension();
        try (InputStream inputStream = SDL.class.getResourceAsStream(libPath)) {
            if (inputStream == null) {
                throw new Error("Unable to find jni library for jsdl2 in dependencies. " +
                        "Remember to include jsdl2-jni in your classpath");
            }
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
