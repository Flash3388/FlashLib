package com.flash3388.flashlib.robot.hid;

public class RawHidData {

    public static final int MAX_HID = 3;
    public static final int MAX_AXES = 7;
    public static final int MAX_BUTTONS = 15;
    public static final int MAX_POVS = 2;
    public static final int MAX_HID_VALUE = 127;

    public final int[] channelTypes;
    public final int[] channelContents;
    public final short[] axes;
    public final short[] buttons;
    public final short[] povs;

    public RawHidData() {
        channelTypes = new int[MAX_HID];
        channelContents = new int[MAX_HID];
        axes = new short[MAX_HID * MAX_AXES];
        buttons = new short[MAX_HID];
        povs = new short[MAX_HID * MAX_POVS];
    }
}
