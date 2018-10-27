package edu.flash3388.flashlib.robot.hid.scheduling;

import edu.flash3388.flashlib.robot.hid.Button;
import edu.flash3388.flashlib.robot.hid.Hid;
import edu.flash3388.flashlib.robot.scheduling.Scheduler;

public class HidScheduling {

    private HidScheduling() {}

    public static void addButtonsUpdateTaskToScheduer(Hid hid, Scheduler scheduler) {
        for (int buttonIdx = 0; buttonIdx < hid.getButtonCount(); buttonIdx++) {
            Button button = hid.getButton(buttonIdx);
            scheduler.add(new ButtonSchedulerTask(button));
        }
    }
}
