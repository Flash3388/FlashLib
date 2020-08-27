package com.flash3388.flashlib.control;

/**
 * Defines a direction of motion.
 * <p>
 *     Can be used with conjecture of absolute speeds.
 *     <pre>
 *         <b>double</b> absoluteSpeed = 0.2;
 *         Direction direction = Direction.FORWARD;
 *
 *         <b>double</b> nonAbsolute = absoluteSpeed * direction.sign();
 *     </pre>
 * </p>
 *
 * @since FlashLib 2.0.0
 */
public enum Direction {
    /**
     * Forward motion direction, i.e. positive speed values.
     * Also a synonym for motion to the right.
     */
    FORWARD(true),
    /**
     * Backward motion direction, i.e. negative speed values.
     * Also a synonym for motion to the left.
     */
    BACKWARD(false);

    private final boolean mBooleanValue;

    Direction(boolean booleanValue) {
        mBooleanValue = booleanValue;
    }

    /**
     * Gets the expected signum function value for a speed in this direction.
     * <p>
     *     Can be used together with non-absolute speeds to create absolute speeds. For example:
     * </p>
     *
     * @return <code>1</code> or <code>-1</code>, based on the direction.
     */
    public int sign() {
        return mBooleanValue ? 1 : -1;
    }

    /**
     * Gets the <b>boolean</b> representation of the direction, based on the expected signum
     * value of speeds in this direction. i.e. <b>true</b> for positive, <b>false</b> for negative.
     *
     * @return  <b>true</b> for direction with positive speeds, <b>false</b> for direction with negative speed.
     */
    public boolean booleanValue() {
        return mBooleanValue;
    }
}
