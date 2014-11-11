package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.ValueParser;

import java.math.BigInteger;

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
