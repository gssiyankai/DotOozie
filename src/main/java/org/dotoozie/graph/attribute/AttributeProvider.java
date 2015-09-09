package org.dotoozie.graph.attribute;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

abstract class AttributeProvider {

    private static final String CONFIG_PROPERTIES = "config.properties";
    private static final String SEPARATOR = ".";

    private final Properties properties = new Properties();

    AttributeProvider() throws IOException {
        properties.load(this.getClass().getResourceAsStream("/" + CONFIG_PROPERTIES));
    }

    Map<String, String> attributes(String component, String type, String... properties) {
        return Arrays.stream(properties)
                .collect(
                        Collectors.toMap(
                                Function.identity(),
                                property -> attribute(component, type.toLowerCase(), property)));
    }

    private String attribute(String component, String type, String property) {
        String attribute = property(component + SEPARATOR + property + SEPARATOR + type);
        if (attribute.isEmpty()) {
            attribute = property(component + SEPARATOR + property);
        }
        return attribute;
    }

    private String property(String key) {
        return properties.getProperty(key, "");
    }

}
