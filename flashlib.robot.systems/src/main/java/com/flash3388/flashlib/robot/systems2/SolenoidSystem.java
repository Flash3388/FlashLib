package com.flash3388.flashlib.robot.systems2;

import com.flash3388.flashlib.io.devices.Solenoid;
import com.flash3388.flashlib.io.devices.SolenoidGroup;
import com.flash3388.flashlib.robot.systems2.actions.CloseValve;
import com.flash3388.flashlib.robot.systems2.actions.OpenValve;
import com.flash3388.flashlib.robot.systems2.actions.ToggleValve;
import com.flash3388.flashlib.robot.systems.Valve;
import com.flash3388.flashlib.scheduling.Subsystem;
import com.flash3388.flashlib.scheduling.actions.Action;

public class SolenoidSystem extends Subsystem implements Piston {

    private final Valve mInterface;

    public SolenoidSystem(Valve valve) {
        mInterface = valve;
    }

    public SolenoidSystem(Solenoid solenoid) {
        this(new Interface(solenoid));
    }

    public SolenoidSystem(Solenoid... solenoids) {
        this(new SolenoidGroup(solenoids));
    }

    @Override
    public Action open() {
        return new OpenValve(mInterface)
                .requires(this);
    }

    @Override
    public Action close() {
        return new CloseValve(mInterface)
                .requires(this);
    }

    @Override
    public Action toggle() {
        return new ToggleValve(mInterface)
                .requires(this);
    }

    @Override
    public boolean isOpen() {
        return mInterface.isOpen();
    }

    public static class Interface implements
            com.flash3388.flashlib.robot.systems.Valve {

        private final Solenoid mSolenoid;

        public Interface(Solenoid solenoid) {
            mSolenoid = solenoid;
        }

        @Override
        public void open() {
            mSolenoid.set(true);
        }

        @Override
        public void close() {
            mSolenoid.set(false);
        }

        @Override
        public boolean isOpen() {
            return mSolenoid.get();
        }
    }
}
