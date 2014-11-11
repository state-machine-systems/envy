package com.statemachinesystems.envy;

import com.statemachinesystems.envy.parsers.BooleanValueParser;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class ValueParserFactoryTest {

    public static enum MyEnum {
        FOO, BAR, BAZ
    }

    public static class MyClass {
        private final String value;

        public MyClass(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MyClass myClass = (MyClass) o;

            if (!value.equals(myClass.value)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

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
        ValueParser<?> parser = valueParserFactory.getValueParser(expected.getClass());
        assertArrayEquals(expected, (Boolean[]) parser.parseValue("true,false"));
    }

    @Test
    public void createsParserForBoxedArrayOfRegisteredType() {
        boolean[] primitiveArray = new boolean[] {};
        ValueParser<?> parser = valueParserFactory.getValueParser(primitiveArray.getClass());
        Boolean[] expected = new Boolean[] { Boolean.TRUE, Boolean.TRUE };
        assertArrayEquals(expected, (Boolean[]) parser.parseValue("true,true"));
    }

    @Test
    public void createsParserForArrayOfEnumType() {
        MyEnum[] expected = new MyEnum[] { MyEnum.BAR, MyEnum.BAR, MyEnum.BAZ };
        ValueParser<?> parser = valueParserFactory.getValueParser(expected.getClass());
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
        Random[] arrayOfRandom = new Random[] {};
        valueParserFactory.getValueParser(arrayOfRandom.getClass());
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsArrayOfArrays() {
        String[][] arrayOfArrays = new String[][] {};
        valueParserFactory.getValueParser(arrayOfArrays.getClass());
    }
}
