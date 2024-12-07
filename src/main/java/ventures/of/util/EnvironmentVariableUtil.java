package ventures.of.util;

import ventures.of.MainProgram;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static ventures.of.util.FileUtil.*;

public class EnvironmentVariableUtil {

    public static void readerFromConfigWithFallback(Properties properties) throws URISyntaxException, IOException {
        String configPath = new File(FileUtil.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath() + "/config.txt";
        File configFile = new File(configPath);
        BufferedReader reader =  readerFileFromJar("default.config.properties");
        properties.load(reader);
        if(configFile.exists()) {
            properties.load(readerFromFile(configPath));
        } else {
            copyFile(reader, configPath);
        }
    }

    public static Boolean getPropertyBool(String key) {
        return Boolean.valueOf(getPropertyString(key));
    }

    public static int getPropertyInt(String key) {
        return Integer.parseInt(getPropertyString(key));
    }

    public static long getPropertyLong(String key) {
        return Long.parseLong(getPropertyString(key));
    }

    public static String getPropertyString(String key) {
        return MainProgram.properties.getProperty(key).toUpperCase(Locale.ROOT);
    }

    public static List<String> getPropertyStringArrayList(String key) {
        String valueAsClumpString = MainProgram.properties.getProperty(key).toUpperCase(Locale.ROOT);
        if(valueAsClumpString.startsWith("[") && valueAsClumpString.endsWith("]")) {
            valueAsClumpString = valueAsClumpString.substring(1, valueAsClumpString.length()-1);
            return Arrays.stream((valueAsClumpString.split(","))).map(String::trim).collect(Collectors.toList());
        }
        else {
            return Collections.singletonList(valueAsClumpString);
        }
    }
}
