package ventures.of.util;

import java.util.function.Function;

public class FunctionUtil {

    public static <P, R> R applyIfExists(Function<P, R> function, P param) {
        R retVal = null;
        if (function != null) {
            retVal = function.apply(param);
        }
        return retVal;
    }

    public static <A> A rotateArray(A[] array, A current) {
        if(array[array.length-1].equals(current)){
            return array[0];
        }

        for (int i = 0; i < array.length - 1; i++) {
            if(array[i].equals(current)) {
                return array[i+1];
            }
        }
        return array[0];
    }
}
