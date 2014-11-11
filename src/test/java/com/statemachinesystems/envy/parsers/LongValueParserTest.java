package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LongValueParserTest {

    @Test
    public void parsesLongs() {
        long value = Long.MAX_VALUE;
        assertThat(new LongValueParser().parseValue(String.valueOf(value)), is(value));
    }
}
