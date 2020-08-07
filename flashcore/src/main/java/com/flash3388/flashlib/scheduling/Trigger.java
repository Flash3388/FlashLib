package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;

public interface Trigger {
    void whenActive(Action action);
    void cancelWhenActive(Action action);
    void toggleWhenActive(Action action);
    void whileActive(Action action);
    void whenInactive(Action action);
    void cancelWhenInactive(Action action);
}
