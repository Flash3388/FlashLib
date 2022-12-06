package com.flash3388.flashlib.hid.sdl2;

import com.castle.nio.temp.TempPath;
import com.castle.nio.temp.TempPathGenerator;
import com.castle.util.os.OperatingSystem;
import com.castle.util.os.System;
import com.flash3388.flashlib.hid.HidChannel;
import com.flash3388.flashlib.hid.generic.GenericHidChannel;
import sdl2.SDL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Sdl2Hid {

    private static final String SDLJNI_LIBNAME = "libsdl2_jni";

    private static final Sdl2HidData sHidData;
    private static final Sdl2UpdateService sUpdateService;

    static {
        loadNatives();

        sHidData = new Sdl2HidData();
        sUpdateService = new Sdl2UpdateService(sHidData);
        sUpdateService.start();
    }

    public static Sdl2HidData getHidData() {
        return sHidData;
    }

    public static HidChannel newChannel(int channel) {
        return new GenericHidChannel(channel);
    }

    private static void loadNatives() {
        if (System.operatingSystem() == OperatingSystem.Windows) {
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
        switch (System.operatingSystem()) {
            case Windows:
                Path sdlExtractPath = Paths.get(java.lang.System.getProperty("user.dir"))
                        .resolve(SDLJNI_LIBNAME + ".dll");
                if (!Files.exists(sdlExtractPath)) {
                    // not extracted
                    extractSdl(sdlExtractPath);
                }

                java.lang.System.load(sdlExtractPath.toAbsolutePath().toString());
                break;
            case Linux:
                try (TempPath tempPath = new TempPathGenerator().generateFile()) {
                    extractSdl(tempPath.originalPath());
                    java.lang.System.load(tempPath.originalPath().toAbsolutePath().toString());
                }
                break;
            default:
                throw new Error("Current operating system isn't support by jsdl2");
        }
    }

    private static void extractSdl(Path destination) throws IOException {
        String libPath;
        switch (System.operatingSystem()) {
            case Windows:
                libPath = "/" + SDLJNI_LIBNAME + ".dll";
                break;
            case Linux:
                libPath = "/" + SDLJNI_LIBNAME + ".so";
                break;
            default:
                throw new UnsupportedOperationException();
        }

        try (InputStream inputStream = SDL.class.getResourceAsStream(libPath)) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
