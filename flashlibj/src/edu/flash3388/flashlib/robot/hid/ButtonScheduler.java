package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.scheduling.triggers.TriggerScheduler;

public class ButtonScheduler extends TriggerScheduler {

    public ButtonScheduler(Button button) {
        super(button, button);
    }
}
