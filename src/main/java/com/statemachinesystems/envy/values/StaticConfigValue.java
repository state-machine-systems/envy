package com.statemachinesystems.envy.values;

import java.util.Objects;

/**
 * Wrapper type for static configuration values.
 */
public class StaticConfigValue<T> implements ConfigValue<T> {

    private final T value;
    private final Status status;

    public StaticConfigValue(T value, Status status) {
        this.value = value;
        this.status = status;
    }

    @Override
    public T getValue(Object proxy) {
        return value;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticConfigValue that = (StaticConfigValue) o;
        return Objects.equals(value, that.value) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, status);
    }
}
