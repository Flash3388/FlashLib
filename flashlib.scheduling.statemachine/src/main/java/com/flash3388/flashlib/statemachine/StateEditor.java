package com.flash3388.flashlib.statemachine;

import com.flash3388.flashlib.scheduling.actions.Action;

import java.util.Arrays;
import java.util.Collection;

public interface StateEditor {

    StateEditor allowTransitionTo(Collection<? extends State> states);

    default StateEditor allowTransitionTo(State... states) {
        return allowTransitionTo(Arrays.asList(states));
    }

    ActionAttachmentEditor attachAction(Action action);
}
