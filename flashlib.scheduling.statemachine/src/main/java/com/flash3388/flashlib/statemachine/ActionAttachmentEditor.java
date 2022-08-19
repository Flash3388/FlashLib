package com.flash3388.flashlib.statemachine;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.BooleanSupplier;

public interface ActionAttachmentEditor {

    ActionAttachmentEditor transitionOnFinish(BooleanSupplier condition, Collection<? extends State> states);

    default ActionAttachmentEditor transitionOnFinish(BooleanSupplier condition, State... states) {
        return transitionOnFinish(condition, Arrays.asList(states));
    }

    ActionAttachmentEditor transitionOnFinish(Collection<? extends State> states);

    default ActionAttachmentEditor transitionOnFinish(State... states) {
        return transitionOnFinish(Arrays.asList(states));
    }

    StateEditor runWhileStateActive();
}
