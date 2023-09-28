package com.flash3388.flashlib.scheduling;

/**
 * Represents a requirement of {@link ActionInterface Actions}.
 * When implementing this, an object may be declared as a requirement with
 * {@link ActionInterface#requires(Requirement...)}.
 * <p>
 *     As specified by the scheduler, there can be only one action running at any given time which a specific requirement.
 *     Thus, a case where 2 actions have the same requirement declared <b>and</b> are both running is not possible.
 * </p>
 * <p>
 *     This is a symbolic interface, such that there are no methods to implement.
 * </p>
 *
 * @since FlashLib 3.0.0
 */
public interface Requirement {
}
