package com.flash3388.flashlib.net.obsr;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class StoragePath implements Iterable<String> {

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

    public boolean startsWith(StoragePath path) {
        return mPath.startsWith(path.mPath);
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

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            private final String[] mParts = mPath.split(String.valueOf(DELIMITER));
            private int mIndex = 0;

            @Override
            public boolean hasNext() {
                return mIndex < mParts.length - 1;
            }

            @Override
            public String next() {
                mIndex++;
                if (mIndex <= 0 || mIndex >= mParts.length) {
                    throw new NoSuchElementException();
                }

                return mParts[mIndex];
            }
        };
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
