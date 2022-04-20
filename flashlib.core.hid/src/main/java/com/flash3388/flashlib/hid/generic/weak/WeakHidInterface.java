package com.flash3388.flashlib.hid.generic.weak;

import com.flash3388.flashlib.hid.Axis;
import com.flash3388.flashlib.hid.Button;
import com.flash3388.flashlib.hid.DualshockController;
import com.flash3388.flashlib.hid.Hid;
import com.flash3388.flashlib.hid.HidChannel;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.hid.Joystick;
import com.flash3388.flashlib.hid.Pov;
import com.flash3388.flashlib.hid.XboxController;
import com.flash3388.flashlib.hid.generic.GenericAxis;
import com.flash3388.flashlib.hid.generic.GenericButton;
import com.flash3388.flashlib.hid.generic.GenericHidChannel;
import com.flash3388.flashlib.hid.generic.GenericPov;
import com.flash3388.flashlib.hid.generic.RawHidInterface;

public class WeakHidInterface implements HidInterface {

    private final RawHidInterface mInterface;

    public WeakHidInterface(RawHidInterface anInterface) {
        mInterface = anInterface;
    }

    @Override
    public Axis newAxis(HidChannel channel) {
        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();

        return new GenericAxis(mInterface, RawHidInterface.NO_HID_CHANNEL, channelInt);
    }

    @Override
    public Button newButton(HidChannel channel) {
        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();

        return new GenericButton(mInterface, RawHidInterface.NO_HID_CHANNEL, channelInt);
    }

    @Override
    public Pov newPov(HidChannel channel) {
        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();

        return new GenericPov(mInterface, RawHidInterface.NO_HID_CHANNEL, channelInt);
    }

    @Override
    public Hid newGenericHid(HidChannel channel) {
        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();

        return new WeakHid(mInterface, channelInt);
    }

    @Override
    public Joystick newJoystick(HidChannel channel) {
        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();

        return new WeakJoystick(mInterface, channelInt);
    }

    @Override
    public XboxController newXboxController(HidChannel channel) {
        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();

        return new WeakXboxController(mInterface, channelInt);
    }

    @Override
    public DualshockController newDualshockController(HidChannel channel) {
        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();

        return new WeakDualshockController(mInterface, channelInt);
    }
}
