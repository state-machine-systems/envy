package com.statemachinesystems.envy.values;

import com.statemachinesystems.envy.ConfigSource;
import com.statemachinesystems.envy.Sensitive;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Wrapper type for configuration values indicating whether the value was sourced
 * from configuration, missing or set to a default.
 */
public interface ConfigValue<T> extends Serializable {

    static <T> ConfigValue<T> of(T value, ConfigValue.Status status, Method method) {
        return method.getAnnotation(Sensitive.class) != null
            ? new SensitiveValue<>(value, status)
            : new ResolvedValue<>(value, status);
    }

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
     * Returns the value formatted as a String.
     *
     * @param proxy  the proxy this value belongs to
     * @return  the value formatted as a String
     */
    String format(Object proxy);

    /**
     * Indicates whether the value was found in the underlying {@link ConfigSource},
     * missing, or replaced by a default value.
     */
    Status getStatus();
}
