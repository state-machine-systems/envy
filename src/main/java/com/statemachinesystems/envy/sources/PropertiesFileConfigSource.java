package com.statemachinesystems.envy.sources;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.statemachinesystems.envy.ConfigSource;
import com.statemachinesystems.envy.Parameter;

/**
 * A {@link com.statemachinesystems.envy.ConfigSource} implementation
 * that retrieves configuration values from a .properties file using a
 * <code>lower.case.dotted</code> naming convention.
 */
public class PropertiesFileConfigSource implements ConfigSource {

    public static ConfigSource fromClasspath(String path) throws IOException {
        return fromInputStream(PropertiesFileConfigSource.class.getResourceAsStream(path));
    }

    public static ConfigSource fromFile(String path) throws IOException {
        return fromInputStream(new BufferedInputStream(new FileInputStream(path)));
    }

    public static ConfigSource fromInputStream(InputStream in) throws IOException {
        try {
            return new PropertiesFileConfigSource(in);
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException ignored) {}
        }
    }

    private final Properties properties;

    public PropertiesFileConfigSource(InputStream in) throws IOException {
        this.properties = new Properties();
        properties.load(in);
    }

    @Override
    public String getValue(Parameter parameter) {
        return properties.getProperty(parameter.asSystemPropertyName());
    }
}
