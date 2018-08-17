package edu.flash3388.flashlib.util.concurrent;

import edu.flash3388.flashlib.util.Operation;

public interface Locker {

    <R> R run(Operation<R> operation);
}
