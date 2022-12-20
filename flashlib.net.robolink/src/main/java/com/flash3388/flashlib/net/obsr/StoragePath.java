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

    public boolean isRoot() {
        return mPath.length() == 1 && mPath.charAt(0) == DELIMITER;
    }

    public StoragePath parent() {
        return new StoragePath(dirname(mPath));
    }

    public StoragePath child(String subPath) {
        if (subPath.indexOf(DELIMITER) == 0) {
            subPath = subPath.substring(1);
        }

        return new StoragePath(mPath + DELIMITER + subPath);
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
        if (path.lastIndexOf(DELIMITER) == path.length() - 1) {
            path = path.substring(0, path.length() - 2);
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
