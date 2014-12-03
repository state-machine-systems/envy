package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.lang.reflect.Array;

/**
 * {@link ValueParser} implementation for array values.
 *
 * @param <T>  the type of the array element to be parsed
 */
public class ArrayValueParser<T> implements ValueParser<T[]> {

    public static final String DEFAULT_SEPARATOR = ",";
    private static final int INCLUDE_TRAILING = -1;

    private final ValueParser<T> valueParser;
    private final String separator;

    public ArrayValueParser(ValueParser<T> valueParser, String separator) {
        this.valueParser = valueParser;
        this.separator = separator;
    }

    public ArrayValueParser(ValueParser<T> valueParser) {
        this(valueParser, DEFAULT_SEPARATOR);
    }

    @Override
    public T[] parseValue(String value) {
        String[] parts = value.isEmpty()
            ? new String[] {}
            : value.split(separator, INCLUDE_TRAILING);

        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(valueParser.getValueClass(), parts.length);

        for (int i = 0; i < parts.length; i++) {
            array[i] = valueParser.parseValue(parts[i]);
        }
        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T[]> getValueClass() {
        return (Class<T[]>) Array.newInstance(valueParser.getValueClass(), 0).getClass();
    }
}
