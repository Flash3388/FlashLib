package com.flash3388.flashlib.robot.hid.scheduling;

import com.flash3388.flashlib.robot.scheduling.triggers.TriggerSchedulerTask;
import com.flash3388.flashlib.robot.hid.Button;

public class ButtonSchedulerTask extends TriggerSchedulerTask {

    public ButtonSchedulerTask(Button button) {
        super(button, button);
    }
}
