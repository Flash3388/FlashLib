package com.flash3388.flashlib.hid.generic;

import com.flash3388.flashlib.hid.Axis;
import com.flash3388.flashlib.hid.Button;
import com.flash3388.flashlib.hid.Hid;
import com.flash3388.flashlib.hid.HidChannel;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.hid.Joystick;
import com.flash3388.flashlib.hid.Pov;
import com.flash3388.flashlib.hid.XboxController;

public class GenericHidInterface implements HidInterface {

    private final RawHidInterface mInterface;

    public GenericHidInterface(RawHidInterface anInterface) {
        mInterface = anInterface;
    }

    @Override
    public Axis newAxis(HidChannel channel) {
        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();

        ensureExistsChannelOfType(channelInt, ChannelType.AXIS);
        return new GenericAxis(mInterface, RawHidInterface.NO_HID_CHANNEL, channelInt);
    }

    @Override
    public Button newButton(HidChannel channel) {
        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();

        ensureExistsChannelOfType(channelInt, ChannelType.BUTTON);
        return new GenericButton(mInterface, RawHidInterface.NO_HID_CHANNEL, channelInt);
    }

    @Override
    public Pov newPov(HidChannel channel) {
        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();

        ensureExistsChannelOfType(channelInt, ChannelType.POV);
        return new GenericPov(mInterface, RawHidInterface.NO_HID_CHANNEL, channelInt);
    }

    @Override
    public Hid newGenericHid(HidChannel channel) {
        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();

        ensureExistsChannelOfType(channelInt, ChannelType.HID);
        return new GenericHid(mInterface, channelInt);
    }

    @Override
    public Joystick newJoystick(HidChannel channel) {
        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();

        ensureExistsChannelOfType(channelInt, ChannelType.JOYSTICK);
        return new GenericJoystick(mInterface, channelInt);
    }

    @Override
    public XboxController newXboxController(HidChannel channel) {
        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();

        ensureExistsChannelOfType(channelInt, ChannelType.XBOX);
        return new GenericXboxController(mInterface, channelInt);
    }

    private void ensureExistsChannelOfType(int channel, ChannelType wantedType) {
        if (!mInterface.hasChannel(channel)) {
            throw new IllegalArgumentException("No such channel " + channel);
        }

        ChannelType actualType = mInterface.getChannelType(channel);
        if (!actualType.doesSupport(wantedType)) {
            throw new IllegalArgumentException(
                    String.format("Incompatible channel type %s, for channel %d (is %s)",
                            actualType, channel, wantedType));
        }
    }
}
