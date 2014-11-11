package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ByteValueParserTest {

    @Test
    public void parsesBytes() {
        byte value = Byte.MIN_VALUE;
        assertThat(new ByteValueParser().parseValue(String.valueOf(value)), is(value));
    }
}
