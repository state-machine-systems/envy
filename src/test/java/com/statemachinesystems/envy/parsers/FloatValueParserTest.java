package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FloatValueParserTest {

    @Test
    public void parsesFloats() {
        float value = Float.MAX_VALUE;
        assertThat(new FloatValueParser().parseValue(String.valueOf(value)), is(value));
    }
}
