package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

/**
 * {@link ValueParser} implementation for {@link java.lang.Double} values.
 */
public class DoubleValueParser implements ValueParser<Double> {

    @Override
    public Double parseValue(String value) {
        return Double.parseDouble(value);
    }

    @Override
    public Class<Double> getValueClass() {
        return Double.class;
    }
}
