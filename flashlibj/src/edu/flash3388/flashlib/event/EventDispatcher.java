package edu.flash3388.flashlib.event;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Responsible for registering listeners to events and dispatching calls to them
 * when an event has occurred.
 * <p>
 * Each listener added has a condition, which must be answered by the event occurred for the listener
 * to be dispatched, allowing for selective dispatching.
 * <p>
 * Additionally, when dispatching, it is possible to call only one method from the wanted listener.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.3.0
 */
public class EventDispatcher {

	private Set<ListenerWrapper> mListeners;
	
	public EventDispatcher() {
		mListeners = new HashSet<ListenerWrapper>();
	}
	
	/**
	 * Registers a new listener that can be dispatched.
	 * 
	 * @param eventPredicate a condition which will be tested and must be answered to dispatch this listener.
	 * @param listener listener
	 * @return if the listener does not exist already and was added.
	 */
	public boolean registerListener(Predicate<Event> eventPredicate, Listener listener) {
		synchronized (mListeners) {
			return mListeners.add(new ListenerWrapper(eventPredicate, listener));
		}
	}
	
	/**
	 * Removes a listener from the set of listeners.
	 * 
	 * @param listener listener object to remove
	 * @return true if the object was registered before and was now removed.
	 */
	public boolean unregisterListener(Listener listener) {
		synchronized (mListeners) {
			return mListeners.remove(listener);
		}
	}
	
	/**
	 * Dispatch an event for listeners of the given class by calling the given method.
	 * 
	 * @param listenerCls class of listener to invoke - all listeners of this class will be invoked.
	 * @param event event to dispatch, passing it to all listeners.
	 * @param consumer consumer to dispatch event to, must be a method from the listener class which receives the given event as a parameter.
	 */
	public synchronized <E extends Event, L extends Listener> void dispatch(Class<L> listenerCls, E event, BiConsumer<L, E> consumer) {
		Stream<ListenerWrapper> listenersStream = getListenersForClass(listenerCls);
		listenersStream.forEach(new InvocationConsumer<L, E>(listenerCls, event, consumer));
	}
	
	private <L extends Listener> Stream<ListenerWrapper> getListenersForClass(Class<L> cls) {
		return mListeners.stream().filter((listenerWrapper)-> {
			return cls.isInstance(listenerWrapper.mListener);
		});
	}
	
	private static class ListenerWrapper {
		private Listener mListener;
		private Predicate<Event> mEventPredicate;
		
		ListenerWrapper(Predicate<Event> eventPredicate, Listener listener) {
			mListener = listener;
			mEventPredicate = eventPredicate;
		}
	}
	
	private static class InvocationConsumer<L extends Listener, E extends Event> implements Consumer<ListenerWrapper> {

		private Class<L> mListenerCls;
		private E mEvent;
		private BiConsumer<L, E> mConsumer;
		
		InvocationConsumer(Class<L> listenerCls, E event, BiConsumer<L, E> consumer) {
			mListenerCls = listenerCls;
			mEvent = event;
			mConsumer = consumer;
		}
		
		@Override
		public void accept(ListenerWrapper wrapper) {
			L listener = mListenerCls.cast(wrapper.mListener);
			Predicate<Event> predicate = wrapper.mEventPredicate;
			
			if (predicate.test(mEvent)) {
				mConsumer.accept(listener, mEvent);
			}
		}
	}
}
