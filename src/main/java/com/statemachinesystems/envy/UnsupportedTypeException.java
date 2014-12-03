package com.statemachinesystems.envy;

/**
 * {@link java.lang.RuntimeException} thrown when requesting a
 * {@link com.statemachinesystems.envy.ValueParser} for an unsupported type.
 */
public class UnsupportedTypeException extends RuntimeException {

    public UnsupportedTypeException(String message) {
        super(message);
    }
}
