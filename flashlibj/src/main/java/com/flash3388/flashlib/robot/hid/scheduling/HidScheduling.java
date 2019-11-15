package com.flash3388.flashlib.robot.hid.scheduling;

import com.flash3388.flashlib.robot.hid.Hid;

public class HidScheduling {

    private HidScheduling() {}

    public static void addButtonsUpdateTaskToScheduler(Hid hid) {
        hid.buttons().forEach((button) -> new ButtonActivationAction(button).start());
    }
}
