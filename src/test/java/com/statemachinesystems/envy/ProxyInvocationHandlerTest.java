package com.statemachinesystems.envy;

import com.statemachinesystems.envy.example.DummyConfigSource;
import com.statemachinesystems.envy.parsers.IntegerValueParser;
import com.statemachinesystems.envy.parsers.ObjectAsStringValueParser;
import com.statemachinesystems.envy.parsers.StringValueParser;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.containsString;
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

        Object methodWithObjectReturnType();
    }

    public interface SubInterfaceConfig extends ExampleConfig {
        String getAString();
        int intInSubInterface();
    }

    public interface AnotherExampleConfig {
        String getAnotherString();
    }

    public interface SubSubInterfaceConfig extends SubInterfaceConfig, AnotherExampleConfig {
        int intInSubSubInterface();
    }

    public interface BadConfigCombiningOptionalWithPrimitive {
        @Optional
        int notOptional();
    }

    public interface BadConfigWithOverriddenObjectMethod {
        @Override
        String toString();
    }

    public interface BadConfigWithVoidReturnType {
        void methodWithVoidReturnType();
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
                .add("OPTIONAL_NON_NULL", "5")
                .add("METHOD_WITH_OBJECT_RETURN_TYPE", "bar")
                .add("INT_IN_SUB_INTERFACE", "37")
                .add("INT_IN_SUB_SUB_INTERFACE", "98")
                .add("ANOTHER_STRING", "baz");

        valueParserFactory = new ValueParserFactory(new StringValueParser(), new IntegerValueParser(),
                new ObjectAsStringValueParser());

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
    public void toStringMethodFormatsUsingMethodNames() throws Throwable {
        String format = (String) invoke(Object.class.getMethod("toString"));

        String[] expectedParts = {
                "getAString=foo",
                "getABoxedInteger=10",
                "getAPrimitiveInteger=15",
                "getAnArrayOfBoxedIntegers=[7]",
                "getAnArrayOfPrimitiveIntegers=[1, 2, 3]",
                "defaultedString=default value",
                "stringWithCustomName=bar",
                "optionalNonNull=5",
                "optionalNull=null",
                "methodWithObjectReturnType=bar",
        };

        for (String part : expectedParts) {
            assertThat(format, containsString(part));
        }

        int expectedLength = "{".length();
        for (int i = 0; i < expectedParts.length; i++) {
            if (i > 0) {
                expectedLength += ", ".length();
            }
            expectedLength += expectedParts[i].length();
        }
        expectedLength += "}".length();

        assertThat(format.length(), is(expectedLength));
    }

    @Test(expected = IllegalArgumentException.class)
    public void overriddenObjectMethodsAreRejected() {
        DummyConfigSource configSource = new DummyConfigSource().add("to.string", "bad");
        ProxyInvocationHandler.createInvocationHandler(BadConfigWithOverriddenObjectMethod.class, configSource,
                valueParserFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void voidReturnTypesAreRejected() {
        DummyConfigSource configSource = new DummyConfigSource().add("method.with.void.return.type", "bad");
        ValueParserFactory valueParserFactory = new ValueParserFactory(new ValueParser<Void>() {
            @Override
            public Void parseValue(String value) {
                return null;
            }

            @Override
            public Class<Void> getValueClass() {
                return Void.class;
            }
        });
        ProxyInvocationHandler.createInvocationHandler(BadConfigWithVoidReturnType.class, configSource,
                valueParserFactory);
    }

    @Test
    public void subInterfaceMethodsAreAvailableUsingSubInterface() throws Throwable {
        Method subInterfaceMethod = SubInterfaceConfig.class.getMethod("intInSubInterface");

        ProxyInvocationHandler handler =
            ProxyInvocationHandler.createInvocationHandler(SubInterfaceConfig.class, configSource, valueParserFactory);

        Integer returnValue = (Integer) invoke(handler, subInterfaceMethod);
        assertThat(returnValue, is(37));
    }

    @Test
    public void superInterfaceMethodsAreAvailableUsingSubInterface() throws Throwable {
        ProxyInvocationHandler handler =
                ProxyInvocationHandler.createInvocationHandler(SubInterfaceConfig.class, configSource, valueParserFactory);

        Integer returnValue = (Integer) invoke(handler, "getABoxedInteger");
        assertThat(returnValue, is(10));
    }

    @Test
    public void duplicatedMethodsAreAvailableUsingSubInterface() throws Throwable {
        ProxyInvocationHandler handler =
                ProxyInvocationHandler.createInvocationHandler(SubInterfaceConfig.class, configSource, valueParserFactory);

        String returnValueUsingSuperMethod = (String) invoke(handler, ExampleConfig.class, "getAString");
        assertThat(returnValueUsingSuperMethod, is("foo"));

        String returnValueUsingSubMethod = (String) invoke(handler, SubInterfaceConfig.class, "getAString");
        assertThat(returnValueUsingSubMethod, is("foo"));
    }

    @Test
    public void subSubInterfaceMethodsAreAvailableUsingSubSubInterface() throws Throwable {
        Method subSubInterfaceMethod = SubSubInterfaceConfig.class.getMethod("intInSubSubInterface");

        ProxyInvocationHandler handler =
                ProxyInvocationHandler.createInvocationHandler(SubSubInterfaceConfig.class, configSource, valueParserFactory);

        Integer returnValue = (Integer) invoke(handler, subSubInterfaceMethod);
        assertThat(returnValue, is(98));
    }

    @Test
    public void superInterfaceMethodsAreAvailableUsingSubSubInterface() throws Throwable {
        ProxyInvocationHandler handler =
                ProxyInvocationHandler.createInvocationHandler(SubSubInterfaceConfig.class, configSource, valueParserFactory);

        String returnValue = (String) invoke(handler, AnotherExampleConfig.class, "getAnotherString");
        assertThat(returnValue, is("baz"));
    }

    private Object invoke(String methodName) throws Throwable {
        return invoke(invocationHandler, methodName);
    }

    private Object invoke(Method method) throws Throwable {
        return invoke(invocationHandler, method);
    }

    private Object invoke(ProxyInvocationHandler handler, String methodName) throws Throwable {
        return invoke(handler, ExampleConfig.class, methodName);
    }

    private Object invoke(ProxyInvocationHandler handler, Class<?> configClass, String methodName) throws Throwable {
        return invoke(handler, configClass.getMethod(methodName));
    }

    private Object invoke(ProxyInvocationHandler handler, Method method) throws Throwable {
        return handler.invoke(null, method, new Object[] {});
    }
}
