package com.statemachinesystems.envy.parsers;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DoubleValueParserTest {

    @Test
    public void parsesDoubles() {
        double value = Double.MAX_VALUE;
        assertThat(new DoubleValueParser().parseValue(String.valueOf(value)), is(value));
    }
}
