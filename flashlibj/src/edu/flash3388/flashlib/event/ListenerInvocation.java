package edu.flash3388.flashlib.event;

import java.util.function.BiConsumer;

public interface ListenerInvocation {

	<L, E> void invoke(L listener, E event, BiConsumer<L, E> consumer);
}
