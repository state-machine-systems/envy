package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.math.BigInteger;

/**
 * {@link ValueParser} implementation for {@link java.math.BigInteger} values.
 */
public class BigIntegerValueParser implements ValueParser<BigInteger> {

    @Override
    public BigInteger parseValue(String value) {
        return new BigInteger(value);
    }

    @Override
    public Class<BigInteger> getValueClass() {
        return BigInteger.class;
    }
}
