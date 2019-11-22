package benchmark.util;

public class Range {

    private final int mMin;
    private final int mMax;

    public Range(int min, int max) {
        mMin = min;
        mMax = max;
    }

    public int getMin() {
        return mMin;
    }

    public int getMax() {
        return mMax;
    }
}
