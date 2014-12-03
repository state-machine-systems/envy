package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

/**
 * {@link ValueParser} implementation for {@link java.lang.String} values.
 */
public class StringValueParser implements ValueParser<String> {

    @Override
    public String parseValue(String value) {
        return value;
    }

    @Override
    public Class<String> getValueClass() {
        return String.class;
    }
}
