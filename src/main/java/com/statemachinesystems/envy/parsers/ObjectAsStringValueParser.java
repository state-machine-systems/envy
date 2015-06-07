package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

/**
 * {@link ValueParser} implementation for {@link java.lang.Object} values that produces
 * a {@link java.lang.String} result.
 */
public class ObjectAsStringValueParser implements ValueParser<Object> {

    private final StringValueParser valueParser = new StringValueParser();

    @Override
    public Object parseValue(String value) {
        return valueParser.parseValue(value);
    }

    @Override
    public Class<Object> getValueClass() {
        return Object.class;
    }
}
