package com.flash3388.flashlib.util;

/**
 * Represents the results from {@link Comparable#compareTo(Object)} or
 * {@link java.util.Comparator#compare(Object, Object)}.
 *
 * @since FlashLib 2.0.0
 */
public enum CompareResult {
    /**
     * If the first value (or this) was greater than the other.
     */
    GREATER_THAN(1),
    /**
     * If the first value (or this) was smaller than the other.
     */
    SMALLER_THAN(-1),
    /**
     * If the first value (or this) was equal to the other.
     */
    EQUAL_TO(0);

    private final int mValue;

    CompareResult(int value) {
        mValue = value;
    }

    /**
     * The sign returned by comparing methods.
     *
     * @return 1, -1 or 0 for greater than, smaller than or equal to.
     */
    public int value() {
        return mValue;
    }

    /**
     * Gets whether {@link #value()} matches the sign of the given value.
     * <p>
     *     Helpful when comparing against results from a compare method:
     * </p>
     * <pre>
     *     int result = something.compareTo(other);
     *     if (CompareResult.EQUAL_TO.is(result)) {
     *         // do something
     *     }
     * </pre>
     *
     * @param value value to compare against.
     *
     * @return <b>true</b> if equal, <b>false</b> otherwise.
     */
    public boolean is(int value) {
        return mValue == Math.signum(value);
    }

    /**
     * Finds the {@link CompareResult} object which matches the given comparing result
     * value.
     * <p>
     *     Can be used directly against a compare results to get the enum value:
     * </p>
     * <pre>
     *     int result = something.compareTo(other);
     *     CompareResult compareResult = CompareResult.forValue(result);
     * </pre>
     *
     * @param value 1, -1 or 0, as produced by {@link Comparable#compareTo(Object)} and
     *  {@link java.util.Comparator#compare(Object, Object)}.
     *
     * @return a {@link CompareResult} whose {@link #value()} matches the given value.
     */
    public static CompareResult forValue(int value) {
        for (CompareResult result : values()) {
            if (result.is(value)) {
                return result;
            }
        }

        throw new EnumConstantNotPresentException(CompareResult.class, String.valueOf(value));
    }

    /**
     * Checks whether the given value matches the {@link #value()} of one of the given {@link CompareResult}.
     * In essence, checking if the given result, is one of the expected possible.
     * <p>
     *     Can be helpful if checking whether or not a compare result matches several options:
     * </p>
     * <pre>
     *     int result = something.compareTo(other);
     *     if (CompareResult.in(result, CompareResult.EQUAL_TO, CompareResult.GREATER_THAN)) {
     *         // Do something
     *     }
     * </pre>
     *
     * @param value value to check.
     * @param possibleResults accepted results.
     *
     * @return <b>true</b> if the given value matches on of the accepted results, <b>false</b> otherwise.
     */
    public static boolean in(int value, CompareResult... possibleResults) {
        for (CompareResult compareResult : possibleResults) {
            if (compareResult.is(value)) {
                return true;
            }
        }

        return false;
    }
}
