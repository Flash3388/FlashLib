package com.flash3388.flashlib.scheduling.actions;

import java.util.Collection;

public interface ActionGroup extends Action {

    ActionGroup add(Action action);
    ActionGroup add(Action... actions);
    ActionGroup add(Collection<Action> actions);
}
