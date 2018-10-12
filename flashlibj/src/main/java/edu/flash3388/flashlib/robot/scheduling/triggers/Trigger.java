package edu.flash3388.flashlib.robot.scheduling.triggers;

import edu.flash3388.flashlib.robot.scheduling.Action;

import java.util.ArrayList;
import java.util.Collection;

public class Trigger {

    private final Collection<TriggerStateHandler> mTriggerStateHandlers;

    public Trigger() {
        this(new ArrayList<>());
    }

    public Trigger(Collection<TriggerStateHandler> triggerStateHandlers) {
        mTriggerStateHandlers = triggerStateHandlers;
    }
    
    public void addStateHandler(TriggerStateHandler handler) {
        mTriggerStateHandlers.add(handler);
    }

    public void whenActive(Action action) {
        addStateHandler((state)-> {
            if (state == TriggerState.ACTIVE) {
                action.start();
            }
        });
    }

    public void cancelWhenActive(Action action) {
        addStateHandler((state)-> {
            if (state == TriggerState.ACTIVE) {
                action.cancel();
            }
        });
    }

    public void whileActive(Action action) {
        addStateHandler((state)-> {
            if (state == TriggerState.ACTIVE) {
                action.start();
            } else {
                action.cancel();
            }
        });
    }

    public void whenInactive(Action action) {
        addStateHandler((state)-> {
            if (state == TriggerState.INACTIVE) {
                action.start();
            }
        });
    }

    public void cancelWhenInactive(Action action) {
        addStateHandler((state)-> {
            if (state == TriggerState.INACTIVE) {
                action.cancel();
            }
        });
    }

    public void activate() {
        handleState(TriggerState.ACTIVE);
    }

    public void deactivate() {
        handleState(TriggerState.INACTIVE);
    }

    private void handleState(TriggerState state) {
        for (TriggerStateHandler handler : mTriggerStateHandlers) {
            handler.handleState(state);
        }
    }
}
