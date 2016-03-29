package com.statemachinesystems.envy.sources;

import com.statemachinesystems.envy.ConfigSource;
import com.statemachinesystems.envy.Parameter;

/**
 * A {@link com.statemachinesystems.envy.ConfigSource} implementation
 * that retrieves configuration values from environment variables using an
 * <code>UPPER_CASE_UNDERSCORED</code> naming convention.
 */
public class EnvironmentVariableConfigSource implements ConfigSource {

    @Override
    public String getValue(Parameter parameter) {
        return System.getenv(parameter.asEnvironmentVariableName());
    }
}
