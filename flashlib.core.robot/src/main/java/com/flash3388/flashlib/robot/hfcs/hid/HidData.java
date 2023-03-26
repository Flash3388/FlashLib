package com.flash3388.flashlib.robot.hfcs.hid;

import com.flash3388.flashlib.hid.generic.ChannelType;

import java.util.Arrays;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HidData {

    private final ReadWriteLock mLock;
    private RawHidData mHidData;

    public HidData() {
        mLock = new ReentrantReadWriteLock();
        mHidData = null;
    }

    public RawHidData loadNewData(RawHidData data) {
        mLock.writeLock().lock();
        try {
            RawHidData old = mHidData;
            mHidData = data;
            return old;
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public RawHidData replace(RawHidData data) {
        mLock.writeLock().lock();
        try {
            RawHidData old = mHidData;
            mHidData = data;
            if (old == null) {
                return null;
            }

            System.arraycopy(old.channelTypes, 0, data.channelTypes, 0, old.channelTypes.length);
            System.arraycopy(old.channelContents, 0, data.channelContents, 0,
                    old.channelContents.length);

            System.arraycopy(old.axes, 0, data.axes, 0, old.axes.length);
            System.arraycopy(old.buttons, 0, data.buttons, 0, old.buttons.length);
            System.arraycopy(old.povs, 0, data.povs, 0, old.povs.length);

            return old;
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public boolean hasChannel(int index) {
        checkValidHid(index);

        mLock.readLock().lock();
        try {
            if (mHidData == null) {
                return false;
            }

            return mHidData.channelTypes[index] >= 0;
        } finally {
            mLock.readLock().unlock();
        }
    }

    public ChannelType getChannelType(int index) {
        checkValidHid(index);

        mLock.readLock().lock();
        try {
            if (mHidData == null) {
                return null;
            }

            int intType = mHidData.channelTypes[index];
            if (intType < 0) {
                return null;
            }

            return ChannelType.values()[intType];
        } finally {
            mLock.readLock().unlock();
        }
    }

    public int getAxesCount(int index) {
        checkValidHid(index);

        mLock.readLock().lock();
        try {
            if (mHidData == null) {
                return 0;
            }

            return mHidData.channelContents[index] & 0xf;
        } finally {
            mLock.readLock().unlock();
        }
    }

    public int getButtonCount(int index) {
        checkValidHid(index);

        mLock.readLock().lock();
        try {
            if (mHidData == null) {
                return 0;
            }

            return (mHidData.channelContents[index] >> 4) & 0xff;
        } finally {
            mLock.readLock().unlock();
        }
    }

    public int getPovCount(int index) {
        checkValidHid(index);

        mLock.readLock().lock();
        try {
            if (mHidData == null) {
                return 0;
            }

            return (mHidData.channelContents[index] >> 12) & 0xf;
        } finally {
            mLock.readLock().unlock();
        }
    }

    public double getAxisValue(int hid, int index) {
        checkValidHid(hid);
        checkValidAxis(index);

        mLock.readLock().lock();
        try {
            if (mHidData == null) {
                return 0.0;
            }

            if (index >= getAxesCount(hid)) {
                return 0;
            }

            return mHidData.axes[hid * RawHidData.MAX_AXES + index] / (double) RawHidData.MAX_HID_VALUE;
        } finally {
            mLock.readLock().unlock();
        }
    }

    public boolean getButtonValue(int hid, int index) {
        checkValidHid(hid);
        checkValidButton(index);

        mLock.readLock().lock();
        try {
            if (mHidData == null) {
                return false;
            }

            if (index >= getButtonCount(hid)) {
                return false;
            }

            return (mHidData.buttons[hid] & (1 << index)) != 0;
        } finally {
            mLock.readLock().unlock();
        }
    }

    public int getPovValue(int hid, int index) {
        checkValidHid(hid);
        checkValidPov(index);

        mLock.readLock().lock();
        try {
            if (mHidData == null) {
                return -1;
            }

            if (index >= getPovCount(hid)) {
                return -1;
            }

            return mHidData.povs[hid * RawHidData.MAX_POVS + index];
        } finally {
            mLock.readLock().unlock();
        }
    }

    public void setChannel(int index, ChannelType type, int axes, int buttons, int povs) {
        checkValidHid(index);

        mLock.writeLock().lock();
        try {
            if (mHidData == null) {
                return;
            }

            mHidData.channelTypes[index] = type.ordinal();
            mHidData.channelContents[index] =
                    (((short) axes) & 0xf) |
                            ((((short) buttons) & 0xff) << 4) |
                            ((((short) povs) & 0xf) << 12);

            Arrays.fill(mHidData.axes,
                    index * RawHidData.MAX_AXES,
                    index * RawHidData.MAX_AXES + RawHidData.MAX_AXES,
                    (short) 0);
            mHidData.buttons[index] = 0;
            Arrays.fill(mHidData.povs,
                    index * RawHidData.MAX_POVS,
                    index * RawHidData.MAX_POVS + RawHidData.MAX_POVS,
                    (short) 0);
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public void clearChannel(int index) {
        checkValidHid(index);

        mLock.writeLock().lock();
        try {
            if (mHidData == null) {
                return;
            }

            mHidData.channelTypes[index] = -1;
            mHidData.channelContents[index] = 0;
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public void clearChannels() {
        for (int i = 0; i < mHidData.channelTypes.length; i++) {
            clearChannel(0);
        }
    }

    public void moveChannel(int srcIndex, int dstIndex) {
        checkValidHid(srcIndex);
        checkValidHid(dstIndex);

        mLock.writeLock().lock();
        try {
            if (mHidData == null) {
                return;
            }

            int dstType = mHidData.channelTypes[dstIndex];
            int dstContents = mHidData.channelContents[dstIndex];
            short[] dstAxes = new short[RawHidData.MAX_AXES];
            System.arraycopy(
                    mHidData.axes, dstIndex * RawHidData.MAX_AXES,
                    dstAxes, 0,
                    RawHidData.MAX_AXES);
            short dstButtons = mHidData.buttons[dstIndex];
            short[] dstPovs = new short[RawHidData.MAX_POVS];
            System.arraycopy(
                    mHidData.povs, dstIndex * RawHidData.MAX_POVS,
                    dstPovs, 0,
                    RawHidData.MAX_POVS);

            mHidData.channelTypes[dstIndex] = mHidData.channelTypes[srcIndex];
            mHidData.channelContents[dstIndex] = mHidData.channelContents[srcIndex];
            System.arraycopy(
                    mHidData.axes, srcIndex * RawHidData.MAX_AXES,
                    mHidData.axes, dstIndex * RawHidData.MAX_AXES,
                    RawHidData.MAX_AXES);
            mHidData.buttons[dstIndex] = mHidData.buttons[srcIndex];
            System.arraycopy(
                    mHidData.povs, srcIndex * RawHidData.MAX_POVS,
                    mHidData.povs, dstIndex * RawHidData.MAX_POVS,
                    RawHidData.MAX_POVS);

            mHidData.channelTypes[srcIndex] = dstType;
            mHidData.channelContents[srcIndex] = dstContents;
            System.arraycopy(
                    dstAxes, 0,
                    mHidData.povs, srcIndex * RawHidData.MAX_AXES,
                    RawHidData.MAX_AXES);
            mHidData.buttons[srcIndex] = dstButtons;
            System.arraycopy(
                    dstPovs, 0,
                    mHidData.povs, srcIndex * RawHidData.MAX_POVS,
                    RawHidData.MAX_POVS);
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public void setAxis(int hid, int index, short value) {
        checkValidHid(hid);
        checkValidAxis(index);

        mLock.writeLock().lock();
        try {
            if (mHidData == null) {
                return;
            }

            mHidData.axes[hid * RawHidData.MAX_AXES + index] = value;
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public void setButton(int hid, int index, boolean value) {
        checkValidHid(hid);
        checkValidButton(index);

        mLock.writeLock().lock();
        try {
            if (mHidData == null) {
                return;
            }

            if (value) {
                mHidData.buttons[hid] |= (1 << index);
            } else {
                mHidData.buttons[hid] &= ~(1 << index);
            }
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public void setPovs(int hid, int index, short value) {
        checkValidHid(hid);
        checkValidPov(index);

        mLock.writeLock().lock();
        try {
            if (mHidData == null) {
                return;
            }

            mHidData.povs[hid * RawHidData.MAX_POVS + index] = value;
        } finally {
            mLock.writeLock().unlock();
        }
    }

    private void checkValidHid(int index) {
        if (index < 0 || index >= RawHidData.MAX_HID) {
            throw new IllegalArgumentException("hid index must be non-negative and below " + RawHidData.MAX_HID);
        }
    }

    private void checkValidAxis(int index) {
        if (index < 0 || index >= RawHidData.MAX_AXES) {
            throw new IllegalArgumentException("axis index must be non-negative and below " + RawHidData.MAX_AXES);
        }
    }

    private void checkValidButton(int index) {
        if (index < 0 || index >= RawHidData.MAX_BUTTONS) {
            throw new IllegalArgumentException("button index must be non-negative and below " + RawHidData.MAX_BUTTONS);
        }
    }

    private void checkValidPov(int index) {
        if (index < 0 || index >= RawHidData.MAX_POVS) {
            throw new IllegalArgumentException("pov index must be non-negative and below " + RawHidData.MAX_POVS);
        }
    }
}
