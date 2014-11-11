package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringValueParserTest {

    @Test
    public void parsesStrings() {
        String value = "foo";
        assertThat(new StringValueParser().parseValue(value), is(value));
    }
}
