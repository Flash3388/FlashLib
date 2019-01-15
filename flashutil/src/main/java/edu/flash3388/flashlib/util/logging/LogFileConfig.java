package edu.flash3388.flashlib.util.logging;

public class LogFileConfig {

    private final int mSizeLimitBytes;
    private final int mFileCount;

    public LogFileConfig(int sizeLimitBytes, int fileCount) {
        mSizeLimitBytes = sizeLimitBytes;
        mFileCount = fileCount;
    }

    public int getSizeLimitBytes() {
        return mSizeLimitBytes;
    }

    public int getFileCount() {
        return mFileCount;
    }
}
