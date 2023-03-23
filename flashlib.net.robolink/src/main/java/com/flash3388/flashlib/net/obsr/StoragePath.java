package com.flash3388.flashlib.net.obsr;

import java.util.Objects;

public class StoragePath {

    public static final char DELIMITER = '/';

    private final String mPath;

    private StoragePath(String path) {
        mPath = normalizePath(path);
    }

    public static StoragePath create(String path) {
        return new StoragePath(path);
    }

    public static StoragePath root() {
        return new StoragePath(DELIMITER + "");
    }

    public boolean isRoot() {
        return mPath.length() == 1 && mPath.charAt(0) == DELIMITER;
    }

    public StoragePath parent() {
        if (isRoot()) {
            throw new IllegalStateException("this path is root and has no parents");
        }

        return new StoragePath(dirname(mPath));
    }

    public StoragePath child(String subPath) {
        if (subPath.indexOf(DELIMITER) == 0) {
            subPath = subPath.substring(1);
        }

        String endPath = mPath;
        if (mPath.lastIndexOf(DELIMITER) != mPath.length() - 1) {
            endPath += DELIMITER;
        }

        return new StoragePath(endPath + subPath);
    }

    public String getName() {
        return basename(mPath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoragePath that = (StoragePath) o;
        return Objects.equals(mPath, that.mPath);
    }

    @Override
    public int hashCode() {
        return mPath.hashCode();
    }

    @Override
    public String toString() {
        return mPath;
    }

    private static String normalizePath(String path) {
        if (path.indexOf(DELIMITER) != 0) {
            path = DELIMITER + path;
        }

        return path;
    }

    private static String basename(String path) {
        int lastDelimiterIndex = path.lastIndexOf(DELIMITER);
        return path.substring(lastDelimiterIndex + 1);
    }

    private static String dirname(String path) {
        int lastDelimiterIndex = path.lastIndexOf(DELIMITER);
        return path.substring(0, lastDelimiterIndex);
    }
}
