package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.scheduling.triggers.TriggerSchedulerTask;

public class ButtonSchedulerTask extends TriggerSchedulerTask {

    public ButtonSchedulerTask(Button button) {
        super(button, button);
    }
}
