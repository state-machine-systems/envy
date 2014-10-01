package com.statemachinesystems.envy;

public interface ValueParser<T> {
    T parseValue(String value);
    Class<T> getValueClass();
}
