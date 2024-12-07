package ventures.of.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ventures.of.util.JsonBuilder;
import ventures.of.util.StringUtil;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CameraSettings {
    // Marked speed to shutter time basically follows fps calculation according to https://www.flutotscamerarepair.com/Shutterspeed.htm
    // but seems to be off by an unknown factor
    // ISO = 100 * 2^(gain)
    //    // ISO = 100 * 2^2 = 400
    private ValueWithIndex shutterTime = new ValueWithIndex(0, 0, new Number[]{-1L, 1L, 50L, 25_000L, 100_000L, 150_000L, 200_000L, 500_000L, 1_000_000L, 10_000_000L, 100_000_000L});
    private ValueWithIndex gain = new ValueWithIndex(1, 0.5, 1, 2.5, 5, 7.5, 10, 15, 20);
    private ValueWithIndex saturation = new ValueWithIndex(2, 0.5, 0.75, 1, 1.25, 1.5, 2);
    private ValueWithIndex sharpness = new ValueWithIndex(2, 0.5, 0.75, 1.0, 1.25, 1.5, 2.0);
    private ValueWithIndex contrast = new ValueWithIndex(2, 0.5, 0.75, 1.0, 1.25,1.5, 2.0, 3.0, 5);
    private ValueWithIndex framerate = new ValueWithIndex(1, 24.0, 30.0, 60.0, 90.0, 120.0);
    private ValueWithIndex tlTimeBetween = new ValueWithIndex(2, 1_000L,2_000L, 5_000L, 10_000L, 30_000L, 60_000L, 120_000L, 600_000L);
    private ValueWithIndex[] settings = {shutterTime, gain, tlTimeBetween, saturation, sharpness, contrast, framerate};

    public String toJson() {
        return toJson(0);
    }
        public String toJson(int indentLevel) {
        return JsonBuilder.builder().setIndentLevel(indentLevel)
                .jsonAddStringProperty("shutterTime", StringUtil.formatIntoShorterString(((long) shutterTime.getActualValue())))
                .jsonAddStringProperty("gain", gain.getActualValue())
                .jsonAddStringProperty("saturation", saturation.getActualValue())
                .jsonAddStringProperty("sharpness", sharpness.getActualValue())
                .jsonAddStringProperty("framerate", framerate.getActualValue())
                .jsonAddStringProperty("tlTimeBetween", StringUtil.formatIntoShorterString(((long) tlTimeBetween.getActualValue())))
                .jsonEndObject().build();
    }

}
