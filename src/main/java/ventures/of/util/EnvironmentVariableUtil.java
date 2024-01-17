package ventures.of.util;

import ventures.of.MainProgram;

public class EnvironmentVariableUtil {

    public static Boolean getPropertyBool(String key) {
        return Boolean.valueOf(getPropertyString(key));
    }

    public static String getPropertyString(String key) {/*
        // In override
        String overrideValue = MainProgram.properties.getProperty(key);
        // In system
        String envVariableValue = System.getenv(key);
        return overrideValue != null ? overrideValue : envVariableValue;
        */
        return MainProgram.properties.getProperty(key);
    }
}
