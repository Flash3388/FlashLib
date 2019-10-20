package com.flash3388.flashlib.robot.hid.scheduling;

import com.flash3388.flashlib.robot.hid.Hid;
import com.flash3388.flashlib.robot.scheduling.Scheduler;

public class HidScheduling {

    private HidScheduling() {}

    public static void addButtonsUpdateTaskToScheduler(Hid hid, Scheduler scheduler) {
        hid.buttons().forEach((button) -> {
            scheduler.add(new ButtonActivationAction(button));
        });
    }
}
