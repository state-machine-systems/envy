package com.statemachinesystems.envy;

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
