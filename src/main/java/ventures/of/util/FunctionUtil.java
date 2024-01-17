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

}
