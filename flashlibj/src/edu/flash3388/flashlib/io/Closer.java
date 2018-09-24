package edu.flash3388.flashlib.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
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
public class Closer implements Closeable {

	private Collection<Closeable> mCloseables;
	
	private Closer(Collection<Closeable> closeables) {
		mCloseables = closeables;
	}

	public static Closer empty() {
		return new Closer(new ArrayList<Closeable>());
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
		return new Closer(closeables);
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
	 * @param closeOption states when to close the resources
     * @param <R> return type
     *
	 * @return result of the operation, from the callable call.
	 * @throws IOException if an exception was thrown by the callable object.
	 */
	public <R> R run(IORunnable<R> callable, CloseOption closeOption) throws IOException {
		boolean errorOccurred = false;

		try {
			return callable.run();
		} catch (Throwable t) {
			errorOccurred = true;

			throw t;
		} finally {
			if (closeOption.shouldClose(errorOccurred)) {
				close();
			}
		}
	}

	/**
	 * Run the IO operation and return the result from it. After operation, whether or not it has failed,
	 * all saved resources all be closed by calling {@link Closeable#close()}.
	 * <p>
	 * Calls {@link #run(IORunnable, CloseOption)} and passes it {@link CloseOption#CLOSE_ALWAYS}.
	 *
	 * @param callable operation callable object.
     * @param <R> return type
     *
	 * @return result of the operation, from the callable call.
	 * @throws IOException if an exception was thrown by the callable object.
	 */
	public <R> R run(IORunnable<R> callable) throws IOException {
		return run(callable, CloseOption.CLOSE_ALWAYS);
	}

	@Override
	public void close() throws IOException {
		for (Closeable closeable : mCloseables) {
			closeable.close();
		}
	}
}
