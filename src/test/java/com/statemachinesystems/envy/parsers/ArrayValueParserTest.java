package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ArrayValueParserTest {

    private ArrayValueParser<String> stringArrayParser =
            new ArrayValueParser<String>(new StringValueParser());

    private ArrayValueParser<Boolean> booleanArrayParser =
            new ArrayValueParser<Boolean>(new BooleanValueParser());

    @Test
    public void parsesEmptyStringAsEmptyArray() {
        assertThat(stringArrayParser.parseValue(""), is(new String[] {}));
    }

    @Test
    public void parsesWhiteSpaceAsSingleValue() {
        assertThat(stringArrayParser.parseValue("   "), is(new String[] {"   "}));
    }

    @Test
    public void parsersCommaDelimitedStrings() {
        assertThat(stringArrayParser.parseValue("a,b,c, d ,,"), is(new String[] {
                "a", "b", "c", " d ", "", ""
        }));
    }

    @Test
    public void parsesCommaDelimitedBooleans() {
        assertThat(booleanArrayParser.parseValue("true,false,false,true"), is(new Boolean[] {
                true, false, false, true
        }));
    }

    @Test
    public void hasValueClassOfArrayType() {
        String[] emptyStringArray = new String[] {};
        assertEquals(emptyStringArray.getClass(), stringArrayParser.getValueClass());
    }
}
