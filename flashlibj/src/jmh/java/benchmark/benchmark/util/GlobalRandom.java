package benchmark.benchmark.util;

import java.util.Random;

public class GlobalRandom {

    private static final Random RANDOM = new Random();

    public static int nextIntInRange(Range range) {
        return RANDOM.nextInt(range.getMax() + range.getMin()) - range.getMin();
    }
}
