package com.flash3388.flashlib.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * Interface for IO ports. Contains a single method {@link #close()} which releases the port from usage.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public interface IoPort extends Closeable {

	/**
	 * Frees the port. Releasing all resources and
	 * closing it. The port cannot be used after that.
	 */
    @Override
    void close() throws IOException;
}
