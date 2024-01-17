package ventures.of.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ventures.of.util.MathUtil;

import java.util.Arrays;

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

}

