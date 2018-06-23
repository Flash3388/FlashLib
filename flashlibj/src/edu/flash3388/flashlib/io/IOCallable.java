package edu.flash3388.flashlib.io;

import java.io.IOException;

/**
 * An interface for operations that can be executed with IO utilities.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.3.0
 * 
 * @param <R> operation result type
 */
@FunctionalInterface
public interface IOCallable<R> {

	/**
	 * Calls the IO operation to be executed.
	 * 
	 * @return result of the operation.
	 * @throws IOException exception in the operation.
	 */
	R call() throws IOException;
}
