package edu.flash3388.flashlib.event;

import java.util.function.BiConsumer;

public class SimpleListenerInvocation implements ListenerInvocation {

	@Override
	public <L, E> void invoke(L listener, E event, BiConsumer<L, E> consumer) {
		consumer.accept(listener, event);
	}
}
