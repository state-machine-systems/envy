package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ShortValueParserTest {

    @Test
    public void parsesShorts() {
        short value = Short.MIN_VALUE;
        assertThat(new ShortValueParser().parseValue(String.valueOf(value)), is(value));
    }
}
