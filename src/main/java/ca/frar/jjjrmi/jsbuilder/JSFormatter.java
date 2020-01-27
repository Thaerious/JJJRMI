package ca.frar.jjjrmi.jsbuilder;

public class JSFormatter {
    private final static String INDENT = "    ";
    private final StringBuilder builder = new StringBuilder();

    public static String process(String input, int starting) {
        return new JSFormatter(input, starting).toString();
    }

    public JSFormatter(String input, int starting) {
        int indent = starting;
        if (input == null) return;
        String[] split = input.split("\n");

        for (String s : split) {
            if (s.contains("}")) indent--;
            for (int i = 0; i < indent; i++) builder.append(INDENT);
            builder.append(s).append("\n");
            if (s.contains("{")) indent++;
        }
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
