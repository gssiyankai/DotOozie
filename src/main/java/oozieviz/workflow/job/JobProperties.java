package oozieviz.workflow.job;

import java.io.*;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JobProperties {

    private static final String TOKEN_REGEX = "\\$\\{(.+?)\\}";

    private final Properties properties;

    private JobProperties(Properties properties) {
        this.properties = properties;
    }

    public static JobProperties newJobProperties(Optional<File> f) throws Exception {
        Properties properties = new Properties();
        if (f.isPresent()) {
            properties.load(new FileInputStream(f.get()));
        }
        return new JobProperties(properties);
    }

    public String replaceTokens(String text) {
        Pattern pattern = Pattern.compile(TOKEN_REGEX);
        Matcher matcher = pattern.matcher(text);
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            String replacement = property(matcher.group(1));
            builder.append(text.substring(i, matcher.start()));
            if (replacement == null) {
                builder.append(matcher.group(0));
            } else {
                builder.append(replacement);
            }
            i = matcher.end();
        }
        builder.append(text.substring(i, text.length()));
        return builder.toString();
    }

    private String property(String key) {
        return properties.getProperty(key, "");
    }

}
