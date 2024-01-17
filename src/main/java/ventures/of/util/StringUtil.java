package ventures.of.util;

import lombok.extern.slf4j.Slf4j;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
public class StringUtil {

    public static String formatIntoShorterString(long value) {
        return formatIntoShorterString(value, NumberNotations.MATH_NOTATION_SUFFIXES.getMap());
    }

    public static String formatIntoShorterString(long value, NotationMap<Long, String> notation) {
        short maxDecimals = 2;
        Map.Entry<Long, String> closestLowerSuffix = notation.floorEntry(value);
        //A simple hack for negative values, recursively running this method with the value as the absolute and adding a "-" to the string that is returned from the top call
        if (value < 0) {
            return "-" + formatIntoShorterString(-value);
        }
        //When there's no suffix then don't use one
        else if (closestLowerSuffix == null) {
            return Long.toString(value);
        }
        double divideBy = closestLowerSuffix.getKey();
        String suffix = closestLowerSuffix.getValue();
        String truncated = truncateToXDecimals(value / divideBy, maxDecimals);

        return truncated + suffix;
    }

    public static String getCurrentTimeShort(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ssSSS");
        return now.format(formatter);
    }

    public static String getCurrentTime(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
        return now.format(formatter);
    }

    public static void printVerbose(String text, boolean verbose) {
        if(verbose){
            log.info(text);
        }
    }

    // Removes unnecessary decimals as a nice bonus
    public static String truncateToXDecimals(double raw, int decimals) {
        StringBuilder pattern = new StringBuilder("#.");
        for (int i = 0; i < decimals; i++) {
            pattern.append("#");
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        df.setRoundingMode(RoundingMode.FLOOR);

        return df.format(raw);
    }

}
