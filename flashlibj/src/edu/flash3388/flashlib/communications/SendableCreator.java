package edu.flash3388.flashlib.communications;

/**
 * Interface for a {@link Sendable} creation object. Used by {@link Communications} to create remotely
 * added sendables.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
@FunctionalInterface
public interface SendableCreator {
	/**
	 * Creates a new {@link Sendable} with the provided name, id and type, if possible.
	 * 
	 * @param name name of the new sendable
	 * @param id id of the new sendable
	 * @param type type of the new sendable
	 * 
	 * @return a new sendable
	 */
	Sendable create(String name, int id, byte type);
}
