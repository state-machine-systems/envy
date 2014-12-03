package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

/**
 * {@link ValueParser} implementation for {@link java.lang.Long} values.
 */
public class LongValueParser implements ValueParser<Long> {

    @Override
    public Long parseValue(String value) {
        return Long.parseLong(value);
    }

    @Override
    public Class<Long> getValueClass() {
        return Long.class;
    }
}
