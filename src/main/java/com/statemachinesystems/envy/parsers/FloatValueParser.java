package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

/**
 * {@link ValueParser} implementation for {@link java.lang.Float} values.
 */
public class FloatValueParser implements ValueParser<Float> {

    @Override
    public Float parseValue(String value) {
        return Float.parseFloat(value);
    }

    @Override
    public Class<Float> getValueClass() {
        return Float.class;
    }
}
