package com.flash3388.flashlib.robot.hid.scheduling;

import com.flash3388.flashlib.robot.hid.Button;
import com.flash3388.flashlib.robot.hid.Hid;

public final class HidScheduling {

    private HidScheduling() {}

    public static void addButtonsUpdateTaskToScheduler(Hid hid) {
        hid.buttons().forEach(Button::addToScheduler);
    }
}
