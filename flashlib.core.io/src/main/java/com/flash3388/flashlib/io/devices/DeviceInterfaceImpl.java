package com.flash3388.flashlib.io.devices;

import com.castle.reflect.exceptions.TypeException;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.function.Function;

public class DeviceInterfaceImpl implements DeviceInterface {

    private static final Logger LOGGER = Logging.getMainLogger();

    private final FlashLibMainThread mMainThread;

    public DeviceInterfaceImpl(FlashLibMainThread mainThread) {
        mMainThread = mainThread;
    }

    @Override
    public <T> T newDevice(String id, Class<T> type, Map<String, Object> namedArgs) {
        mMainThread.verifyCurrentThread();

        Class<? extends T> foundType = findType(id, type);
        return newInstance(foundType, namedArgs);
    }

    @Override
    public <E, T extends DeviceGroup<E>> GroupBuilder<E, T> newGroup(Class<T> groupType, Function<List<E>, T> creator) {
        mMainThread.verifyCurrentThread();
        return new GroupBuilder<>(creator);
    }

    private <T> Class<? extends T> findType(String id, Class<T> type) {
        ThrowableChain chain = Throwables.newChain();

        ServiceLoader<DeviceProvider> providers = ServiceLoader.load(DeviceProvider.class);
        for (DeviceProvider provider : providers) {
            try {
                Class<? extends T> found = provider.findDevice(id, type);
                LOGGER.info("Found device {} with type {}", id, found.getName());

                return found;
            } catch (NoSuchElementException | TypeException e) {
                chain.chain(e);
            }
        }

        if (!chain.getTopThrowable().isPresent()) {
            throw new NoSuchElementException(id + " not found");
        }

        chain.throwIfType(NoSuchElementException.class);
        chain.throwIfType(TypeException.class);
        throw new AssertionError("should not have reached here");
    }

    private static <T> T newInstance(Class<? extends T> type, Map<String, Object> namedArgs) {
        ThrowableChain chain = Throwables.newChain();

        for (Constructor<?> constructor : type.getConstructors()) {
            try {
                T instance = newInstance(type, namedArgs, constructor);
                LOGGER.info("Created new device for type={} with constructor {}",
                        type.getName(), constructor);

                return instance;
            } catch (TypeException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                chain.chain(e);
            }
        }

        chain.throwIfType(TypeException.class);
        throw new AssertionError("should not have reached here");
    }

    private static <T> T newInstance(Class<? extends T> type, Map<String, Object> namedArgs,
                                     Constructor<?> constructor)
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (constructor.getAnnotation(DeviceConstructor.class) == null) {
            throw new TypeException("contractor is not supported for use");
        }
        if (constructor.getParameterCount() != namedArgs.size()) {
            throw new TypeException("constructor does not match passed args");
        }

        Object[] args = new Object[constructor.getParameterCount()];
        int argIndex = 0;

        for (Parameter parameter : constructor.getParameters()) {
            NamedArg namedArg = parameter.getAnnotation(NamedArg.class);
            if (namedArg == null) {
                throw new TypeException("constructor has parameter with NamedArg");
            }

            String name = namedArg.value();
            Object value = namedArgs.get(name);
            if (value == null) {
                throw new TypeException("no argument given for constructor arg " + name);
            }
            if (!parameter.getType().isInstance(value)) {
                throw new TypeException("given value does not match actual arg type " + name);
            }

            args[argIndex++] = value;
        }

        //noinspection unchecked
        return (T) constructor.newInstance(args);
    }
}
