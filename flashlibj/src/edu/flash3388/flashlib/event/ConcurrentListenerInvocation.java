package edu.flash3388.flashlib.event;

import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

public class ConcurrentListenerInvocation implements ListenerInvocation {

	private ExecutorService mExecutorService;
	
	public ConcurrentListenerInvocation(ExecutorService executorService) {
		mExecutorService = executorService;
	}
	
	@Override
	public <L, E> void invoke(L listener, E event, BiConsumer<L, E> consumer) {
		mExecutorService.submit(new InvokeRunnable<L, E>(listener, event, consumer));
	}
	
	private static class InvokeRunnable<L, E> implements Runnable {

		private L mListener;
		private E mEvent;
		private BiConsumer<L, E> mConsumer;
		
		InvokeRunnable(L listener, E event, BiConsumer<L, E> consumer) {
			mListener = listener;
			mEvent = event;
			mConsumer = consumer;
		}
		
		@Override
		public void run() {
			mConsumer.accept(mListener, mEvent);
		}
	}
}
