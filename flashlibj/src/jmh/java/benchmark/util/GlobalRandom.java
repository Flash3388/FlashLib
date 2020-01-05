package benchmark.util;

import java.util.Random;

public final class GlobalRandom {

    private static final Random RANDOM = new Random();

    private GlobalRandom() {
    }

    public static int nextIntInRange(Range range) {
        return RANDOM.nextInt(range.getMax() - range.getMin() + 1) + range.getMin();
    }
}
