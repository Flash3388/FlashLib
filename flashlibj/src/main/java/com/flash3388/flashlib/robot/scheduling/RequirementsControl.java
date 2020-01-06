package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.scheduling.actions.Action;

interface RequirementsControl {
    void updateRequirementsNoCurrentAction(Action action);
    void updateRequirementsWithNewRunningAction(Action action);
}
