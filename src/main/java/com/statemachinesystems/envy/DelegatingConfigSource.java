package com.statemachinesystems.envy;

/**
 * A composite {@link com.statemachinesystems.envy.ConfigSource} implementation
 * that retrieves configuration values from its component sources in preference order.
 */
public class DelegatingConfigSource implements ConfigSource {

    private final ConfigSource[] sources;

    public DelegatingConfigSource(ConfigSource... sources) {
        this.sources = sources;
    }

    @Override
    public String getValue(Parameter parameter) {
        for (ConfigSource source : sources) {
            String value = source.getValue(parameter);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
