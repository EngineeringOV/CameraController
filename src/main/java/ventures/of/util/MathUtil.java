package ventures.of.util;

public class MathUtil {
    public static int minMax(int value, int min, int max) {
        int realMin = min, retVal = value;
        if (realMin > max ) {
            realMin = max;
        }

        if(retVal <= realMin ) {
            retVal = realMin;
        }
        else if (retVal >= max) {
            retVal = max;
        }
        return retVal;
    }

    //todo rewrite this trash
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
