package edu.flash3388.flashlib.robot.hid.scheduling;

import edu.flash3388.flashlib.robot.hid.Button;
import edu.flash3388.flashlib.robot.scheduling.triggers.TriggerSchedulerTask;

public class ButtonSchedulerTask extends TriggerSchedulerTask {

    public ButtonSchedulerTask(Button button) {
        super(button, button);
    }
}
