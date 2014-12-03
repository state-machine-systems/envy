package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

/**
 * {@link ValueParser} implementation for {@link java.lang.Short} values.
 */
public class ShortValueParser implements ValueParser<Short> {

    @Override
    public Short parseValue(String value) {
        return Short.parseShort(value);
    }

    @Override
    public Class<Short> getValueClass() {
        return Short.class;
    }
}
