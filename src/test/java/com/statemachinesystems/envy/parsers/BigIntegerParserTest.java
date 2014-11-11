package com.statemachinesystems.envy.parsers;


import org.junit.Test;

import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BigIntegerParserTest {

    @Test
    public void parsesBigIntegers() {
        BigInteger value = BigInteger.TEN;
        assertThat(new BigIntegerValueParser().parseValue(String.valueOf(value)), is(value));
    }
}
