package ventures.of.util;

public class MathUtil {
    public static int minMax(int value, int min, int max) {
        int retVal = Math.max(value, min);
        retVal = Math.min(retVal, max);

        return retVal;
    }

    public static int incrementByAndReturnAround(int newValue, int min, int max) {
        if (newValue > max) {
            return min;
        }
        else if (newValue < min) {
            return max;
        }
        return newValue;
    }
}
