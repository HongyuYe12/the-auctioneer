package util;

import java.util.Random;

/**
 * Random Utility Class
 * Provides a unified encapsulation of random logic to avoid repeated Math.random() calls in controllers.
 */
public final class RandomUtil {

    private static final Random RANDOM = new Random();
    // Global unique Random instance to avoid performance issues from frequent new Random() calls.
    // All random operations use the same instance.

    private RandomUtil() {}
    // Private constructor to prevent instantiation (typical utility class pattern).

    /**
     * Returns a random integer in the range [min, max] (inclusive).
     */
    public static int randomInt(int min, int max) {

        return RANDOM.nextInt(max - min + 1) + min;
        // nextInt(n) generates [0, n-1], so + min shifts to [min, max].
    }

    /**
     * Returns a random double in the range [0.0, 1.0).
     */
    public static double randomDouble() {

        return RANDOM.nextDouble();
    }

    /**
     * Returns a random double in the range [min, max].
     */
    public static double randomDouble(double min,
                                      double max) {

        return min + (max - min) * RANDOM.nextDouble();
        // Maps [0,1] → [min,max].
    }
}