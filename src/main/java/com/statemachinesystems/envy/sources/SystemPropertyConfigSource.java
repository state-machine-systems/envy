package com.statemachinesystems.envy.sources;

import com.statemachinesystems.envy.ConfigSource;
import com.statemachinesystems.envy.Parameter;

/**
 * A {@link com.statemachinesystems.envy.ConfigSource} implementation
 * that retrieves configuration values from JVM system properties using a
 * <code>lower.case.dotted</code> naming convention.
 */
public class SystemPropertyConfigSource implements ConfigSource {

    @Override
    public String getValue(Parameter parameter) {
        return System.getProperty(parameter.asSystemPropertyName());
    }
}
