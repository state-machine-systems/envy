package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ObjectAsStringValueParserTest {

    @Test
    public void parsesObjectsAsStrings() {
        String value = "foo";
        assertEquals(value, new ObjectAsStringValueParser().parseValue(value));
    }
}
