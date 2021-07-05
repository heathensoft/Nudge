package nudge.io.serialization;


public class JsonBuilder {

    private final StringBuilder stringBuilder;

    public JsonBuilder() {
        stringBuilder = new StringBuilder();
    }

    private String tabs(int num) {
        return "\t".repeat(Math.max(0, num));
    }

    private void endLine(boolean newLine, boolean comma) {
        if(comma) stringBuilder.append(",");
        if(newLine) stringBuilder.append("\n");
    }

    private String newLine() {
        return "\n";
    }

    private String comma() { return ","; }

    public void begin() {
        stringBuilder.append("{").append(newLine());
    }

    public void end() {
        stringBuilder.append("}");
    }

    public void addVar(String name, boolean value, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\": ").append(value);
        endLine(newLine, comma);
    }

    public void addVar(String name, byte value, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\": ").append(value);
        endLine(newLine, comma);
    }

    public void addVar(String name, short value, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\": ").append(value);
        endLine(newLine, comma);
    }

    public void addVar(String name, char value, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\": ").append(value);
        endLine(newLine, comma);
    }

    public void addVar(String name, int value, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\": ").append(value);
        endLine(newLine, comma);
    }

    public void addVar(String name, long value, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\": ").append(value);
        endLine(newLine, comma);
    }

    public void addVar(String name, float value, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\": ").append(value);
        endLine(newLine, comma);
    }

    public void addVar(String name, double value, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\": ").append(value);
        endLine(newLine, comma);
    }

    public void addString(String name, String value, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\": ").append(value);
        endLine(newLine, comma);
    }

    public void addArray(String name, boolean[] array, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\"").append(": [").append(newLine());
        int len = array.length - 1;
        int arrTabCount = tabCount + 1;
        for (int i = 0; i < array.length; i++) {
            if (i == len) stringBuilder.append(tabs(arrTabCount)).append(array[len]).append(newLine());
            else stringBuilder.append(tabs(arrTabCount)).append(array[i]).append(comma()).append(newLine());
        }
        stringBuilder.append(tabs(tabCount)).append("]");
        endLine(newLine,comma);
    }

    public void addArray(String name, byte[] array, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\"").append(": [").append(newLine());
        int len = array.length - 1;
        int arrTabCount = tabCount + 1;
        for (int i = 0; i < array.length; i++) {
            if (i == len) stringBuilder.append(tabs(arrTabCount)).append(array[len]).append(newLine());
            else stringBuilder.append(tabs(arrTabCount)).append(array[i]).append(comma()).append(newLine());
        }
        stringBuilder.append(tabs(tabCount)).append("]");
        endLine(newLine,comma);
    }

    public void addArray(String name, short[] array, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\"").append(": [").append(newLine());
        int len = array.length - 1;
        int arrTabCount = tabCount + 1;
        for (int i = 0; i < array.length; i++) {
            if (i == len) stringBuilder.append(tabs(arrTabCount)).append(array[len]).append(newLine());
            else stringBuilder.append(tabs(tabCount)).append(array[i]).append(comma()).append(newLine());
        }
        stringBuilder.append(tabs(tabCount)).append("]");
        endLine(newLine,comma);
    }

    public void addArray(String name, char[] array, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\"").append(": [").append(newLine());
        int len = array.length - 1;
        int arrTabCount = tabCount + 1;
        for (int i = 0; i < array.length; i++) {
            if (i == len) stringBuilder.append(tabs(arrTabCount)).append(array[len]).append(newLine());
            else stringBuilder.append(tabs(arrTabCount)).append(array[i]).append(comma()).append(newLine());
        }
        stringBuilder.append(tabs(tabCount)).append("]");
        endLine(newLine,comma);
    }

    public void addArray(String name, int[] array, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\"").append(": [").append(newLine());
        int len = array.length - 1;
        int arrTabCount = tabCount + 1;
        for (int i = 0; i < array.length; i++) {
            if (i == len) stringBuilder.append(tabs(arrTabCount)).append(array[len]).append(newLine());
            else stringBuilder.append(tabs(arrTabCount)).append(array[i]).append(comma()).append(newLine());
        }
        stringBuilder.append(tabs(tabCount)).append("]");
        endLine(newLine,comma);
    }

    public void addArray(String name, long[] array, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\"").append(": [").append(newLine());
        int len = array.length - 1;
        int arrTabCount = tabCount + 1;
        for (int i = 0; i < array.length; i++) {
            if (i == len) stringBuilder.append(tabs(arrTabCount)).append(array[len]).append(newLine());
            else stringBuilder.append(tabs(arrTabCount)).append(array[i]).append(comma()).append(newLine());
        }
        stringBuilder.append(tabs(tabCount)).append("]");
        endLine(newLine,comma);
    }

    public void addArray(String name, float[] array, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\"").append(": [").append(newLine());
        int len = array.length - 1;
        int arrTabCount = tabCount + 1;
        for (int i = 0; i < array.length; i++) {
            if (i == len) stringBuilder.append(tabs(arrTabCount)).append(array[len]).append(newLine());
            else stringBuilder.append(tabs(arrTabCount)).append(array[i]).append(comma()).append(newLine());
        }
        stringBuilder.append(tabs(tabCount)).append("]");
        endLine(newLine,comma);
    }

    public void addArray(String name, double[] array, int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\"").append(": [").append(newLine());
        int len = array.length - 1;
        int arrTabCount = tabCount + 1;
        for (int i = 0; i < array.length; i++) {
            if (i == len) stringBuilder.append(tabs(arrTabCount)).append(array[len]).append(newLine());
            else stringBuilder.append(tabs(arrTabCount)).append(array[i]).append(comma()).append(newLine());
        }
        stringBuilder.append(tabs(tabCount)).append("]");
        endLine(newLine,comma);
    }

    public void beginObject(String name, int tabCount) {
        stringBuilder.append(tabs(tabCount)).append("\"").append(name).append("\"").append(": {").append(newLine());
    }

    public void endObject (int tabCount, boolean newLine, boolean comma) {
        stringBuilder.append(tabs(tabCount)).append("}");
        endLine(newLine, comma);
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }
}
