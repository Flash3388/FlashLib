package com.flash3388.flashlib.robot.hid.scheduling;

import com.flash3388.flashlib.robot.hid.Button;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.robot.scheduling.triggers.TriggerActivationAction;

public class ButtonActivationAction extends TriggerActivationAction {

    public ButtonActivationAction(Scheduler scheduler, Button button) {
        super(scheduler, button, button);
    }

    public ButtonActivationAction(Button button) {
        super(button, button);
    }
}
