package com.flash3388.flashlib.util.concurrent;

import java.util.concurrent.ThreadFactory;

public interface NamedThreadFactory extends ThreadFactory {

    Thread newThread(String name, Runnable r);
}
