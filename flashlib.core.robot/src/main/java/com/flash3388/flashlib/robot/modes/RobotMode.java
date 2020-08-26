package com.flash3388.flashlib.robot.modes;

import com.flash3388.flashlib.scheduling.SchedulerMode;

public interface RobotMode extends SchedulerMode {
    RobotMode DISABLED = create("DISABLED", 0, true);

    String getName();
    int getKey();

    @Override
    boolean isDisabled();

    default boolean equals(RobotMode other) {
        return other != null && getKey() == other.getKey();
    }

    static <T extends RobotMode> T cast(RobotMode robotMode, Class<T> type) {
        if (type.isInstance(robotMode)) {
            return type.cast(robotMode);
        }

        throw new ClassCastException(String.format("Mode is not of type %s", type.getName()));
    }

    static RobotMode create(String name, int key) {
        return create(name, key, false);
    }

    @SuppressWarnings("AnonymousInnerClassWithTooManyMethods")
    static RobotMode create(String name, int key, boolean isDisabled) {
        return new RobotMode() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getKey() {
                return key;
            }

            @Override
            public boolean isDisabled() {
                return isDisabled;
            }
        };
    }
}
