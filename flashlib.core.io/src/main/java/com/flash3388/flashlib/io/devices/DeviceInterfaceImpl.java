package com.flash3388.flashlib.io.devices;

import com.castle.reflect.Types;
import com.castle.reflect.exceptions.TypeException;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;

public class DeviceInterfaceImpl implements DeviceInterface {

    private static final Logger LOGGER = Logging.getMainLogger();

    private final FlashLibMainThread mMainThread;

    public DeviceInterfaceImpl(FlashLibMainThread mainThread) {
        mMainThread = mainThread;
    }

    @Override
    public <T> T newDevice(int id, Class<T> type, Map<String, Object> namedArgs) {
        mMainThread.verifyCurrentThread();

        Class<? extends T> foundType = findType(id, type);
        return newInstance(foundType, namedArgs);
    }

    @Override
    public GroupBuilder<SpeedController, SpeedControllerGroup> newSpeedControllerGroup() {
        mMainThread.verifyCurrentThread();
        return new GroupBuilder<>(this, SpeedControllerGroup::new, SpeedController.class);
    }

    @Override
    public GroupBuilder<Solenoid, SolenoidGroup> newSolenoidGroup() {
        mMainThread.verifyCurrentThread();
        return new GroupBuilder<>(this, SolenoidGroup::new, Solenoid.class);
    }

    @Override
    public GroupBuilder<DoubleSolenoid, DoubleSolenoidGroup> newDoubleSolenoidGroup() {
        mMainThread.verifyCurrentThread();
        return new GroupBuilder<>(this, DoubleSolenoidGroup::new, DoubleSolenoid.class);
    }

    private <T> Class<? extends T> findType(int id, Class<T> type) {
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

    private <T> T newInstance(Class<? extends T> type, Map<String, Object> namedArgs) {
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

    private <T> T newInstance(Class<? extends T> type, Map<String, Object> namedArgs,
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

            args[argIndex++] = loadValue(name, value, parameter.getType());
        }

        //noinspection unchecked
        return (T) constructor.newInstance(args);
    }

    private Object loadValue(String name, Object value, Class<?> wantedType) {
        if (wantedType.isPrimitive()) {
            if (!Types.toWrapperClass(wantedType).isInstance(value)) {
                throw new TypeException("given value does not match actual arg type " + name);
            }

            return value;
        } else {
            if (!wantedType.isInstance(value)) {
                throw new TypeException("given value does not match actual arg type " + name);
            }

            return wantedType.cast(value);
        }
    }
}
