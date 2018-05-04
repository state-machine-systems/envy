package com.statemachinesystems.envy.values;

import com.statemachinesystems.envy.ConfigSource;

import java.io.Serializable;
import java.util.Objects;

/**
 * Wrapper type for configuration values.
 */
public class ConfigValue implements Serializable {

    public enum Status {
        CONFIGURED,
        MISSING,
        DEFAULTED
    }

    private final Object value;
    private final Status status;

    public ConfigValue(Object value, Status status) {
        this.value = value;
        this.status = status;
    }

    /**
     * Returns the fully extracted and parsed value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Indicates whether the value was found in the underlying {@link ConfigSource},
     * missing, or replaced by a default value.
     */
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigValue that = (ConfigValue) o;
        return Objects.equals(value, that.value) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, status);
    }
}
