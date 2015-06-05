package com.statemachinesystems.envy;

import com.statemachinesystems.envy.example.DummyConfigSource;
import com.statemachinesystems.envy.parsers.IntegerValueParser;
import com.statemachinesystems.envy.parsers.StringValueParser;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ProxyInvocationHandlerTest {

    public interface ExampleConfig {
        String getAString();
        Integer getABoxedInteger();
        int getAPrimitiveInteger();
        Integer[] getAnArrayOfBoxedIntegers();
        int[] getAnArrayOfPrimitiveIntegers();

        @Default("default value")
        String defaultedString();

        @Name("custom.parameter.name")
        String stringWithCustomName();

        @Optional
        Integer optionalNonNull();

        @Optional
        Integer optionalNull();
    }

    public interface BadConfigCombiningOptionalWithPrimitive {
        @Optional
        int notOptional();
    }

    public interface BadConfigWithOverriddenObjectMethod {
        @Override
        String toString();
    }

    private DummyConfigSource configSource;
    private ValueParserFactory valueParserFactory;
    private ProxyInvocationHandler invocationHandler;

    @Before
    public void setUp() {
        configSource = new DummyConfigSource()
                .add("A_STRING", "foo")
                .add("A_BOXED_INTEGER", "10")
                .add("A_PRIMITIVE_INTEGER", "15")
                .add("AN_ARRAY_OF_BOXED_INTEGERS", "7")
                .add("AN_ARRAY_OF_PRIMITIVE_INTEGERS", "1,2,3")
                .add("CUSTOM_PARAMETER_NAME", "bar")
                .add("OPTIONAL_NON_NULL", "5");

        valueParserFactory = new ValueParserFactory(new StringValueParser(), new IntegerValueParser());

        invocationHandler = ProxyInvocationHandler.createInvocationHandler(ExampleConfig.class, configSource,
                valueParserFactory);
    }

    @Test
    public void retrievesStringProperty() throws Throwable {
        assertEquals("foo", invoke("getAString"));
    }

    @Test
    public void retrievesBoxedIntegerProperty() throws Throwable {
        assertEquals(10, invoke("getABoxedInteger"));
    }

    @Test
    public void retrievesPrimitiveIntegerProperty() throws Throwable {
        assertEquals(15, invoke("getAPrimitiveInteger"));
    }

    @Test
    public void retrievesArrayOfBoxedInteger() throws Throwable {
        assertArrayEquals(new Integer[]{ 7 }, (Integer[]) invoke("getAnArrayOfBoxedIntegers"));
    }

    @Test
    public void retrievesArrayOfPrimitiveIntegers() throws Throwable {
        assertArrayEquals(new int[] { 1, 2, 3 }, (int[]) invoke("getAnArrayOfPrimitiveIntegers"));
    }

    @Test
    public void retrievesDefaultValue() throws Throwable {
        assertEquals("default value", invoke("defaultedString"));
    }

    @Test
    public void retrievesValueWithCustomName() throws Throwable {
        assertEquals("bar", invoke("stringWithCustomName"));
    }

    @Test
    public void retrievesOptionalNonNullValue() throws Throwable {
        assertEquals(5, invoke("optionalNonNull"));
    }

    @Test
    public void retrievesOptionalNullValue() throws Throwable {
        assertNull(invoke("optionalNull"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsMissingParameter() {
        ConfigSource emptyConfigSource = new DummyConfigSource();
        ProxyInvocationHandler.createInvocationHandler(ExampleConfig.class, emptyConfigSource, valueParserFactory);
    }

    @Test(expected = UnsupportedTypeException.class)
    public void rejectsUnsupportedType() {
        ValueParserFactory emptyValueParserFactory = new ValueParserFactory();
        ProxyInvocationHandler.createInvocationHandler(ExampleConfig.class, configSource, emptyValueParserFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsCombinationOfOptionalAnnotationWithPrimitiveReturnType() {
        ProxyInvocationHandler.createInvocationHandler(BadConfigCombiningOptionalWithPrimitive.class, configSource,
                valueParserFactory);
    }

    @Test
    public void toStringMethodFormatsUsingMethodNamesInSourceOrder() throws Throwable {
        Object expectedFormat = "{" +
                "getAString=foo, " +
                "getABoxedInteger=10, " +
                "getAPrimitiveInteger=15, " +
                "getAnArrayOfBoxedIntegers=[7], " +
                "getAnArrayOfPrimitiveIntegers=[1, 2, 3], " +
                "defaultedString=default value, " +
                "stringWithCustomName=bar, " +
                "optionalNonNull=5, " +
                "optionalNull=null" +
                "}";
        assertThat(invoke(Object.class.getMethod("toString")), is(expectedFormat));
    }

    @Test(expected = IllegalArgumentException.class)
    public void overriddenObjectMethodsAreRejected() {
        DummyConfigSource configSource = new DummyConfigSource().add("to.string", "bad");
        ProxyInvocationHandler.createInvocationHandler(BadConfigWithOverriddenObjectMethod.class, configSource,
                valueParserFactory);
    }

    private Object invoke(String methodName) throws Throwable {
        return invoke(ExampleConfig.class.getMethod(methodName));
    }

    private Object invoke(Method method) throws Throwable {
        return invocationHandler.invoke(null, method, new Object[] {});
    }
}
