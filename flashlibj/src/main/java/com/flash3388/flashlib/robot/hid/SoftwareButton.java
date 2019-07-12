package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.robot.scheduling.Action;

public abstract class SoftwareButton extends Button {

    @Override
    public void whenPressed(Action action) {
        whenActive(action);
    }

    @Override
    public void whileHeld(Action action) {
        whileActive(action);
    }
}
