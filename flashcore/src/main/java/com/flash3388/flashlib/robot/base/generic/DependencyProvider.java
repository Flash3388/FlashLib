package com.flash3388.flashlib.robot.base.generic;

import com.flash3388.flashlib.robot.hid.HidInterface;
import com.flash3388.flashlib.robot.io.IoInterface;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.resources.ResourceHolder;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

public interface DependencyProvider {

    Clock getClock();
    Supplier<? extends RobotMode> getRobotModeSupplier();
    IoInterface getIoInterface();
    HidInterface getHidInterface();
    Scheduler getScheduler();

    static LazyInitializingBuilder cascadingInitializationBuilder(Logger logger, ResourceHolder resourceHolder) {
        return new LazyInitializingBuilder(Arrays.asList(logger, resourceHolder));
    }

    class LazyInitializingBuilder {

        private final Collection<Object> mKnownDependencies;
        private final Collection<Initializer> mInitializers;

        private LazyInitializingBuilder(Collection<Object> knownDependencies) {
            mKnownDependencies = knownDependencies;
            mInitializers = new ArrayList<>();
        }

        public LazyInitializingBuilder add(Initializer initializer) {
            mInitializers.add(initializer);
            return this;
        }
        public LazyInitializingBuilder add(Supplier<Object> initializer) {
            return add((h)-> initializer.get());
        }

        public LazyInitializer build() {
            return new LazyInitializer(new CascadingInitializer(mInitializers, mKnownDependencies));
        }
    }

    class LazyInitializer implements DependencyProvider {

        private final CascadingInitializer mInitializer;
        private DependencyHolder mDependencyHolder;

        public LazyInitializer(CascadingInitializer initializer) {
            mInitializer = initializer;
        }

        @Override
        public Clock getClock() {
            initializeIfNeeded();
            return mDependencyHolder.get(Clock.class);
        }
        @Override
        public Supplier<? extends RobotMode> getRobotModeSupplier() {
            initializeIfNeeded();
            return mDependencyHolder.get(Supplier.class);
        }
        @Override
        public IoInterface getIoInterface() {
            initializeIfNeeded();
            return mDependencyHolder.get(IoInterface.class);
        }
        @Override
        public HidInterface getHidInterface() {
            initializeIfNeeded();
            return mDependencyHolder.get(HidInterface.class);
        }
        @Override
        public Scheduler getScheduler() {
            initializeIfNeeded();
            return mDependencyHolder.get(Scheduler.class);
        }

        private void initializeIfNeeded() {
            if (mDependencyHolder != null) {
                return;
            }

            mDependencyHolder = mInitializer.initialize();
        }
    }
}
