package com.flash3388.flashlib.hid.generic.weak;

import com.flash3388.flashlib.hid.Axis;
import com.flash3388.flashlib.hid.Button;
import com.flash3388.flashlib.hid.Dpad;
import com.flash3388.flashlib.hid.DualshockAxis;
import com.flash3388.flashlib.hid.DualshockButton;
import com.flash3388.flashlib.hid.DualshockController;
import com.flash3388.flashlib.hid.Pov;
import com.flash3388.flashlib.hid.generic.GenericAxis;
import com.flash3388.flashlib.hid.generic.GenericButton;
import com.flash3388.flashlib.hid.generic.GenericDpad;
import com.flash3388.flashlib.hid.generic.RawHidInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeakDualshockController extends WeakHid implements DualshockController {

    public WeakDualshockController(RawHidInterface hidInterface, int channel) {
        super(hidInterface, channel,
                createAxes(hidInterface, channel),
                createButtons(hidInterface, channel),
                Collections.singletonList(new GenericDpad(hidInterface, channel, 0)));
    }

    @Override
    public Pov getPov(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("Unknown pov " + index);
        }

        return super.getPov(index);
    }

    @Override
    public Dpad getDpad() {
        return (Dpad) getPov(0);
    }

    private static List<Axis> createAxes(RawHidInterface hidInterface, int channel) {
        List<Axis> axes = new ArrayList<>();
        for (int i = 0; i < DualshockAxis.count(); i++) {
            axes.add(new GenericAxis(hidInterface, channel, i));
        }
        return axes;
    }

    private static List<Button> createButtons(RawHidInterface hidInterface, int channel) {
        List<Button> buttons = new ArrayList<>();
        for (int i = 0; i < DualshockButton.count(); i++) {
            buttons.add(new GenericButton(hidInterface, channel, i));
        }
        return buttons;
    }
}
