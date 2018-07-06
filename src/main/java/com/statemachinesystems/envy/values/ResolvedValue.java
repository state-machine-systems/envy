package com.statemachinesystems.envy.values;

import java.lang.reflect.Array;
import java.util.Objects;

/**
 * Wrapper type for resolved configuration values.
 */
public class ResolvedValue<T> implements ConfigValue<T> {

    private final T value;
    private final Status status;

    protected ResolvedValue(T value, Status status) {
        this.value = value;
        this.status = status;
    }

    @Override
    public T getValue(Object proxy) {
        return value;
    }

    @Override
    public String format(Object proxy) {
        T value = getValue(proxy);
        return value != null && value.getClass().isArray()
                ? formatArray(value)
                : String.valueOf(value);
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResolvedValue that = (ResolvedValue) o;
        return Objects.equals(value, that.value) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, status);
    }

    private static String formatArray(Object array) {
        StringBuilder buf = new StringBuilder();
        buf.append('[');
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                buf.append(", ");
            }
            buf.append(Array.get(array, i));
        }
        buf.append(']');
        return buf.toString();
    }
}
