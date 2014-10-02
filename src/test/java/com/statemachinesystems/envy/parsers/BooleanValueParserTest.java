package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BooleanValueParserTest {

    private BooleanValueParser parser = new BooleanValueParser();

    @Test
    public void isCaseInsensitive() {
        assertThat(parser.parseValue("TRUe"), is(Boolean.TRUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsUnknownValue() {
        assertThat(parser.parseValue("unknown"), is(Boolean.FALSE));
    }

    @Test
    public void supportsCustomBooleanValues() {
        Map<String, Boolean> acceptedValues = new HashMap<String, Boolean>();
        acceptedValues.put("foo", Boolean.TRUE);
        BooleanValueParser customParser = BooleanValueParser.createParser(acceptedValues);
        assertThat(customParser.parseValue("FoO"), is(Boolean.TRUE));
    }
}
