package com.statemachinesystems.envy.sources;

import com.statemachinesystems.envy.ConfigSource;
import com.statemachinesystems.envy.Parameter;

/**
 * A composite {@link com.statemachinesystems.envy.ConfigSource} implementation
 * that retrieves configuration values from its component sources in preference order.
 */
public class DelegatingConfigSource implements ConfigSource {

    private final ConfigSource[] sources;

    /**
     * Creates a {@link com.statemachinesystems.envy.sources.DelegatingConfigSource} using
     * the given component sources in preference order.
     *
     * @param sources  component {@link com.statemachinesystems.envy.ConfigSource}s in preference order
     */
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
