package com.flash3388.flashlib.robot.modes;

public interface RobotMode {
    RobotMode DISABLED = create("DISABLED", 0, true);

    String getName();
    int getKey();
    boolean isDisabled();

    default boolean equals(RobotMode other) {
        return other != null && getKey() == other.getKey();
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
