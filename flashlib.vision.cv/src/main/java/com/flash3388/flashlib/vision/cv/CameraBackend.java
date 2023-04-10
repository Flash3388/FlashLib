package com.flash3388.flashlib.vision.cv;

import org.opencv.videoio.Videoio;

public enum CameraBackend {
    ANY(Videoio.CAP_ANY),
    V4L2(Videoio.CAP_V4L2),
    V4L(Videoio.CAP_V4L),
    OPENCV_MJPEG(Videoio.CAP_OPENCV_MJPEG),
    FFMPEG(Videoio.CAP_FFMPEG),
    GSTREAMER(Videoio.CAP_GSTREAMER)
    ;

    private final int mCode;

    CameraBackend(int code) {
        mCode = code;
    }

    public int getCode() {
        return mCode;
    }

    public boolean isSupported() {
        return Videoio.hasBackend(mCode);
    }
}
