package com.statemachinesystems.envy;

/**
 * A default {@link com.statemachinesystems.envy.ConfigSource} implementation
 * that retrieves configuration values from either JVM system properties or
 * environment variables.
 *
 * System properties override environment variables with
 * equivalent names.
 */
public class DefaultConfigSource implements ConfigSource {

    @Override
    public String getValue(Parameter parameter) {
        String value = System.getProperty(parameter.asSystemPropertyName());
        if (value == null) {
            value = System.getenv(parameter.asEnvironmentVariableName());
        }
        return value;
    }
}
