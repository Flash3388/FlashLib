package com.flash3388.flashlib.hid.generic;

import com.flash3388.flashlib.hid.Axis;
import com.flash3388.flashlib.hid.Button;
import com.flash3388.flashlib.hid.DualshockController;
import com.flash3388.flashlib.hid.Hid;
import com.flash3388.flashlib.hid.HidChannel;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.hid.Joystick;
import com.flash3388.flashlib.hid.Pov;
import com.flash3388.flashlib.hid.XboxController;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

public class GenericHidInterface implements HidInterface {

    private static final Logger LOGGER = Logging.getMainLogger();

    private final RawHidInterface mInterface;
    private final FlashLibMainThread mMainThread;

    public GenericHidInterface(RawHidInterface anInterface, FlashLibMainThread mainThread) {
        mInterface = anInterface;
        mMainThread = mainThread;
    }

    @Override
    public Axis newAxis(HidChannel channel) {
        mMainThread.verifyCurrentThread();

        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();
        LOGGER.info("Create new HID Axis for channel {}", channelInt);

        ensureExistsChannelOfType(channelInt, ChannelType.AXIS);
        return new GenericAxis(mInterface, RawHidInterface.NO_HID_CHANNEL, channelInt);
    }

    @Override
    public Button newButton(HidChannel channel) {
        mMainThread.verifyCurrentThread();

        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();
        LOGGER.info("Create new HID Button for channel {}", channelInt);

        ensureExistsChannelOfType(channelInt, ChannelType.BUTTON);
        return new GenericButton(mInterface, RawHidInterface.NO_HID_CHANNEL, channelInt);
    }

    @Override
    public Pov newPov(HidChannel channel) {
        mMainThread.verifyCurrentThread();

        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();
        LOGGER.info("Create new HID POV for channel {}", channelInt);

        ensureExistsChannelOfType(channelInt, ChannelType.POV);
        return new GenericPov(mInterface, RawHidInterface.NO_HID_CHANNEL, channelInt);
    }

    @Override
    public Hid newGenericHid(HidChannel channel) {
        mMainThread.verifyCurrentThread();

        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();
        LOGGER.info("Create new HID for channel {}", channelInt);

        ensureExistsChannelOfType(channelInt, ChannelType.HID);
        return new GenericHid(mInterface, channelInt);
    }

    @Override
    public Joystick newJoystick(HidChannel channel) {
        mMainThread.verifyCurrentThread();

        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();
        LOGGER.info("Create new HID Joystick for channel {}", channelInt);

        ensureExistsChannelOfType(channelInt, ChannelType.JOYSTICK);
        return new GenericJoystick(mInterface, channelInt);
    }

    @Override
    public XboxController newXboxController(HidChannel channel) {
        mMainThread.verifyCurrentThread();

        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();
        LOGGER.info("Create new HID XBOX Controller for channel {}", channelInt);

        ensureExistsChannelOfType(channelInt, ChannelType.XBOX);
        return new GenericXboxController(mInterface, channelInt);
    }

    @Override
    public DualshockController newDualshockController(HidChannel channel) {
        mMainThread.verifyCurrentThread();

        GenericHidChannel genericHidChannel = HidChannel.cast(channel, GenericHidChannel.class);
        int channelInt = genericHidChannel.intValue();
        LOGGER.info("Create new HID DualShock4 for channel {}", channelInt);

        ensureExistsChannelOfType(channelInt, ChannelType.XBOX);
        return new GenericDualshockController(mInterface, channelInt);
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
