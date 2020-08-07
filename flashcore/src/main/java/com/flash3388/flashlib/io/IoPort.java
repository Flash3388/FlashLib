package com.flash3388.flashlib.io;

import com.flash3388.flashlib.util.resources.Resource;

/**
 * Interface for IO ports. Contains a single method {@link #free()} which releases the port from usage.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public interface IoPort extends Resource {

	/**
	 * Frees the port. Releasing all resources and
	 * closing it. The port cannot be used after that.
	 */
	@Override
	void free();
}
