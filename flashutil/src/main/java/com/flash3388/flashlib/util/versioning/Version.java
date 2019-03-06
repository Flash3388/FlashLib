package com.flash3388.flashlib.util.versioning;

import java.io.Serializable;
import java.util.Objects;

public class Version implements Serializable {

    private final int mMajor;
    private final int mMinor;
    private final int mBuild;

    public Version(int major, int minor, int build) {
        mMajor = major;
        mMinor = minor;
        mBuild = build;
    }

    public boolean isCompatibleWith(Version other) {
        return mMajor == other.mMajor && mMinor >= other.mMinor;
    }

    public int getMajor() {
        return mMajor;
    }

    public int getMinor() {
        return mMinor;
    }

    public int getBuild() {
        return mBuild;
    }

    public String getVersionString() {
        return String.format("%d.%d.%d", mMajor, mMinor, mBuild);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mMajor, mMinor, mBuild);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Version) {
            Version other = (Version)obj;
            return mMajor == other.mMajor && mMinor == other.mMinor && mBuild == other.mBuild;
        }

        return false;
    }

    @Override
    public String toString() {
        return getVersionString();
    }
}
