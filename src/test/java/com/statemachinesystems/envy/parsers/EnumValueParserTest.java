package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EnumValueParserTest {

    private static enum MyEnum {
        FOO, BAR, BAZ;
    }

    private static final EnumValueParser<MyEnum> parser =
            new EnumValueParser<MyEnum>(MyEnum.class);

    @Test
    public void parsesEnums() {
        for (MyEnum value : MyEnum.values()) {
            assertThat(parser.parseValue(value.name()), is(value));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsUnknownEnumValues() {
        parser.parseValue("QUX");
    }
}
