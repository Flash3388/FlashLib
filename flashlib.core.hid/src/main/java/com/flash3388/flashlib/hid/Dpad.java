package com.flash3388.flashlib.hid;

import java.util.Arrays;

/**
 * Represents a Direction Pad on a Human Interface Device.
 * <p>
 *     A DPad is a specialized {@link Pov} limited to specific direction: up, down, left right
 *     and combinations between them.
 *
 *     It returns a degree value from 0 to 360 indicating direction, where:
 * </p>
 * <ul>
 *     <li>Up = 360/0</li>
 *     <li>Down = 180</li>
 *     <li>Right = 90</li>
 *     <li>Left = 270</li>
 * </ul>
 * <p>
 *     Several direction may be active at the same time, if they do not cancel each other. This will be indicated
 *     in {@link #getAsInt()} by a direction which is in the middle of both.
 * </p>
 *
 * @since FlashLib 3.0.0
 */
public interface Dpad extends Pov {

    /**
     * Gets the button representation of the <em>up</em> direction. This button
     * will be considered <em>active</em> when the {@link #getAsInt()} value
     * represents the upwards direction.
     *
     * @return a {@link Button} representing UP.
     */
    Button up();

    /**
     * Gets the button representation of the <em>down</em> direction. This button
     * will be considered <em>active</em> when the {@link #getAsInt()} value
     * represents the upwards direction.
     *
     * @return a {@link Button} representing DOWN.
     */
    Button down();

    /**
     * Gets the button representation of the <em>left</em> direction. This button
     * will be considered <em>active</em> when the {@link #getAsInt()} value
     * represents the upwards direction.
     *
     * @return a {@link Button} representing LEFT.
     */
    Button left();

    /**
     * Gets the button representation of the <em>right</em> direction. This button
     * will be considered <em>active</em> when the {@link #getAsInt()} value
     * represents the upwards direction.
     *
     * @return a {@link Button} representing RIGHT.
     */
    Button right();

    /**
     * Gets all the button representations of the directions of the DPad.
     * This includes: up, down, left, and right.
     *
     * @return up, down, left, and right buttons.
     */
    default Iterable<Button> buttons() {
        return Arrays.asList(up(), down(), left(), right());
    }
}
