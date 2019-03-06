package com.flash3388.flashlib.robot.scheduling;

public enum SchedulerRunMode {
    DISABLED {
        @Override
        boolean shouldRunTasks() {
            return false;
        }

        @Override
        boolean shouldRunActions() {
            return false;
        }
    },
    TASKS_ONLY {
        @Override
        boolean shouldRunTasks() {
            return true;
        }

        @Override
        boolean shouldRunActions() {
            return false;
        }
    },
    ACTIONS_ONLY {
        @Override
        boolean shouldRunTasks() {
            return false;
        }

        @Override
        boolean shouldRunActions() {
            return true;
        }
    },
    ALL {
        @Override
        boolean shouldRunTasks() {
            return true;
        }

        @Override
        boolean shouldRunActions() {
            return true;
        }
    };

    SchedulerRunMode() {
    }

    abstract boolean shouldRunTasks();
    abstract boolean shouldRunActions();
}
