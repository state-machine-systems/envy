package com.statemachinesystems.envy;

/**
 * Wraps a sensitive value to be masked in proxy toString() calls.
 */
public class SensitiveValueWrapper {

    public static final String MASKED_VALUE = "********";

    private final Object value;

    /**
     * Creates a {@link com.statemachinesystems.envy.SensitiveValueWrapper} instance for the given value.
     *
     * @param value  the value to wrap
     */
    public SensitiveValueWrapper(Object value) {
        this.value = value;
    }

    /**
     * Returns the wrapped value.
     *
     * @return  the wrapped value
     */
    public Object unwrap() {
        return value;
    }

    @Override
    public String toString() {
        return MASKED_VALUE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SensitiveValueWrapper that = (SensitiveValueWrapper) o;

        return value != null ? value.equals(that.value) : that.value == null;

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
