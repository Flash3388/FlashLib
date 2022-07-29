package com.flash3388.flashlib.statemachine;

import java.util.Arrays;
import java.util.Collection;

public interface ActionAttachmentEditor {

    ActionAttachmentEditor transitionOnFinish(Collection<? extends State> states);

    default ActionAttachmentEditor transitionOnFinish(State... states) {
        return transitionOnFinish(Arrays.asList(states));
    }

    StateEditor runWhileStateActive();
}
