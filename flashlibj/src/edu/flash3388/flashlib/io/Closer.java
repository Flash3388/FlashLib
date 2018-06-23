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

	private Collection<Closeable> mCloseables;
	private boolean mOnErrorOnly;
	
	private Closer(Collection<Closeable> closeables, boolean onErrorOnly) {
		mCloseables = closeables;
		mOnErrorOnly = onErrorOnly;
	}
	
	/**
	 * Creates a new {@link Closer} with the given array of {@link Closeable} objects, which
	 * are the resources to be closed after the operation is performed.
	 * 
	 * @param closeables array of resources to close after use.
	 * @return a new {@link Closer} object.
	 */
	public static Closer with(Closeable... closeables) {
		return with(Arrays.asList(closeables));
	}
	
	/**
	 * Creates a new {@link Closer} with the given collection of {@link Closeable} objects, which
	 * are the resources to be closed after the operation is performed.
	 * 
	 * @param closeables collection of resources to close after use.
	 * @return a new {@link Closer} object.
	 */
	public static Closer with(Collection<Closeable> closeables) {
		return new Closer(closeables, false);
	}
	
	/**
	 * Creates a new {@link Closer} with the given array of {@link Closeable} objects, which
	 * are the resources to be closed after the operation is performed, only if an error has occurred.
	 * 
	 * @param closeables array of resources to close after use.
	 * @return a new {@link Closer} object.
	 */
	public static Closer onError(Closeable... closeables) {
		return onError(Arrays.asList(closeables));
	}
	
	/**
	 * Creates a new {@link Closer} with the given collection of {@link Closeable} objects, which
	 * are the resources to be closed after the operation is performed, only if an error has occurred.
	 * 
	 * @param closeables collection of resources to close after use.
	 * @return a new {@link Closer} object.
	 */
	public static Closer onError(Collection<Closeable> closeables) {
		return new Closer(closeables, true);
	}
	
	/**
	 * Adds a new resource to be closed after operation.
	 * 
	 * @param closeable resource to close.
	 * @return this object.
	 */
	public Closer add(Closeable closeable) {
		mCloseables.add(closeable);
		return this;
	}
	
	/**
	 * Run the IO operation and return the result from it. After operation, whether or not it has failed,
	 * all saved resources all be closed by calling {@link Closeable#close()}.
	 * 
	 * @param callable operation callable object.
	 * @return result of the operation, from the callable call.
	 * @throws IOException if an exception was thrown by the callable object.
	 */
	public <R> R run(IORunnable<R> callable) throws IOException {
		try {
			return callable.run();
		} catch (Throwable e) {
			if (mOnErrorOnly) {
				closeResources();
			}
			
			throw e;
		} finally {
			if (!mOnErrorOnly) {
				closeResources();
			}
		}
	}
	
	private void closeResources() throws IOException {
		for (Closeable closeable : mCloseables) {
			closeable.close();
		}
	}
}
