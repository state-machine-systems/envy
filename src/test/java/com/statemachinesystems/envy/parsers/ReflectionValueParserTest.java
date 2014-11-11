package com.statemachinesystems.envy.parsers;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReflectionValueParserTest {

    public static class ClassWithStringConstructor {
        final String value;
        public ClassWithStringConstructor(String value) { this.value = value; }
    }

    public static class ClassWithMultipleConstructors {
        final String value;
        public ClassWithMultipleConstructors(String value) { this.value = value; }
        public ClassWithMultipleConstructors() { this.value = ""; }
    }

    public static class ClassWithNoStringConstructor {}

    public static class ClassWithExceptionThrowingConstructor {
        public ClassWithExceptionThrowingConstructor(String value) {
            throw new IllegalStateException();
        }
    }

    private static final ReflectionValueParser<ClassWithStringConstructor> parserForClassWithStringConstructor =
            ReflectionValueParser.parserOrNull(ClassWithStringConstructor.class);

    private static final ReflectionValueParser<ClassWithMultipleConstructors> parserForClassWithMultipleConstructors =
            ReflectionValueParser.parserOrNull(ClassWithMultipleConstructors.class);

    private static final ReflectionValueParser<ClassWithNoStringConstructor> parserForClassWithNoStringConstructor =
            ReflectionValueParser.parserOrNull(ClassWithNoStringConstructor.class);

    private static final ReflectionValueParser<ClassWithExceptionThrowingConstructor> parserForClassWithExceptionThrowingConstructor =
            ReflectionValueParser.parserOrNull(ClassWithExceptionThrowingConstructor.class);

    @Test
    public void parsesClassWithStringConstructor() {
        String input = "foo";
        ClassWithStringConstructor value = parserForClassWithStringConstructor.parseValue(input);
        assertThat(value.value, is(input));
    }

    @Test
    public void parsesClassWithMultipleConstructors() {
        String input = "bar";
        ClassWithMultipleConstructors value = parserForClassWithMultipleConstructors.parseValue(input);
        assertThat(value.value, is(input));
    }

    @Test
    public void rejectsClassWithNoStringConstructor() {
        assertNull(parserForClassWithNoStringConstructor);
    }

    @Test
    public void hasValueClassOfSameType() {
        assertEquals(ClassWithStringConstructor.class, parserForClassWithStringConstructor.getValueClass());
        assertEquals(ClassWithMultipleConstructors.class, parserForClassWithMultipleConstructors.getValueClass());
    }

    @Test(expected = RuntimeException.class)
    public void throwsRuntimeExceptionWhenConstructionFails() {
        parserForClassWithExceptionThrowingConstructor.parseValue("foo");
    }
}
