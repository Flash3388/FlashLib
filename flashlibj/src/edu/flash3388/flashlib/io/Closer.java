package edu.flash3388.flashlib.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * A {@link Closer} provides a utility for ensuring IO resources are closed after usage, regardless
 * of whether or not the operation has failed.
 * 
 * An instance can only be used once, since the resources will be closed after that and so won't be useable again.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.3.0
 */
public class Closer {

	private Collection<Closeable> mClosables;
	
	private Closer(Collection<Closeable> closables) {
		mClosables = closables;
	}
	
	public static Closer with(Closeable closeable) {
		return with(closeable);
	}
	
	public static Closer with(Closeable... closeables) {
		return with(Arrays.asList(closeables));
	}
	
	public static Closer with(Collection<Closeable> closeables) {
		return new Closer(closeables);
	}
	
	public Closer add(Closeable closeable) {
		mClosables.add(closeable);
		return this;
	}
	
	public <R> R run(IOCallable<R> callable) throws IOException {
		try {
			return callable.call();
		} finally {
			for (Closeable closeable : mClosables) {
				closeable.close();
			}
		}
	}
}
