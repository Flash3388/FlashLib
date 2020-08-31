package com.flash3388.flashlib.robot.modes;

import com.flash3388.flashlib.scheduling.SchedulerMode;

/**
 * Describes a control mode for the robot.
 * <p>
 *     Control modes, allow users to define different behaviours, and execute
 *     them by selecting a specific mode.
 * </p>
 *
 * @since FlashLib 1.0.0
 */
public interface RobotMode extends SchedulerMode {
    RobotMode DISABLED = create("DISABLED", 0, true);

    /**
     * Get the name of the mode.
     *
     * @return name of the mode
     */
    String getName();

    /**
     * Gets the key of the mode. The key is a unique identifier for the mode.
     * Each mode should define a unique key that allows identifying them.
     *
     * @return key
     */
    int getKey();

    /**
     * Gets whether or not the current mode is a <em>disabled</em> mode.
     * Such modes require that no motion should be made by the robot. This
     * is entirely a safety concern, that allows users to be certain that when
     * the robot is in <em>disabled</em> they can interact with it directly without
     * concern.
     *
     * @return <b>true</b> if requires a disable behaviour, <b>false</b>
     * otherwise.
     */
    @Override
    boolean isDisabled();

    /**
     * Checks whether or not the given mode is equal to this one,
     * This is done by checking the unique identifier returned from {@link #getKey()}.
     *
     * @param other mode to check against.
     *
     * @return <b>true</b> if both objects describe the same mode.
     */
    default boolean equals(RobotMode other) {
        return other != null && getKey() == other.getKey();
    }

    /**
     * Casts the {@link RobotMode} to the requested type.
     * <p>
     *     Should be used by implementations of robot bases to cast the given
     *     object to the wanted type, allowing access to more information that
     *     may be contained in the implementation.
     * </p>
     * <pre>
     *     void handleMode(RobotMode mode) {
     *         MyRobotMode myMode = RobotMode.cast(mode, MyRobotMode.class);
     *         myMode.getData();
     *     }
     * </pre>
     *
     * @param mode the mode object.
     * @param type the wanted class type.
     * @param <T> type parameter indicating the wanted class.
     *
     * @return a casted {@link RobotMode}.
     *
     * @throws ClassCastException if the given instance cannot be casted to the wanted type.
     */
    static <T extends RobotMode> T cast(RobotMode mode, Class<T> type) {
        if (type.isInstance(mode)) {
            return type.cast(mode);
        }

        throw new ClassCastException(String.format("Mode is not of type %s", type.getName()));
    }

    /**
     * Creates a new {@link RobotMode} instance describing a specific mode. The
     * created mode will not describe a <em>disabled</em> mode. See {@link #create(String, int, boolean)}.
     *
     * @param name name of the mode.
     * @param key unique identifier of the mode, as described by {@link #getKey()}.
     *
     * @return new {@link RobotMode} describing a specific mode.
     */
    static RobotMode create(String name, int key) {
        return create(name, key, false);
    }

    /**
     * Creates a new {@link RobotMode} instance describing a specific mode.
     *
     * @param name name of the mode.
     * @param key unique identifier of the mode, as described by {@link #getKey()}.
     * @param isDisabled <b>true</b> to make the mode a disabled mode, <b>false</b> otherwise.
     *
     * @return new {@link RobotMode} describing a specific mode.
     */
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
