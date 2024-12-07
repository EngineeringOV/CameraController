package ventures.of.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ventures.of.util.MathUtil;

import java.util.Arrays;

//todo figure out if it's better to have this be a string but allow numbers in and then transform that to strings
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ValueWithIndex {
    private int index = 0, defaultIndex = 0;
    private Number[] values = {};

    public ValueWithIndex(int index, Number... values) {
        this.index = index;
        this.defaultIndex = index;
        this.values = values;
    }

    public boolean isFloatArray() {
        return Arrays.stream(values).anyMatch(e -> e instanceof Float || e instanceof Double);
    }

    public Number getActualValue() {
        return values[index];
    }

    public Void incrementIndexBy(int indexChange) {
        index = MathUtil.incrementByAndReturnAround(index + indexChange, 0, values.length - 1);
        return null;
    }

    public Void restoreDefault() {
        index = defaultIndex;
        return null;
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"index\":").append(index).append(", ");
        json.append("\"values\":[");

        for (int i = 0; i < values.length; i++) {
            json.append(values[i].toString());
            if (i < values.length - 1) {
                json.append(", ");
            }
        }

        json.append("]}");
        return json.toString();
    }

    public long asLong() {
        return getActualValue().longValue();
    }

    public double asDouble() {
        return getActualValue().doubleValue();
    }
}

