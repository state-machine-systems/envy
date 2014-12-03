package com.statemachinesystems.envy;

/**
 * Parses strings into values of a given type.
 *
 * @param <T>  the type to be parsed
 */
public interface ValueParser<T> {

    /**
     * Parse the given string into a value of the required type.
     *
     * @param value  the string to parse
     * @return       a value of the required type
     */
    T parseValue(String value);

    /**
     * Provide the type required by this parser.
     *
     * @return  a {@link java.lang.Class} object representing the required type
     */
    Class<T> getValueClass();
}
