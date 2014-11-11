package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntegerValueParserTest {

    @Test
    public void parsesIntegers() {
        int value = Integer.MIN_VALUE;
        assertThat(new IntegerValueParser().parseValue(String.valueOf(value)), is(value));
    }
}
