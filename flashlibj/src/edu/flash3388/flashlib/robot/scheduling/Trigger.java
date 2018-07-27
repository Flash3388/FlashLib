package edu.flash3388.flashlib.robot.scheduling;

import java.util.ArrayList;
import java.util.List;

public class Trigger {

    private final List<TriggerStateHandler> mTriggerStateHandlers;

    public Trigger() {
        mTriggerStateHandlers = new ArrayList<TriggerStateHandler>();
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

    public void cancelWhenInActive(Action action) {
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
