package com.flash3388.flashlib.robot.hid.scheduling;

import com.flash3388.flashlib.robot.hid.Button;
import com.flash3388.flashlib.robot.hid.Hid;
import com.flash3388.flashlib.robot.scheduling.Scheduler;

public class HidScheduling {

    private HidScheduling() {}

    public static void addButtonsUpdateTaskToScheduler(Hid hid, Scheduler scheduler) {
        for (int buttonIdx = 0; buttonIdx < hid.getButtonCount(); buttonIdx++) {
            Button button = hid.getButton(buttonIdx);
            scheduler.add(new ButtonSchedulerTask(button));
        }
    }
}
