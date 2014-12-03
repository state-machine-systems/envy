package com.statemachinesystems.envy;

/**
 * A source of configuration values.
 */
public interface ConfigSource {

    /**
     * Retrieves the configuration value associated with the given parameter.
     *
     * @param parameter  the parameter to retrieve.
     * @return           the value associated with the parameter, or null if no value was present
     */
    String getValue(Parameter parameter);
}
