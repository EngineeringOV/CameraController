package ventures.of.util;

import ventures.of.MainProgram;

public class EnvironmentVariableUtil {


    public static String getProperty(String key) {/*
        // In override
        String overrideValue = MainProgram.properties.getProperty(key);
        // In system
        String envVariableValue = System.getenv(key);
        return overrideValue != null ? overrideValue : envVariableValue;
        */
        return MainProgram.properties.getProperty(key);
    }
}
