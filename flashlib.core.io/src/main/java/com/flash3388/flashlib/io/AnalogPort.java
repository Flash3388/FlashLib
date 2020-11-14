package com.flash3388.flashlib.io;

public interface AnalogPort extends IoPort {

    /**
     * Gets the maximum voltage of the analog port.
     *
     * @return maximum voltage on the port in volts.
     */
    double getMaxVoltage();

    /**
     * Gets the maximum raw value of the port. This value corresponds to
     * a voltage value and depends on the used implementation.
     *
     * @return maximum raw value.
     */
    int getMaxValue();

    /**
     * Converts a given voltage in volts to an analog value according to this port's analog
     * configuration.
     * <p>
     * Conversion is done by dividing the given voltage by {@link #getMaxVoltage()} and multiplying the
     * result by {@link #getMaxValue()}.
     *
     * @param volts voltage in volts to convert
     * @return analog value corresponding to the given voltage.
     */
    default int voltsToValue(double volts){
        return (int) (volts / getMaxVoltage() * getMaxValue());
    }

    /**
     * Converts a given analog value to voltage in volts according to this port's analog
     * configuration.
     * <p>
     * Conversion is done by dividing the given value by {@link #getMaxValue()} and multiplying the
     * result by {@link #getMaxVoltage()}.
     *
     * @param value analog input value to convert
     * @return analog voltage corresponding to the given value.
     */
    default double valueToVolts(int value){
        return ((double)value /  (double)getMaxValue() * getMaxVoltage());
    }
}
