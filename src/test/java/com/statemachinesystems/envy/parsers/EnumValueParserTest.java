package com.statemachinesystems.envy.parsers;

import com.statemachinesystems.envy.common.MyEnum;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;

public class EnumValueParserTest {

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

    @Test
    public void hasValueClassOfSameEnum() {
        assertEquals(MyEnum.class, parser.getValueClass());
    }
}
