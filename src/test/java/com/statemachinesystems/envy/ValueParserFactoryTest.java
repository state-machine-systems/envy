package com.statemachinesystems.envy;

import com.statemachinesystems.envy.example.MyClass;
import com.statemachinesystems.envy.example.MyEnum;
import com.statemachinesystems.envy.parsers.BooleanValueParser;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class ValueParserFactoryTest {

    private final ValueParserFactory valueParserFactory =
            new ValueParserFactory(new BooleanValueParser());

    @Test
    public void createsParserForRegisteredType() {
        ValueParser<?> parser = valueParserFactory.getValueParser(Boolean.class);
        assertEquals(Boolean.TRUE, parser.parseValue("true"));
    }

    @Test
    public void createsParserForBoxedRegisteredType() {
        ValueParser<?> parser = valueParserFactory.getValueParser(boolean.class);
        assertEquals(Boolean.FALSE, parser.parseValue("false"));
    }

    @Test
    public void createsParserForEnumType() {
        ValueParser<?> parser = valueParserFactory.getValueParser(MyEnum.class);
        assertEquals(MyEnum.FOO, parser.parseValue("FOO"));
    }

    @Test
    public void createsParserForTypeWithStringConstructor() {
        ValueParser<?> parser = valueParserFactory.getValueParser(MyClass.class);
        String input = "foo";
        MyClass expected = new MyClass(input);
        MyClass actual = (MyClass) parser.parseValue(input);
        assertEquals(expected, actual);
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsUnsupportedType() {
        valueParserFactory.getValueParser(Random.class);
    }

    @Test
    public void createsParserForArrayOfRegisteredType() {
        Boolean[] expected = new Boolean[] { Boolean.TRUE, Boolean.FALSE };
        ValueParser<?> parser = valueParserFactory.getValueParser(Boolean[].class);
        assertArrayEquals(expected, (Boolean[]) parser.parseValue("true,false"));
    }

    @Test
    public void createsParserForBoxedArrayOfRegisteredType() {
        ValueParser<?> parser = valueParserFactory.getValueParser(boolean[].class);
        Boolean[] expected = new Boolean[] { Boolean.TRUE, Boolean.TRUE };
        assertArrayEquals(expected, (Boolean[]) parser.parseValue("true,true"));
    }

    @Test
    public void createsParserForArrayOfEnumType() {
        MyEnum[] expected = new MyEnum[] { MyEnum.BAR, MyEnum.BAR, MyEnum.BAZ };
        ValueParser<?> parser = valueParserFactory.getValueParser(MyEnum[].class);
        assertArrayEquals(expected, (MyEnum[]) parser.parseValue("BAR,BAR,BAZ"));
    }

    @Test
    public void createsParserForArrayOfTypeWithStringConstructor() {
        MyClass[] expected = new MyClass[] { new MyClass("foo"), new MyClass("bar") };
        ValueParser<?> parser = valueParserFactory.getValueParser(expected.getClass());
        assertArrayEquals(expected, (MyClass[]) parser.parseValue("foo,bar"));
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsArrayOfUnsupportedType() {
        valueParserFactory.getValueParser(Random[].class);
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsArrayOfArrays() {
        valueParserFactory.getValueParser(String[][].class);
    }
}
