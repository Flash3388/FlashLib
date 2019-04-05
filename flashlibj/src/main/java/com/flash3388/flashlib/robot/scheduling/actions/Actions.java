package com.flash3388.flashlib.robot.scheduling.actions;

import com.flash3388.flashlib.robot.scheduling.Action;

public class Actions {

    private Actions() {}

    /**
     * Creates an action which does nothing.
     *
     * @return an empty action.
     */
    public static Action empty() {
        return new Action() {
            @Override
            protected void execute() {
            }

            @Override
            protected void end() {
            }
        };
    }

    /**
     * Creates a canceling action for an action. This is an {@link InstantAction} which calls {@link Action#cancel()}
     * for a given action when started.
     *
     * @param action action to cancel
     * @return canceling action
     */
    public static Action stopAction(Action action){
        return new InstantAction(){
            @Override
            public void execute() {
                if(action.isRunning())
                    action.cancel();
            }
        };
    }

    public static Action instantAction(Runnable runnable) {
        return new InstantAction() {
            @Override
            protected void execute() {
                runnable.run();
            }
        };
    }
}
