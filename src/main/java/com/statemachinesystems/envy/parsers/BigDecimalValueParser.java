package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.math.BigDecimal;

/**
 * {@link ValueParser} implementation for {@link java.math.BigDecimal} values.
 */
public class BigDecimalValueParser implements ValueParser<BigDecimal> {

    @Override
    public BigDecimal parseValue(String value) {
        return new BigDecimal(value);
    }

    @Override
    public Class<BigDecimal> getValueClass() {
        return BigDecimal.class;
    }
}
