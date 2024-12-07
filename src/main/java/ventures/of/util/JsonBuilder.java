package ventures.of.util;

// Just realized after writing this that I might as well use a .properties file that's not in the jar. Already have a reader for that which would be the hard part.
public class JsonBuilder {
    final StringBuilder sb = new StringBuilder();
    static final int spacesPerIndent = 2;
    int indentLevel = 0;

    public JsonBuilder setIndentLevel(int level) {
        indentLevel = level;
        return this;
    }

    private JsonBuilder() {
        sb.append("{\n");
    }

    public String build() {
        return sb.toString();
    }

    public static JsonBuilder builder() {
        return builder(0);
    }
    public static JsonBuilder builder(int indentLevel) {
        return new JsonBuilder().setIndentLevel(indentLevel);
    }

    public  JsonBuilder jsonAddStringProperty(String name, int value) {
        jsonIndent();
        sb.append("\"").append(name).append("\":\"").append(value).append("\"");
        return this;
    }
    public  JsonBuilder jsonAddStringProperty(String name, double value) {
        jsonIndent();
        sb.append("\"").append(name).append("\":\"").append(value).append("\"");
        return this;
    }
    public  JsonBuilder jsonAddStringProperty(String name, float value) {
        jsonIndent();
        sb.append("\"").append(name).append("\":\"").append(value).append("\"");
        return this;
    }
    public  JsonBuilder jsonAddStringProperty(String name, long value) {
        jsonIndent();
        sb.append("\"").append(name).append("\":\"").append(value).append("\"");
        return this;
    }
    public  JsonBuilder jsonAddStringProperty(String name, short value) {
        jsonIndent();
        sb.append("\"").append(name).append("\":\"").append(value).append("\"");
        return this;
    }

    public  JsonBuilder jsonAddStringProperty(String name, Number value) {
        jsonIndent();
        sb.append("\"").append(name).append("\":\"").append(value).append("\"");
        return this;
    }
    public  JsonBuilder jsonAddStringProperty(String name, String value) {
        jsonIndent();
        sb.append("\"").append(name).append("\":\"").append(value).append("\"");
        return this;
    }

    public  JsonBuilder jsonEndObject() {
        jsonIndent();
        sb.append("}");
        return this;
    }

    private  JsonBuilder jsonIndent() {
        for (int i = 0 ; i < indentLevel*spacesPerIndent ; i++ ) {
                sb.append(" ");
        }
        return this;
    }

}
