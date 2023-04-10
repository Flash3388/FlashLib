package com.flash3388.flashlib.vision.cv;

import com.castle.code.Natives;
import com.castle.exceptions.CodeLoadException;
import com.castle.exceptions.FindException;
import org.opencv.core.Core;

import java.io.IOException;

public class OpenCv {

    private OpenCv() {}

    public static void loadNatives() {
        try {
            Natives.Loader loader = Natives.newLoader();
            loader.load(Core.NATIVE_LIBRARY_NAME);
        } catch (FindException | IOException | CodeLoadException e) {
            throw new Error(e);
        }
    }
}
