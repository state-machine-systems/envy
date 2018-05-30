package com.statemachinesystems.envy.values;

import com.statemachinesystems.envy.ConfigSource;

import java.io.Serializable;

/**
 * Wrapper type for configuration values indicating whether the value was sourced
 * from configuration, missing or set to a default.
 */
public interface ConfigValue<T> extends Serializable {

    enum Status {
        CONFIGURED,
        MISSING,
        DEFAULTED
    }

    /**
     * Returns the fully extracted and parsed value, or a default.
     *
     * @param proxy  the proxy this value belongs to
     */
    T getValue(Object proxy);

    /**
     * Indicates whether the value was found in the underlying {@link ConfigSource},
     * missing, or replaced by a default value.
     */
    Status getStatus();
}
