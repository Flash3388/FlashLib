package com.flash3388.flashlib.hid;

import java.util.function.IntSupplier;

/**
 * Represents a Point Of View on a Human Interface Device.
 * <p>
 *     Also known as a <em>Hat Switch</em>, a POV is a 360 switch indicating direction.
 *     It returns a degree value from 0 to 360 indicating direction.
 * </p>
 *
 * @since FlashLib 3.0.0
 */
public interface Pov extends IntSupplier {

    /**
     * {@inheritDoc}
     * <p>
     *     Gets the value from the POV, ranging from 0 -&gt; 360 if the switch is used,
     *     or <code>-1</code> if the switch is not used.
     * </p>
     */
    @Override
    int getAsInt();

    /**
     * Gets a {@link Button} representation of this {@link Pov}. The button is considered activated
     * (i.e. <b>true</b>) if the switch is used, i.e. {@link #getAsInt()} returns a non-negative
     * value.
     *
     * @return a {@link Button} based on this.
     */
    Button asButton();
}
