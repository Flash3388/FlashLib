package edu.flash3388.flashlib.event;

import java.util.function.Predicate;

/**
 * An event {@link Predicate} which returns true always.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.3.0
 */
public class TrueEventPredicate implements Predicate<Event> {

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns true always.
	 */
	@Override
	public boolean test(Event t) {
		return true;
	}
}
